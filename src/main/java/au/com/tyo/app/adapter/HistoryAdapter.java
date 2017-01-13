package au.com.tyo.app.adapter;

import android.view.View;
import android.view.ViewGroup;
import au.com.tyo.app.Controller;
import au.com.tyo.app.data.ImagedSearchableItem;
import au.com.tyo.common.ui.ImageTextListViewAdapter;

public class HistoryAdapter extends ImageTextListViewAdapter {
	
//	private Handler handler;

	public HistoryAdapter() {
		super();
		
		init();
	}

	private void init() {
//		handler = new MessageHandler(this);
//		new HistoryLoadingTask().execute();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view =  super.getView(position, convertView, parent);
		
		ImagedSearchableItem page = (ImagedSearchableItem) items.get(position);
		
//        if (page.getThumbnailLink() == null && view != null) {
//	        ImageView image = (ImageView) view.findViewById(R.id.itl_image_view);
//	        Bitmap bitmap = BitmapFactory.decodeResource(controller.getContext().getResources(),
//                    R.drawable.image_not_found);
//	        image.setImageBitmap(bitmap);
//        }
		
		return view;
	}

//	private static class MessageHandler extends Handler {
//		
//		HistoryAdapter parent;
//		
//		public MessageHandler(HistoryAdapter parent) {
//			this.parent = parent;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public void handleMessage(Message msg) {
//			if (msg.obj != null) {
//				parent.setItems((List<ImagedSearchableItem>) msg.obj);
//                parent.notifyDataSetChanged();
//			}
//			else
//				parent.notifyDataSetInvalidated();
//		}
//	}

}
