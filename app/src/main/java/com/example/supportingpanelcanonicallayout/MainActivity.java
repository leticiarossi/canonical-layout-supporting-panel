package com.example.supportingpanelcanonicallayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import java.util.List;
import java.util.concurrent.Executor;

/** An Activity which hosts the Adaptive feed flow. */
public class MainActivity extends AppCompatActivity {

    private AdaptiveSupportingPanelFragment fragment;
    @Nullable private WindowInfoTrackerCallbackAdapter windowInfoTracker;
    private final Consumer<WindowLayoutInfo> stateContainer = new StateContainer();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Executor executor = command -> handler.post(() -> handler.post(command));
    private Configuration configuration;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        windowInfoTracker =
                new WindowInfoTrackerCallbackAdapter(WindowInfoTracker.getOrCreate(this));
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView modalNavDrawer = findViewById(R.id.modal_nav_drawer);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationRailView navRail = findViewById(R.id.nav_rail);
        NavigationView navDrawer = findViewById(R.id.nav_drawer);
        ExtendedFloatingActionButton navFab = findViewById(R.id.nav_fab);
        configuration = getResources().getConfiguration();
        fragment = new AdaptiveSupportingPanelFragment();

        // Update navigation views according to screen width size.
        int screenWidth = configuration.screenWidthDp;
        AdaptiveUtils.updateNavigationViewLayout(
                screenWidth,
                drawerLayout,
                modalNavDrawer,
                /* fab= */ null,
                bottomNav,
                navRail,
                navDrawer,
                navFab);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (windowInfoTracker != null) {
            windowInfoTracker.addWindowLayoutInfoListener(this, executor, stateContainer);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (windowInfoTracker != null) {
            windowInfoTracker.removeWindowLayoutInfoListener(stateContainer);
        }
    }

    private class StateContainer implements Consumer<WindowLayoutInfo> {

        public StateContainer() {}

        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            if (fragment == null) {
                return;
            }
            List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();
            boolean isTableTop = false;
            for (DisplayFeature displayFeature : displayFeatures) {
                if (displayFeature instanceof FoldingFeature) {
                    FoldingFeature foldingFeature = (FoldingFeature) displayFeature;
                    FoldingFeature.Orientation orientation = foldingFeature.getOrientation();
                    if (foldingFeature.getState().equals(FoldingFeature.State.HALF_OPENED)
                            && orientation.equals(FoldingFeature.Orientation.HORIZONTAL)) {
                        // Device is in table top mode.
                        int foldPosition = foldingFeature.getBounds().top;
                        int foldWidth = foldingFeature.getBounds().bottom - foldPosition;
                        fragment.updateTableTopLayout(foldPosition, foldWidth);
                        isTableTop = true;
                    }
                }
            }
            if (!isTableTop) {
                if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Device is in portrait.
                    fragment.updatePortraitLayout();
                } else {
                    // Device is in landscape.
                    fragment.updateLandscapeLayout();
                }
            }
        }
    }

}