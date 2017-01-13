package au.com.tyo.app.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import au.com.tyo.android.utils.BitmapUtils;
import au.com.tyo.android.utils.ListViewItemAdapter;
import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.data.DisplayItem;
import au.com.tyo.app.data.Searchable;
import au.com.tyo.common.ui.AutoResizeTextView;
import au.com.tyo.app.R;

/*
 * 
 * ArrayAdapter will require layout of TextView only
 * so, if other than TextView resource provided will cause exception 
 *             "ArrayAdapter requires the resource ID to be a TextView"
 * 
 */
public class SuggestionsAdapter extends ListViewItemAdapter<Searchable> /*ArrayAdapter<Searchable>*/ implements Filterable,
	OnClickListener {
	
	public static final String LOG_TAG = "SuggestionsAdapter";

	private CompletionListener listener;
	
	private Filter filter;
	
//	private List<Searchable> items;
	
	private Handler handler;
	
	private Controller controller;
	
	private boolean hasToBeBestMatch;

	private boolean keepOriginal;
	
	private CharSequence currentSearch;
	
	private boolean showImage;
	
	public SuggestionsAdapter(Controller controller) {
		super(R.layout.suggestion_list_cell/*au.com.tyo.android.R.layout.simple_dropdown_item_1line*/);
		
		this.controller = controller;
		init();
	}
	
//	public SuggestionsAdapter(Controller controller, int resourceId) {
//		super(controller.getContext(), resourceId);
//		this.controller = controller;
//		
//		init();
//	}
//	
//	public SuggestionsAdapter(Context context, int resourceId) {
//		super(context, resourceId);
//		
//		init();
//	}
//	
//	public SuggestionsAdapter(Context context, int resourceId,
//			CompletionListener listener) {
//		super(context, resourceId);
//		this.listener = listener;
//		
//		
//		
//		init();
//	}

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
		
//		items = new ArrayList<Searchable>();
		filter = new SuggestionFilter(); 
		createMessageHandler();
	}
	
	public void createMessageHandler() {
		handler = new Handler() {

			@SuppressLint("NewApi") 
			@Override
			public void handleMessage(Message msg) {
				if (msg.obj != null) {
					List<Searchable> list =  (List<Searchable>) msg.obj;
					
//					if (!keepOriginal) 
//						clear();
					
//					if (list.size() > 0) {
						if (keepOriginal) {
							removeRedundantItem(items, currentSearch.toString());
							removeRedundantItem(list, currentSearch.toString());
						}
						items = list;
						
//						if (AndroidUtils.getAndroidVersion() >= 11)
//							addAll(items); 
//						else {
//							for (Searchable t : items)
//								add(t);
//						}
//					}
//					else {
//						Log.d(LOG_TAG, "we do something here");
//					}
					
					notifyDataSetChanged();
				}
				super.handleMessage(msg);
			}
			
		};
	}
	
	private void removeRedundantItem(List<Searchable> items, String name) {
		if (items.size() > 0 && name != null && items.contains(name));			
			items.remove(name);
	}

	public interface CompletionListener {
        public void onSearch(String txt, int from);
//        public void onSelect(String txt, int type, String extraData);
	}
	
	public void addCompletionListener(CompletionListener listener) {
		this.listener = listener;
	}

	/*
	 * cannot remember why we need this
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	public void onClick(View v) {
//		if (v instanceof TextView) {
//			String text = ((TextView)v).getText().toString();
//			
//		}
	}

	public Filter getFilter() {
		return filter;
	}
	
	public void createNewFilter() {
		filter = new SuggestionFilter(); 
	}
	
//	public void clear() {
////        filteredResults = null;
//        items = null;
//        notifyDataSetInvalidated();
//	}

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
			
			Thread.currentThread().setName("SuggestionTask");
		}

		@Override
        protected List<?> doInBackground(CharSequence... params) {
        	String query = params[0].toString();
        	List<?> results = controller.getSuggesions(query, hasToBeBestMatch); //new ArrayList<WikiSearch>();
        
            return results;
        }

        @Override
        protected void onPostExecute(List<?> results) {
            Message msg = Message.obtain();
            msg.what = Constants.MESSAGE_SUGGESTION_RETURN;
            msg.obj = results;
        	handler.sendMessage(msg);
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
                results.count = items.size();
                results.values = items;
            }
    		return results;
    	}

    	@Override
    	protected void publishResults(CharSequence constraint, FilterResults results) {
//            if (results != null && results.count > 0) {
                notifyDataSetChanged();
//            }
//            else {
//                notifyDataSetInvalidated();
//            }   		
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
        
        /*
         * for displaying something really short like initials for easy identify the item
         */
        final AutoResizeTextView tvName = (AutoResizeTextView) convertView.findViewById(R.id.itl_text_view);
		
		final Searchable item = this.getItem(position);
		
		if (item.requiresFurtherProcess()) {
			convertView.post(new Runnable() {
	
				@Override
				public void run() {
					DisplayItem displayItem = controller.getItemText(item);
					String highlighted = controller.getTextForSearchResultItem(displayItem.getText());
			        tvTitle.setText(Html.fromHtml(highlighted));
			        
					if (showImage) {
	//					InputStream bytes = controller.getItemImage(item);
						if (displayItem.getImgBytes() != null) {
							Bitmap bm = BitmapUtils.bytesToBitmap(displayItem.getImgBytes());
							iv.setImageBitmap(bm);
						}
						else {
							String oneChar = String.valueOf(Character.toUpperCase(displayItem.getName().charAt(0)));
							tvName.setText(oneChar);
							tvName.setVisibility(View.VISIBLE);
							iv.setVisibility(View.GONE);
						}
					}
					else
						iv.setVisibility(View.GONE);
				}
				
			});
		}
		else {
			tvTitle.setText(item.getTitle());
			String idStr = item.getShort();
			if (idStr != null && idStr.length() > 0) {
				tvName.setText(idStr);
				tvName.setVisibility(View.VISIBLE);
			}
			else
				tvName.setVisibility(View.GONE);
		}
        
		return convertView;
	}
}
