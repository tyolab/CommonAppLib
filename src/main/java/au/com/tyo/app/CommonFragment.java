package au.com.tyo.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.tyo.app.ui.CommonFragmentView;
import au.com.tyo.app.ui.UIPage;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 25/6/17.
 */

public abstract class CommonFragment extends Fragment {

    private static final String TAG = "FragmentShared";

    /**
     * UI Page
     */
    UIPage page;

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
    private boolean toShow = true;

    public boolean isToShow() {
        return toShow;
    }

    public void setToShow(boolean toShow) {
        this.toShow = toShow;
    }

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

    public int getContentViewResId() {
        return contentViewResId;
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

    public UIPage getPage() {
        return page;
    }

    public void setPage(UIPage page) {
        this.page = page;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onFragmentAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        onFragmentAttach(activity);
    }

    protected void onFragmentAttach(Context context) {
        // do nothing
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fragmentView.getViewTreeObserver().addOnGlobalFocusChangeListener(controller.getUi().getOnGolbalFocusChangeListener());
        if (null != page)
            page.addFragmentToList(this);

        /**
         * All the UI elements are created
         */
        onFragmentReady();
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

        loadContentView(inflater);

        initialiseTitle();

        return fragmentView;
    }

    protected void loadContentView() {
        loadContentView(LayoutInflater.from(getActivity()));
    }

    protected void loadContentView(LayoutInflater inflater) {
        if (contentViewResId > -1) {
            contentView = inflater.inflate(contentViewResId,
                    contentContainer, true);
        }
    }

    public boolean shallDisplay() {
        return toShow;
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

    public void showOrHide(boolean b) {
        setToShow(b);
        showOrHide();
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
