package au.com.tyo.app.adapter;

import java.util.List;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import au.com.tyo.android.utils.ListViewItemAdapter;
import au.com.tyo.app.Controller;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.common.ui.R;

public class SearchResultAdapter extends ListViewItemAdapter<ImagedSearchableItem> {
	
	private Controller controller;
	
	private String query;

	public SearchResultAdapter(int resId, List<ImagedSearchableItem> items, Controller controller, String q) {
		super(resId, items);
		this.controller = controller;
		this.query = q;
	}
	
	public void setController(Controller controller) {
		 this.controller = controller;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	
		 
//        if (ws.getSnippetHtml() == null) ws.buildSnippetHtml();
        /* 
         * Don't do it in this background, it will cause problems
         */
//        convertView.post(new Runnable() {
//
//			@Override
//			public void run() {
//
//				SearchResultAdapter.this.notifyDataSetChanged();
//			}
//        	
//        });
		View v;
        if (position < items.size()) {
            v = super.getView(position, convertView, parent);
            
            final TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
            final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.snippet_progress_bar);
            
			final ImagedSearchableItem ws = items.get(position);
			
			v.post(new Runnable() {

				@Override
				public void run() {
					String snippetHtml = "";
					if (ws.getSnippetHtml() != null && ws.getSnippetHtml().length() >  0) {
				        snippetHtml = ws.getSnippetHtml();
					}
					else {
						snippetHtml = controller.getTextForSearchResultItem(ws, query);
						ws.setSnippetHtml(snippetHtml);
					}
						
					tvSnippet.setText(Html.fromHtml(snippetHtml));
					
					progressBar.setVisibility(View.GONE);
					tvSnippet.setVisibility(View.VISIBLE);
				}
				
			});
        }
        else
        	v = super.getView(position, convertView, parent);
        return v;
        
//	    ImageView image = (ImageView) convertView.findViewById(R.id.itl_image_view);
//        String link = page.getThumbnailLink();
//        if (link != null) {
//            int pos;
//            if ((pos = link.indexOf("file://")) > -1)
//            	link = link.substring(pos + 7);
//	        	
//	        controller.getImageDownloader().download(link, image);
//        }
//        else {
//        	if (hideImageViewWhenImageUnavaiable)
//        		image.setVisibility(View.GONE);
//        }
        	
//        TextView tvTitle = (TextView) convertView.findViewById(R.id.itl_text);
//        tvTitle.setText(page.getTitle());

	}
	
	
}
