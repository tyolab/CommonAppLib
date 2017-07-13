package au.com.tyo.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.tyo.app.ui.CommonFragmentView;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 25/6/17.
 */

public abstract class CommonFragment extends Fragment {

    private static final String TAG = "FragmentShared";

    private int parentContainerHeight = -1;

    /**
     * UI widgets
     */

    private int fragmentResId = R.layout.common_frame_for_fragment;
    protected int contentViewResId = -1;
    protected boolean ready;

    protected TextView tvTitle = null;

    private CommonFragmentView fragmentView;
    private ViewGroup contentContainer;
    private View contentView;

    /**
     * Data for bindings
     */

    private String title = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getParentContainerHeight() {
        return parentContainerHeight;
    }

    public void setParentContainerHeight(int parentContainerHeight) {
        this.parentContainerHeight = parentContainerHeight;
    }

    public ViewGroup getContentContainer() {
        return contentContainer;
    }

    public CommonFragmentView getFragmentView() {
        return fragmentView;
    }

    public View getContentView() {
        return contentView;
    }

    public void setFragmentResId(int fragmentResId) {
        this.fragmentResId = fragmentResId;
    }

    public void setContentViewResId(int contentViewResId) {
        this.contentViewResId = contentViewResId;
    }

    public void checkLocationOnScreen() {
        fragmentView.checkLocationOnScreen();
    }

    protected void initialiseTitle() {
        if (title != null && tvTitle != null) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragmentResId == -1)
            throw new IllegalArgumentException("Fragment resource name must be set");
        else if (fragmentResId == 0)
            return super.onCreateView(inflater, container, savedInstanceState);

        fragmentView = (CommonFragmentView) inflater.inflate(fragmentResId,
                container, false);


        contentContainer = (ViewGroup) fragmentView.findViewById(R.id.frame_fragment_content);
        tvTitle = (TextView) fragmentView.findViewById(R.id.frame_fragment_title);

        if (contentViewResId > -1) {
            contentView = inflater.inflate(contentViewResId,
                    contentContainer, true);
        }

        initialiseTitle();

        //fragmentView.getViewTreeObserver().addOnGlobalFocusChangeListener(controller.getUi().getOnGolbalFocusChangeListener());
        /**
         * All the UI elements are created
         */
        onFragmentReady();

        return fragmentView;
    }

    public boolean shallDisplay() {
        return true;
    }

    protected void showOrHide() {
        android.support.v4.app.FragmentManager fm = getFragmentManager();

        if (null != fm) {
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

            if (shallDisplay()) {
                onShowing();
                transaction.show(this);
            } else {
                onHiding();
                transaction.hide(this);
            }

            transaction.commit();
        }
    }

    private void onHiding() {
        // do nothing
    }

    private void onShowing() {
        // do nothing
    }

    protected void onFragmentReady() {
        ready = true;
    }
}
