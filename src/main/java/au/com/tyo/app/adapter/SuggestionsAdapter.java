package au.com.tyo.app.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.tyo.android.images.utils.BitmapUtils;
import au.com.tyo.android.adapter.ListViewItemAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.app.model.DisplayItem;
import au.com.tyo.app.model.Searchable;
import au.com.tyo.common.ui.AutoResizeTextView;

/**
 * 
 * ArrayAdapter will require layout of TextView only
 * so, if other than TextView resource provided will cause exception 
 *             "ArrayAdapter requires the resource ID to be a TextView"
 * 
 */
public class SuggestionsAdapter extends ListViewItemAdapter implements Filterable {
	
	public static final String LOG_TAG = "SuggestionsAdapter";

	public static final List EMPTY = new ArrayList();

    private CompletionListener listener;
	
	private Filter filter;
	
	private Handler handler;
	
	private Controller controller;
	
	private boolean hasToBeBestMatch;

	private boolean keepOriginal;
	
	private CharSequence currentSearch;
	
	private boolean showImage;

	private SuggestionListener suggestionListener;

	/**
	 * The identifier to indicated where the suggestion request from
	 */
	private String requestFromId;

	public interface SuggestionListener {
		List<?> onRequestSuggestions(String query, boolean bestMatch);
	}
	
	public SuggestionsAdapter(Controller controller) {
		super(R.layout.suggestion_list_cell/*au.com.tyo.android.R.layout.simple_dropdown_item_1line*/);
		
		this.controller = controller;
		init();
	}

	public String getRequestFromId() {
		return requestFromId;
	}

	public void setRequestFromId(String requestFromId) {
		this.requestFromId = requestFromId;
	}

	public SuggestionListener getSuggestionListener() {
		return suggestionListener;
	}

	public void setSuggestionListener(SuggestionListener suggestionListener) {
		this.suggestionListener = suggestionListener;
	}

	public boolean toShowImage() {
		return showImage;
	}

	public void setShowImage(boolean showImage) {
		this.showImage = showImage;
	}

	private void init() {
		hasToBeBestMatch = true;
		keepOriginal = false;
		setShowImage(true);

		filter = new SuggestionFilter();
		suggestionListener = null;
	}

    public void setMessageHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler createMessageHandler() {
		handler = new Handler() {

			@SuppressLint("NewApi") 
			@Override
			public void handleMessage(Message msg) {
            if (!handleSuggestionMessage(msg))
			    super.handleMessage(msg);
			}
			
		};
		return handler;
	}

	public boolean handleSuggestionMessage(Message msg) {
	    if (msg.what == Constants.MESSAGE_SUGGESTION_RETURN) {
	        if (null != msg.obj) {
                List<Searchable> list = (List<Searchable>) msg.obj;
                if (keepOriginal) {
                    removeRedundantItem(items, currentSearch.toString());
                    removeRedundantItem(list, currentSearch.toString());
                }
                items = list;
            }
            else
                items.clear();
            notifyDataSetChanged();
	        return true;
        }
        return false;
    }
	
	private void removeRedundantItem(List<Searchable> items, String name) {
		if (items.size() > 0 && name != null && items.contains(name));			
			items.remove(name);
	}

	public interface CompletionListener {
        void onSearch(String txt, int from);
	}
	
	public void addCompletionListener(CompletionListener listener) {
		this.listener = listener;
	}

	public Filter getFilter() {
		return filter;
	}

	public void createNewFilter() {
		filter = new SuggestionFilter();
	}

    class SuggestionTask extends android.os.AsyncTask<CharSequence, Void, List<?>> {
    	
    	private String domain;
    	
    	public SuggestionTask() {
    		this(null);
    	}

        public SuggestionTask(String domain) {
        	this.domain = domain;
        }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Thread.currentThread().setName("SuggestionTask");
		}

		@Override
        protected List<?> doInBackground(CharSequence... params) {
        	String query = params[0].toString();

        	List<?> results = null;
			if (null != suggestionListener)
				results = suggestionListener.onRequestSuggestions(query, hasToBeBestMatch);
			else
				// otherwise we use the default suggestions
				results = controller.getSuggestions(requestFromId, query, hasToBeBestMatch);

			if (null == results)
				results = EMPTY;
        
            return results;
        }

