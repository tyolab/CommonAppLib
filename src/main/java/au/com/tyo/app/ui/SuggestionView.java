package au.com.tyo.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.tyo.app.Controller;
import au.com.tyo.app.adapter.SuggestionsAdapter;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.common.ui.CommonListView;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class SuggestionView extends CommonListView implements OnItemClickListener {

	private SuggestionsAdapter adapter;
	
	private Controller controller;

	public SuggestionView(Context context) {
		super(context);
	}

	public SuggestionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public SuggestionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		/*
		 * don't need this, the listview will be created programmatically
		 */
		list = (ListView) findViewById(R.id.lv_suggestion);
	}
	
	public void setupComponents(Controller controller) {
		this.controller = controller;

		if (list == null)
			createListView();
				
		adapter = new SuggestionsAdapter(controller);
		adapter.setKeepOldItems(false);
		
		list.setAdapter(adapter);
		
		if (controller.getUi() != null) {
			SearchInputView inputView = controller.getUi().getSearchInputView();
			inputView.setAdapter(adapter);
			list.setOnItemClickListener(this);
		}
		
		if (list.getChildCount() == 0) {
			controller.displayHistory();
			/*
			 * for debugging the listview
			 */
//			ArrayList<String> listItems=new ArrayList<String>();
//			listItems.add("Test1");
//			listItems.add("Test2");
//			listItems.add("Test3");
//			list.setAdapter(new ArrayAdapter<String>(getContext(),
//		            android.R.layout.simple_list_item_1,
//		            listItems));
		}
	}
	
	public void restoreAdapter() {
		list.setAdapter(adapter);
	}
	
	public SuggestionsAdapter getWikiSuggestionAdapter() {
		return adapter;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Searchable item = adapter.getItem(position);
		controller.getUi().hideSuggestionView();
		controller.open(item);
	}
}
