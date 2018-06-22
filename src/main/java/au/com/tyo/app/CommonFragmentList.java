package au.com.tyo.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import au.com.tyo.android.widget.CommonListView;

/**
 *
 */

public class CommonFragmentList extends CommonFragment {

    private CommonListView commonListView;

    private ViewGroup extraViewTopContainer;

    private ViewGroup extraViewBottomContainer;

    public CommonListView getCommonListView() {
        return commonListView;
    }

    public void setCommonListView(CommonListView commonListView) {
        this.commonListView = commonListView;
    }

    public ListView getListView() {
        return commonListView.getListView();
    }

    @Override
    protected void onFragmentAttach(Context context) {
        super.onFragmentAttach(context);

        setupContentViewResource();
    }

    protected void setupContentViewResource() {
        getPageFragment().setContentViewResId(R.layout.frame_list);
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView(view);
    }

    protected void setupListView(View view) {
        commonListView = (CommonListView) view.findViewById(R.id.common_list_view);
        extraViewBottomContainer = (ViewGroup) view.findViewById(R.id.list_extra_bottom);
        extraViewTopContainer = (ViewGroup) view.findViewById(R.id.list_extra_top);
    }

    public void addExtraTopView(int resId) {
        extraViewTopContainer.addView(LayoutInflater.from(getActivity()).inflate(resId, null));
    }

    public void addExtraBottomView(int resId) {
        extraViewBottomContainer.addView(LayoutInflater.from(getActivity()).inflate(resId, null));
    }

    public View findViewFromBottomContainer(int resId) {
        return extraViewBottomContainer.findViewById(resId);
    }

    public View findViewFromTopContainer(int resId) {
        return extraViewTopContainer.findViewById(resId);
    }

}
