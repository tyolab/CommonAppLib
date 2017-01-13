package au.com.tyo.app.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.tyo.android.services.Downloader;
import au.com.tyo.android.services.DownloaderInterface;
import au.com.tyo.android.services.ImageDownloader;
import au.com.tyo.app.Controller;
import au.com.tyo.app.Request;
import au.com.tyo.app.adapter.HistoryAdapter;
import au.com.tyo.common.ImageTextListItem;
import au.com.tyo.io.CacheInterface;
import au.com.tyo.io.StreamUtils;
import au.com.tyo.io.WildcardFileStack;

public class History extends Downloader<String, ImagedSearchableItem> 
	implements CacheInterface<String>, DownloaderInterface<String, ImagedSearchableItem>, OnItemClickListener {

	public static final String DOMAIN_TITLE_SEPARATOR = "__";
	
	public static final int HISTORY_ITEM_SIZE = 100;

	private static final String LOG_TAG = "History";
	
//	private PageHistory pageHistory;

	private WildcardFileStack fileStack;
	
	private ArrayList<ImagedSearchableItem> names;
	
	private Set<String> pageName;
	
	private HistoryAdapter adapter;
	
	private Controller controller;
	
	private ImageDownloader imageDownloader;
	
	private int limit;
	
	public History(Controller controller) {
		super(controller.getContext(), "pages");
		
		this.controller = controller;
		
		this.imageDownloader = controller.getImageDownloader();
		
		this.adapter = new HistoryAdapter();
		
		limit = HISTORY_ITEM_SIZE;
	}
	
	public void clear() {
		fileStack = null;
		loadInHistoryDirectoy();
		
		File file;
		while ((file = fileStack.next()) != null)
			file.delete();
		
		this.getCacheDirectory().delete();
		
		if (names != null)
			names.clear();
		
		if (adapter != null)
			adapter.notifyDataSetInvalidated();
		
		if (pageName != null)
			pageName.clear();
	}
	
	private void loadInHistoryDirectoy() {
		if (fileStack == null && this.isCacheEnabled())
			try {
				fileStack = new WildcardFileStack(this.getCacheDirectory(), "*__*");
				fileStack.setIncludeAllSubfolders(true);
//				fileStack.setToListAllFiles(true);
				fileStack.listFiles();
				fileStack.sortByDate();
			} catch (Exception e) {
				Log.e(LOG_TAG, "loading page history error.");
			}
	}
	
	public void startHistoryLoadingTask() {
		new HistoryLoadingTask().execute();
	}
	
	public void loadHistory() {
		synchronized(this) {
			if (names == null) {
				loadInHistoryDirectoy();
				names = new ArrayList<ImagedSearchableItem>();
				pageName = new HashSet<String>();
				
				if (fileStack != null) {
					File file, dir;
					dir = fileStack.next();
					int count = 0;
					
					while((dir = fileStack.next()) != null) {
						if (dir.isDirectory() && count < limit) {
							try {
								WildcardFileStack stack = new WildcardFileStack(dir, "*__*");
								stack.setIncludeAllSubfolders(true);
								stack.setToListAllFiles(true);
								stack.listFiles();
								stack.sortByDate();

								while ((file = stack.next()) != null) {								
									if (count < limit) {
										++count;
										ImagedSearchableItem page = nameToPage(file.getName());
										
										String name = page.getTitle();					
										if (pageName.contains(name)) {
											file.delete();
										}
										else {
											pageName.add(name);
//											if (controller.getNetworkMonitor() != null && controller.getNetworkMonitor().hasInternet())
//												page.setBaseUrl(WikiApi.getInstance().getApiConfig().buildBaseUrl(page.getLangCode()));
											
											page.setUrl(file.getPath());
											names.add(page);
										}
									}
									else
										file.delete();
								}
							} catch (Exception e) {
								dir.delete();
							};
						}
						else
							dir.delete();
					}
				}
				// only needed once
//				pageName.clear();
//				pageName = null;
			}
		}
	}
	
	private class HistoryLoadingTask extends AsyncTask<Void, Void, List<ImagedSearchableItem>> {

		@Override
		protected List<ImagedSearchableItem> doInBackground(Void... params) {
			loadHistory();
			return names; 
		}
		
        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			Thread.currentThread().setName("HistoryLoadingTask");
		}
        
		/*
		 * we don't really need this
		 */
        protected void onPostExecute(List<ImageTextListItem> results) {
        	adapter.setItems(results);
        	if (results.size() > 0)
        		adapter.notifyDataSetChanged();
			else 
				adapter.notifyDataSetInvalidated();
//        	if (controller.getUi() != null && controller.getUi().isReady())
//        		controller.getUi().getMainView().getMixedTabView().showHistory();
        }
	}
	
	@Override
	public void handleResult(ImagedSearchableItem page, String text) {	
//		controller.onAbstractLoaded(page);
//		controller.displayFullArticle(page);
	}

	/**
	 * @param url - the full path when reading, the wiki domain + : +  title when writing
	 */
	@Override
	public File urlToFile(String url, boolean readingOrWriting) {
		File f = null;
		if (readingOrWriting) {
			f = new File(url);
		}
		else {
			String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String dir = getCacheDirectory().toString() + File.separator + formattedDate;
			File dirFile = new File(dir);
			if (!dirFile.exists())
				dirFile.mkdirs();
	//	   	String filename = String.valueOf(url.hashCode());
		   	f = new File(dir, url);
		}
	   	return f;
	}

	@Override
	public String read(File f) {
		InputStream fileStream = null;
		String page = "";
		
		try {
			fileStream = new FileInputStream(f);

			page = StreamUtils.GzipStreamToString(fileStream);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		finally {
			if (fileStream != null)
				try {
					fileStream.close();
				} 
				catch (IOException e) {
				}
		}
		return page;
	}

	@Override
	public void write(String target, File file) throws Exception {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			Writer writer = new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8");
		  
			try {
				writer.write(target/*.getText()*/);
			} 
			finally {
				writer.close();
			}
		} 
		finally {
			output.close();
		}
	}

	public int size() {
		return names.size();
	}

	public void add(ImagedSearchableItem page) {
//		pageHistory.push(page);
		
		if (page.getThumbnailLink() != null)
			imageDownloader.download(page.getThumbnailLink(), null);
		
		String title = page.getTitle();
		if (names != null && title != null) {
			if (pageName.contains(title)) {
				for (int i = 0; i < names.size(); ++i) {
					ImagedSearchableItem wp = names.get(i);
					if (wp.getTitle() != null && wp.getTitle().equalsIgnoreCase(title)) {
						names.remove(i);
						break;
					}
				}
			}
			names.add(0, page);
			adapter.notifyDataSetChanged();
		}
	}
	
