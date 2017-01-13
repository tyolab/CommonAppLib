package au.com.tyo.app.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import au.com.tyo.app.Controller;
import au.com.tyo.app.Request;
import au.com.tyo.app.ui.SearchInputView.SearchStateListener;
import au.com.tyo.app.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */
public class SearchView extends LinearLayout implements
 	OnClickListener, SearchInputView.SearchInputListener, SearchInputView.SearchStateListener {

	protected SearchInputView searchInputView;
    
    protected Controller controller;
    
//    protected TitleBar titleBar;
    
//    protected ImageButton ibMainMenu;
    
//    protected ImageButton ibBookmarks;
    protected ImageButton ibSearch;
    protected ImageButton ibClearSearch;
//    protected ImageButton ibVoiceSearch;
    
    private Context context;
    
	InputMethodManager inputManager;
	
//	private WikiCorpusList corpusList;
//    protected View decedant = null;
	
	public interface SearchViewController {
		
	}
    
	public SearchView(Context context) {
		super(context);
		init(context);
	}

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	protected void init(Context context) {
		// initialize the components in onFinishInflate()
//		controller = WikieTalkieApp.getInstance();
		this.context = context;
		
//        LayoutInflater factory = LayoutInflater.from(context);
//        factory.inflate(R.layout.search_frame, this);
        
//        initilizeComponents();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
        setupComponents();
	}

	public void setupComponents() {
        ibSearch = (ImageButton) findViewById(R.id.search_go_btn);
        ibClearSearch = (ImageButton) findViewById(R.id.btn_clear_search);
		
		inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		
		searchInputView = (SearchInputView) findViewById(R.id.search_input_view);
//		searchInputView.setSearchStateListener(this);
		searchInputView.setSelectAllOnFocus(true);
		searchInputView.setSearchInputListener(this);
		searchInputView.setSearchStateListener(this);
		
		searchInputView.setParent(this);
		
//		ibMainMenu = (ImageButton) findViewById(R.id.main_menu_btn);
//		ibBookmarks = (ImageButton) findViewById(R.id.bookmarks_btn);
//		ibSearch = (ImageButton) findViewById(R.id.search_go_btn);
//		ibVoiceSearch = (ImageButton) findViewById(R.id.search_voice_btn);
		
//		ibVoiceSearch.setOnClickListener(this);
		ibSearch.setOnClickListener(this);
		ibClearSearch.setOnClickListener(this);
		
//		ibBookmarks.setOnClickListener(this);
//		ibMainMenu.setOnClickListener(this);
	}
	
	public void setupComponents(Controller theController) {
		this.setController(theController);
		
		searchInputView.setupComponents(theController);
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public void invokeSearch(String query) {
		controller.search(query);
	}

	public void onClick(View v) {
		if (v == ibSearch) {
			String keywords = getText();
			
			if (keywords.length() == 0) {
				searchInputView.requestFocus();
				return;
			}
			
			searchInputView.onSearch(keywords, Request.FROM_SEARCH_BUTTON);
		}
		else if (v == ibClearSearch) {
			searchInputView.onClearInput();
			showClearSearchButton(false);
		}
//		if (v == ibMainMenu) {
//			/*
//			 * TODO
//			 * 1. press the main menu quickly copy from clip board and search it
//			 * 2. press the main menu button more than 1s
//			 */
//			String query = controller.getTextFromClipboard();
//			query.trim();
//			if (query.length() > 0) {
//				this.invokeSearch(query);
//				this.setSearchText(query);
//				
//				controller.clearClipboard();
//			}
//			else {
////				controller.onSearchRequested();
//				corpusList.show(v);
//			}
			
//		}
//		else if (v == ibBookmarks){
//			
//		}
//		else if (v == ibVoiceSearch){
////			controller.startVoiceRecognizer();
//		}
	}

	private void clearInput() {
		setText("");
	}

	public void onStateChanged(int state) {
        switch(state) {
        case SearchStateListener.SEARCH_NORMAL:
        	if (getText().length() > 0)
        		this.showClearSearchButton(true);
        	else
        		this.showClearSearchButton(false);
//        	ibSearch.setVisibility(View.GONE);
//        	ibBookmarks.setVisibility(View.GONE);
//        	ibVoiceSearch.setVisibility(View.VISIBLE);
            break;
        case SearchStateListener.SEARCH_HIGHLIGHTED:
//        	ibSearch.setVisibility(View.GONE);
        	this.showClearSearchButton(true);
//        	ibBookmarks.setVisibility(View.GONE);
//        	ibVoiceSearch.setVisibility(View.VISIBLE);
            break;
        case SearchStateListener.SEARCH_EDITED:
//        	ibSearch.setVisibility(View.VISIBLE);
        	this.showClearSearchButton(false);
//        	ibBookmarks.setVisibility(View.GONE);
//        	ibVoiceSearch.setVisibility(View.GONE); //setVoiceSearch(false);
            break;
        }
	}
	
	public void setVoiceSearch(boolean value) {
//		ibVoiceSearch.setVisibility(value ? View.VISIBLE : View.GONE);
	}

	public String getText() {
		return searchInputView.getInputText();
	}

	public void showSoftInput() {
		inputManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public void hideSoftInput() {
		/*
		 * Hide the keyboard after losing focus
		 */
		inputManager.hideSoftInputFromWindow(getWindowToken(), 0/*InputMethodManager.HIDE_IMPLICIT_ONLY*/); 
	}

	public void onSuggestionClick(String text, int from) {
		controller.getInputManager().insert(text);
		
		searchInputView.setText("");
//		searchInputView.clearFocus();
		
		requestFocusForSearchButton();
		
		controller.getUi().setSuggestionViewVisibility(false);
		
		controller.search(text);
	}

	public void setSearchText(String query) {
		searchInputView.setText(query);
	}

	public void setText(String speech) {
		if (speech.trim().length() == 0)
			TextKeyListener.clear(searchInputView.getText());
		searchInputView.setText(speech);
	}

//	public void setOnSearchListener(OnClickListener listener) {
//		ibSearch.setOnClickListener(listener);
//	}

	public SearchInputView getSearchInputView() {
		return searchInputView;
	}

	public boolean requestSearchInputViewFocus() {
		return searchInputView.requestFocus();
	}

	private void showClearSearchButton(boolean b) {
		if (b) {
			ibSearch.setVisibility(View.GONE);
			ibClearSearch.setVisibility(View.VISIBLE);
		}
		else {
			ibClearSearch.setVisibility(View.GONE);
			ibSearch.setVisibility(View.VISIBLE);
		}
	}
	
	public void requestFocusForSearchButton() {
		ibSearch.requestFocus();
		clearFocusForSearchInput();
	}

	public void clearFocusForSearchInput() {
		searchInputView.clearFocus();
	}
}
