package au.com.tyo.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.tyo.android.utils.ListViewItemAdapter;
import au.com.tyo.common.ui.CommonListView;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class SearchResultView extends CommonListView {
	
	private TextView headerTextForSearchResults;

	public SearchResultView(Context context) {
		super(context);
	}

	public SearchResultView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public SearchResultView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		/*TODO
		 * uncomment off these
		 */
//		addContentView(R.layout.search_result_content);
//		this.showProgressBar();
		
		list = (ListView) findViewById(R.id.lv_any_list);
		
		headerTextForSearchResults  = (TextView) findViewById(R.id.tv_search_result_info); 
//		expandableList = (ExpandableListView) findViewById(R.id.lv_any_list_expandable);
	}
	
//	private void showList() {
//		expandableList.setVisibility(View.GONE);
//		list.setVisibility(View.VISIBLE);
//	}
//	
//	private void showExpandableList() {
//		list.setVisibility(View.GONE);
//		expandableList.setVisibility(View.VISIBLE);
//	}
//
//	public void display(final Request request) {
//		/*
//		 * has to make a new copy, otherwise the request result items constantly get changed
//		 */
//		final List<WikiSearch> results = request.getResults(); //new ArrayList<WikiSearch>(request.getResults());
//		
//		ListViewItemAdapter<?> oldAdapter = (ListViewItemAdapter<?>) list.getAdapter();
//		if (oldAdapter != null ) oldAdapter.clear();
//		
//		if (results.size() > 0) {
//			WikiPage currentPage = controller.getCurrentPage();
//			int count = 0;
//			do {
//				if (currentPage != null && currentPage.getTitle() != null &&
//						(currentPage.getTitle().equalsIgnoreCase(results.get(0).getTitle())))
//					results.remove(0);
//			}
//			while (count++ < 2 && results.size() > 0);
//			
//			adapter = new SearchResultAdapter(R.layout.image_text_list_cell_for_search, results, controller, request.getQuery());
//			list.setAdapter(adapter);
//			adapter.notifyDataSetChanged();
//			list.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					WikiSearch ws = results.get(position);
//					controller.search(ws.getTitle(), ws.getDomain(), Request.FROM_SEARCH_RESULT, controller.getSettings().isCrosslinkOn());
//				}
//				
//			});
//			
//			headerTextForSearchResults.setVisibility(View.GONE);
//			list.setVisibility(View.VISIBLE);
//		}
//		else {
//			list.setVisibility(View.GONE);
//			headerTextForSearchResults.setVisibility(View.VISIBLE);
//		}
//		
//	}
	
	public void setSearchResultHeaderVisibility() {
		int count = getAdapter() == null ? 0 : getAdapter().getCount();
		headerTextForSearchResults.setVisibility(count == 0 ? VISIBLE : GONE);
	}
}
