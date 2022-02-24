package com.example.supportingpanelcanonicallayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.ReactiveGuide;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptiveSupportingPanelFragment extends Fragment {

    private ConstraintLayout fragmentContainer;
    private ConstraintSet portraitLayout;
    private ConstraintSet landscapeLayout;
    private ReactiveGuide guideline;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater layoutInflater,
            @Nullable ViewGroup viewGroup,
            @Nullable Bundle bundle) {
        View view =
                layoutInflater.inflate(R.layout.fragment_main, viewGroup, false);
        guideline = view.findViewById(R.id.horizontal_fold);
        RecyclerView supportingPanelList = view.findViewById(R.id.supporting_panel_side_container);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        supportingPanelList.setLayoutManager(layoutManager);
        PanelAdapter adapter = new PanelAdapter();
        supportingPanelList.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(supportingPanelList, /* enabled= */ false);

        // Set up constraint sets.
        fragmentContainer = view.findViewById(R.id.supporting_panel_container);
        portraitLayout = new ConstraintSet();
        portraitLayout.clone(fragmentContainer);
        landscapeLayout = getLandscapeLayout(fragmentContainer);

        return view;
    }

    /* Applies the portrait layout configuration. */
    void updatePortraitLayout() {
        portraitLayout.applyTo(fragmentContainer);
    }

    /* Applies the landscape layout configuration. */
    void updateLandscapeLayout() {
        landscapeLayout.applyTo(fragmentContainer);
    }

    /**
     * Applies the table top layout configuration.
     *
     * @param foldPosition position of the fold
     * @param foldWidth width of the fold if it's a hinge
     */
    void updateTableTopLayout(int foldPosition, int foldWidth) {
        ConstraintSet tableTopLayout = getTableTopLayout(portraitLayout, foldWidth);
        tableTopLayout.applyTo(fragmentContainer);
        guideline.setGuidelineBegin(foldPosition);
    }

    /* Returns the constraint set to be used for the landscape layout configuration. */
    private ConstraintSet getLandscapeLayout(@NonNull ConstraintLayout constraintLayout) {
        int marginVertical =
                getResources().getDimensionPixelOffset(R.dimen.margin_vertical);
        int marginHorizontal =
                getResources().getDimensionPixelOffset(R.dimen.margin_horizontal);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        // Main content.
        constraintSet.connect(
                R.id.supporting_panel_main_content,
                ConstraintSet.END,
                R.id.supporting_panel_side_container,
                ConstraintSet.START);
        constraintSet.connect(
                R.id.supporting_panel_main_content,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        constraintSet.setMargin(R.id.supporting_panel_main_content, ConstraintSet.TOP, marginVertical);
        constraintSet.setMargin(
                R.id.supporting_panel_main_content, ConstraintSet.BOTTOM, marginVertical);
        constraintSet.setMargin(
                R.id.supporting_panel_main_content, ConstraintSet.END, marginHorizontal);
        constraintSet.constrainMinHeight(R.id.supporting_panel_main_content, 0);
        // Supporting panel content.
        constraintSet.connect(
                R.id.supporting_panel_side_container,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        constraintSet.connect(
                R.id.supporting_panel_side_container,
                ConstraintSet.START,
                R.id.supporting_panel_main_content,
                ConstraintSet.END);
        constraintSet.constrainPercentWidth(R.id.supporting_panel_side_container, 0.4f);

        return constraintSet;
    }

    /* Returns the constraint set to be used for the table top layout configuration. */
    private ConstraintSet getTableTopLayout(@NonNull ConstraintSet portraitLayout, int foldWidth) {
        int marginVertical =
                getResources().getDimensionPixelOffset(R.dimen.margin_vertical);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(portraitLayout);
        constraintSet.setVisibility(R.id.horizontal_fold, View.VISIBLE);
        // Main content
        constraintSet.connect(
                R.id.supporting_panel_main_content,
                ConstraintSet.BOTTOM,
                R.id.horizontal_fold,
                ConstraintSet.TOP);
        constraintSet.setMargin(
                R.id.supporting_panel_main_content, ConstraintSet.BOTTOM, marginVertical);
        constraintSet.constrainMinHeight(R.id.supporting_panel_main_content, 0);

        // Supporting panel content
        constraintSet.connect(
                R.id.supporting_panel_side_container,
                ConstraintSet.TOP,
                R.id.horizontal_fold,
                ConstraintSet.BOTTOM,
                marginVertical + foldWidth);

        return constraintSet;
    }

    /** A RecyclerView adapter for the side content list of the supporting panel demo. */
    private static final class PanelAdapter
            extends RecyclerView.Adapter<PanelAdapter.PanelViewHolder> {

        PanelAdapter() {}

        /** Provides a reference to the views for each data item. */
        static class PanelViewHolder extends RecyclerView.ViewHolder {
            public PanelViewHolder(@NonNull View view) {
                super(view);
            }
        }

        @NonNull
        @Override
        public PanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item, parent, false);
            return new PanelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PanelViewHolder holder, int position) {
            // Populate content. Empty for demo purposes.
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}

