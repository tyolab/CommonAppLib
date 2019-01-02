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

import au.com.tyo.app.ui.page.PageFragment;
import au.com.tyo.app.ui.view.CommonFragmentView;
import au.com.tyo.app.ui.UIPage;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 25/6/17.
 */

public abstract class CommonFragment<T extends Controller> extends Fragment {

    private static final String TAG = "CommonFragment";

    /**
     * Controller
     */
    private T controller;

    /**
     * associated UI Page
     */
    private UIPage page;

    /**
     * Page fragment
     */
    private PageFragment pageFragment;

    private int parentContainerHeight = -1;
    private int parentContainerWidth = -1;

    /**
     * UI widgets
     */

    private int fragmentResId = R.layout.common_frame_for_fragment;

    protected boolean ready;

    private TextView tvTitle = null;

    private CommonFragmentView fragmentView;
    private ViewGroup contentContainer;
//    private View contentView;

    /**
     * Data for bindings
     */

    private String title = null;
    private boolean toShow = true;

    public CommonFragment() {
        pageFragment = new PageFragment(this);
    }

    public T getController() {
        return controller;
    }

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

    public TextView getTitleView() {
        return tvTitle;
    }

    public int getParentContainerHeight() {
        return parentContainerHeight;
    }

    public void setParentContainerHeight(int parentContainerHeight) {
        this.parentContainerHeight = parentContainerHeight;
    }

    public int getParentContainerWidth() {
        return parentContainerWidth;
    }

    public void setParentContainerWidth(int parentContainerWidth) {
        this.parentContainerWidth = parentContainerWidth;
    }

    public ViewGroup getContentContainer() {
        return contentContainer;
    }

    public CommonFragmentView getFragmentView() {
        return fragmentView;
    }

    public View getContentView() {
        return getPageFragment().getContentView();
    }

    public void setFragmentResId(int fragmentResId) {
        this.fragmentResId = fragmentResId;
    }

    public void setContentViewResId(int contentViewResId) {
        getPageFragment().setContentViewResId(contentViewResId);
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

    public PageFragment getPageFragment() {
        return pageFragment;
    }

    public void setPageFragment(PageFragment pageFragment) {
        this.pageFragment = pageFragment;
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
        controller = CommonApp.getInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //fragmentView.getViewTreeObserver().addOnGlobalFocusChangeListener(controller.getUi().getOnGolbalFocusChangeListener());
        if (null != page)
            page.addFragmentToList(this);

        setupComponents();

        /**
         * All the UI elements are created
         */
        onFragmentReady();
    }

    protected abstract void setupComponents();

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
        loadContentView();
    }

    protected void loadContentView(LayoutInflater inflater) {
        if (pageFragment.getContentViewResId() > -1) {
            contentContainer.removeAllViews();

//            contentView = inflater.inflate(pageFragment.getContentViewResId(),
//                    contentContainer, false);

            View contentView = pageFragment.loadContentView(inflater);
            if (null != contentView.getParent())
                ((ViewGroup) contentView.getParent()).removeAllViews();

            contentContainer.addView(contentView);
        }
    }

    protected void loadContentView(int contentViewResId) {
        removeContentView();
        pageFragment.setContentViewResId(contentViewResId);

        if (contentViewResId > -1) {
            contentContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            pageFragment.loadContentView(inflater);
            contentContainer.addView(pageFragment.getContentView());
        }
    }

    protected void removeContentView() {
        contentContainer.removeAllViews();
    }

    protected void hideContentView() {
        pageFragment.hideContentView();
    }

    public void addContentView(View contentView) {
        contentContainer.removeAllViews();
        this.contentContainer.addView(contentView);
        pageFragment.setContentView(contentView);
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

    @Override
    public void onResume() {
        super.onResume();
        pageFragment.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pageFragment.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        pageFragment.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        pageFragment.onStop();
    }

    public View findViewById(int resId) {
        return pageFragment.getContentView().findViewById(resId);
    }
}
