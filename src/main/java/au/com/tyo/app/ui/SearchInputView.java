package au.com.tyo.app.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import au.com.tyo.app.Controller;
import au.com.tyo.app.Request;
import au.com.tyo.app.adapter.SuggestionsAdapter;
import au.com.tyo.app.adapter.SuggestionsAdapter.CompletionListener;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class SearchInputView extends AppCompatEditText /*AutoCompleteTextView*/ implements OnEditorActionListener,
	CompletionListener, TextWatcher, Filter.FilterListener {
	
	private SearchStateListener searchStateListener;
	private SearchInputListener inputListener;
	
	private Context context;
	
	InputMethodManager imManager;
	private SuggestionsAdapter adapter;
	
    private Filter filter;
	
	private int state;
	
	private boolean softInputHidden;
	
	private Controller controller;
	
	private String lastInput = "";
	
	private SearchView parent;
	
	private boolean keepShowingSuggestionView;
	
	interface SearchStateListener {
	    static final int SEARCH_NORMAL = 0;
	    static final int SEARCH_HIGHLIGHTED = 1;
	    static final int SEARCH_EDITED = 2;

	    public void onStateChanged(int state);
	}
	
    interface SearchInputListener {

//        public void onDismiss();
//
//        public void onAction(String text, String extra, String source);

        public void onSuggestionClick(String text, int from);
    }

	public SearchInputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		
		init();
	}

	public SearchInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		init();
	}

	public SearchInputView(Context context) {
		super(context);
		this.context = context;
		
		init();
	}

	private void init() {
		adapter = null;
		imManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
        
		keepShowingSuggestionView = false;
		
//        setOnItemClickListener(this);
        addTextChangedListener(this);
        
        setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int arg1, KeyEvent event) {
            	switch (event.getKeyCode()) {
		        	case KeyEvent.KEYCODE_SEARCH:
		        	case KeyEvent.KEYCODE_ENTER:
		        		if (SearchInputView.this.getInputText().length() > 0)
		        		onSearch(getInputText().toString(), Request.FROM_SEARCH_BUTTON);
		                return true;
		            default:
		            	break;
	            }
                return false;
            }

        });
        
        setOnEditorActionListener(this);

        state = SearchStateListener.SEARCH_NORMAL;
        softInputHidden = true;
        filter = null;
        lastInput = "";
        adapter = null;
        controller = null;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setSearchStateListener(SearchStateListener listener) {
		this.searchStateListener = listener;
	}
	
	public void setupComponents(Controller theController) {
		setController(theController);
	}

	@Override
	protected void onFocusChanged(final boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);

		final UI ui = controller.getUi();
		
        if (focused) { 	    	
        	controller.onSearchInputFocused();

        	showSoftInput();
        	
        	if (lastInput == null || lastInput.length() == 0)
        		lastInput = controller.getInputManager().getFirst();
        	
        	if (lastInput != null && lastInput.length() > 0) {
        		setText(lastInput);
        	}

            if (hasSelection()) {
                state = SearchStateListener.SEARCH_HIGHLIGHTED;
            } 
            else {

            		state = SearchStateListener.SEARCH_NORMAL;
            }
        } 
        else {
            hideSoftInput();

        	controller.onSearchInputFocusEscaped();
        	
        	if (!this.keepShowingSuggestionView)
        		ui.getCurrentPage().setSuggestionViewVisibility(false);

        	setText("");
        }
        final int s = state;
        post(new Runnable() {
            public void run() {
            	/*
            	 * change the drawer icon to < 
            	 */
            	ui.getCurrentPage().onSearchInputFocusStatus(focused);
            		
                changeState(s);
            }
        });
	}
	
	public void showSoftInput() {
		/**
		 * other options: InputMethodManager.SHOW_IMPLICIT, InputMethodManager.SHOW_FORCED
		 */

        Log.d("isAcceptingText", "..." + imManager.isAcceptingText());
        Log.d("isActive", "..." + imManager.isActive());
        Log.d("isActive(this)", "..." + imManager.isActive(this));
        Log.d("isWatchingCursor(this)", "..." + imManager.isWatchingCursor(this));

		// imManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
		imManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
		softInputHidden = false;
	}
	
	public void hideSoftInput() {
		/**
		 * Hide the keyboard after losing focus
		 *
		 * other options:  InputMethodManager.HIDE_NOT_ALWAYS, InputMethodManager.HIDE_IMPLICIT_ONLY
		 */
		imManager.hideSoftInputFromWindow(getWindowToken(), 0);
		softInputHidden = true;
	}

	protected void changeState(int s) {
		state = s;
		if (searchStateListener != null)
			searchStateListener.onStateChanged(state);
	}
	
	@Override
	public void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
        if (SearchStateListener.SEARCH_HIGHLIGHTED == state || SearchStateListener.SEARCH_NORMAL == state) {
            changeState(SearchStateListener.SEARCH_EDITED);
        }
	}

	public void afterTextChanged(Editable arg0) {
		String text = getText().toString().trim();
		if (text.length() > 0 && (null == lastInput || !lastInput.equals(text))) {
			lastInput = text;
			if (controller != null &&  controller.getUi() != null && controller.getUi().getCurrentPage().getMainView() != null)
				controller.getUi().getCurrentPage().getSuggestionView().restoreAdapter();
			if (filter != null) filter.filter(text, this);
		}
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	public void onSearch(String txt, int from) {
		hideSoftInput();
		
		lastInput = txt;
		
		controller.getInputManager().insert(txt);
				
		this.inputListener.onSuggestionClick(txt, from);
	}

	public void setSearchInputListener(SearchInputListener listener) {
		this.inputListener = listener;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    	switch (actionId) {
    	case EditorInfo.IME_ACTION_GO:
    	case EditorInfo.IME_ACTION_DONE:
    	case EditorInfo.IME_ACTION_SEND:
    	case EditorInfo.IME_ACTION_SEARCH:
            onSearch(v.getText().toString(), Request.FROM_SEARCH_BUTTON);
            return true;
        default:
        	break;
    }
    return false;
	}
	
	public String getInputText() {
		return getText().toString();
	}
	
	@Override
	public boolean onKeyPreIme (int keyCode, KeyEvent event) {
        switch(keyCode) {
        case KeyEvent.KEYCODE_BACK:
//        	if (controller.hasShowedAllSuggestions()) {
//        		parent.requestFocusForSearchButton();
//        		clearFocus();
//        	}
        	
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.handleUpEvent(event);
                }
                if (event.isTracking() && !event.isCanceled()) {
                	parent.requestFocusForSearchButton();
//                	controller.getUi().onSearchInputFocusStatus(false);
                	controller.getUi().getCurrentPage().setSuggestionViewVisibility(false);
                    return true;
                }
            }
            break;
        }
		
		return super.onKeyPreIme(keyCode, event);
	}

	public void setParent(SearchView searchView) {
		parent = searchView;
	}

	@Override
	public void onFilterComplete(int count) {
	}
	
    protected Filter getFilter() {
        return filter;
    }
	
    @SuppressWarnings({ "UnusedDeclaration" })
    protected void performFiltering(CharSequence text, int keyCode) {
        filter.filter(text, this);
    }
    
    public  void setAdapter(SuggestionsAdapter adapter) {
        this.adapter = adapter;
		adapter.addCompletionListener(this);
		adapter.createNewFilter();
		
		filter = adapter.getFilter();
    }

	public void setKeepShowingSuggestionViewEvenLosingFocus(
			boolean keepShowingSuggestionView) {
		this.keepShowingSuggestionView = keepShowingSuggestionView;
	}

	public void onClearInput() {
		/*
		 * this will cause not accepting input, which is OS bug, see
		 * 
		 * http://stackoverflow.com/questions/9069803/edittext-does-not-show-current-input-android-4
		 */
//		this.setText("");
		TextKeyListener.clear(this.getText());
		adapter.clear();
		controller.displayHistory();
	}
}
