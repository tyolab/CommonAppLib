package au.com.tyo.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 *
 */

public class CommonFragmentList extends CommonFragment {

    private ListView listView;

    private ViewGroup extraViewTopContainer;

    private ViewGroup extraViewBottomContainer;

    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onFragmentAttach(Context context) {
        super.onFragmentAttach(context);

        setContentViewResId(R.layout.frame_list);
        // setPage(new PageCommonList((Controller) CommonApp.getInstance(), getActivity()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.list_view);
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