        @Override
        protected void onPostExecute(List<?> results) {
            if (null != handler) {
                Message msg = Message.obtain();
                msg.what = Constants.MESSAGE_SUGGESTION_RETURN;
                msg.obj = results;
                handler.sendMessage(msg);
            }
            else
                Log.w(LOG_TAG, "The message handler is null, so the adapter won't be able to receive the data change");
        }
    }
    
    public class SuggestionFilter extends Filter {

    	@Override
    	protected FilterResults performFiltering(CharSequence hint) {
    		FilterResults results = new FilterResults();
            if (hint == null || hint.length() == 0 
            		|| (Character.UnicodeBlock.of(hint.charAt(0)) != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            			&& hint.length() == 1)) {
                results.count = 0;
                results.values = null;
            }
            else {
            	checkSuggestions(hint);

				if (null == items)
					items = new ArrayList<>();
                results.count = items.size();
                results.values = items;
            }
    		return results;
    	}

    	@Override
    	protected void publishResults(CharSequence constraint, FilterResults results) {
			notifyDataSetChanged();
    	}

    }
    
    public void checkSuggestions(CharSequence hint) {
    	checkSuggestions(hint, "default");
    }

	public void checkSuggestions(CharSequence hint, String type) {
		if (hint == null)
			return;
		
		this.currentSearch = hint;
		
		if (hint.toString().trim().length() > 0)
			new SuggestionTask(type).execute(hint);
	}

	public void setHasToBeMatch(boolean b) {
		this.hasToBeBestMatch = b;
	}
	
	public void setKeepOldItems(boolean b) {
		this.keepOriginal = b;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
        	convertView = inflate(parent);
		
        final TextView tvTitle = (TextView) convertView.findViewById(android.R.id.text1);
        final ImageView iv = (ImageView) convertView.findViewById(R.id.itl_image_view);
        final View overlayView = convertView.findViewById(R.id.overlay);
        
        /*
         * for displaying something really short like initials for easy identify the item
         */
        final AutoResizeTextView tvName = (AutoResizeTextView) convertView.findViewById(R.id.itl_text_view);

		Object obj = this.getItem(position);
		if (!(obj instanceof Searchable))
			Log.w(LOG_TAG, "the data type of the suggestion item can be implemented with the \"Searchable\" interface to achieve better presentation");

        final Object object = this.getItem(position);

        if (object instanceof Searchable) {
            final Searchable item = (Searchable) object;

            if (item.requiresFurtherProcess()) {
                convertView.post(new Runnable() {

                    @Override
                    public void run() {
                        controller.processSearchableItem(item);

                        DisplayItem displayItem = controller.getItemText(item);
                        String highlighted = controller.getTextForSearchResultItem(displayItem.getText());
                        tvTitle.setText(Html.fromHtml(highlighted));

                        if (item.hasImage()) {
                            if (displayItem.getImgBytes() != null) {
                                Bitmap bm = BitmapUtils.bytesToBitmap(displayItem.getImgBytes());
                                iv.setImageBitmap(bm);
                                iv.setVisibility(View.VISIBLE);
                            } else {
                                String oneChar = String.valueOf(Character.toUpperCase(displayItem.getName().charAt(0)));
                                tvName.setText(oneChar);
                                tvName.setVisibility(View.VISIBLE);
                            }
                        }

                        if (!item.isAvailable()) overlayView.setVisibility(View.VISIBLE);
                    }

                });
            } else {
                tvTitle.setText(item.getTitle());
                String idStr = item.getShort();
                if (idStr != null && idStr.length() > 0) {
                    tvName.setText(idStr);
                    tvName.setVisibility(View.VISIBLE);
                } else
                    tvName.setVisibility(View.GONE);
                iv.setImageDrawable(item.getDrawable());
            }
        }
        else
            tvTitle.setText(object.toString());
        
		return convertView;
	}
}