//	public void setPageHistory(PageHistory pageHistory) {
//		this.pageHistory = pageHistory;
//	}
//
//
//	public PageHistory getPageHistory() {
//		if (pageHistory == null)
//			pageHistory = new PageHistory();
//		return pageHistory;
//	}

	/**
	 * we are not downloading file but actually load the downloaded file
	 * 
	 * load cache will load the gzipped string into the text, we will do the parsing here
	 * 
	 * @param url - the page title
	 */
	@Override
	public String downloadFile(ImagedSearchableItem page, String url) {
		String text = "";
		
//		try {
//			text = loadCache(url);
//			page.setText(text);
//			page.setHasFullText(true);
//			page.setBaseUrl(WikiApi.getInstance().getApiConfig().buildBaseUrl(page.getLangCode()));
//			
//			ArticleParsingThread.retrieveArticle(page);
//			
//			if (controller.getNetworkMonitor().hasInternet() && page.hasImage()) 
//				ImagedSearchableItemLoader.retrieveImageUrl(page);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e(LOG_TAG, " \n" + (e.getLocalizedMessage() != null ? e.getLocalizedMessage() : ""));
//		}

		return text;
	}

	@Override
	public String fileCheck(String url) throws Exception {
		return null;
	}

	/**
	 * this is useless bit, because we have totally different logic for getting the page cache
	 */
	@Override
	protected String processInputStream(InputStream inputStream) {
		return null;
	}

	public void save(ImagedSearchableItem page) {
//		if (page.getText() == null || page.getTitle() == null 
//				|| page.getTitle().length() == 0 ||page.getText().length() == 0)
//			return;
//		String name = pageToName(page);
//		File file = urlToFile(name, false);
//		try {
//			if (page.getFromSource() == ImagedSearchableItem.FROM_SOURCE_LOCAL)
//				file.createNewFile(); // we don't need to write content just the name we need
//			else {
//				this.write(page.getText(), file);
//				page.setUrl(file.getAbsolutePath());
//			}
//		} catch (Exception e) {
//			Log.e(LOG_TAG, "save file failed: " + file.toString());
//		}
	}

	public ImagedSearchableItem nameToPage(String name) {
		ImagedSearchableItem page = new ImagedSearchableItem();
		page.setTitle(name);
//		String[] tokens = name.split(DOMAIN_TITLE_SEPARATOR);
//		
//		ImageDownloader imageDownloader = controller.getImageDownloader();
//		
//		if (tokens.length == 3) {
//			page.setThumbnailLink(imageDownloader.getCacheDirectory().toString() + File.separator + tokens[0]);
//			page.setLangCode(tokens[1]);
//			page.setTitle(tokens[2].replace('_', ' '));
//		}
//		else if (tokens.length == 2){
//			page.setLangCode(tokens[0]);
//			page.setTitle(tokens[1].replace('_', ' '));
//		}
//		else
//			page.setTitle(name.replace('_', ' '));
		
		return page;
	}
	
	public String pageToName(ImagedSearchableItem page) {
//		String imageHashCode = urlHashCodeToString(page.getThumbnailLink());
//		
//		if (imageHashCode == null)
//			imageHashCode = "0";
		
		return page.getTitle(); //imageHashCode + (imageHashCode.length() > 0 ? DOMAIN_TITLE_SEPARATOR : "") + page.getLangCode() + DOMAIN_TITLE_SEPARATOR + page.getTitle().replace(' ', '_');
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		controller.getUi().closeDrawer();
		
		ImageTextListItem page = adapter.getItem(position);
		
//		File file = new File(page.getUrl());
//		if (!file.exists() || file.length() == 0)
//			controller.onHistoryItemClick(page, Request.FROM_HISTORY, false);
//		else {
////			controller.getUi().onLoadingPage();
//			this.download(page.getUrl(), page, true);
//		}
	}

	public ArrayList<ImagedSearchableItem> getSearchHistory() {
		return names;
	}

	public HistoryAdapter getAdapter() {
		return adapter;
	}
}
