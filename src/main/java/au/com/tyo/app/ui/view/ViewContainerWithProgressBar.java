package au.com.tyo.app.ui.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import au.com.tyo.app.R;

public class ViewContainerWithProgressBar extends FrameLayout {
	
	private ViewGroup progressBarContainer;
	
	private ViewGroup viewContainer;

	private View contentView;

    private ProgressBar progressBar;
	
	private int viewContainerResId = R.id.view_container;
	
	private int progressBarContainerResId = R.id.container_progress_bar;

	private int contentViewResourceId;
	
	private int id;

	public ViewContainerWithProgressBar(Context context) {
		super(context);

	}

	public ViewContainerWithProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public ViewContainerWithProgressBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	public int getViewContainerResId() {
		return viewContainerResId;
	}

	public void setViewContainerResId(int viewContainerResId) {
		this.viewContainerResId = viewContainerResId;
	}

	public int getProgressBarResId() {
		return progressBarContainerResId;
	}

	public void setProgressBarResId(int progressBarResId) {
		this.progressBarContainerResId = progressBarResId;
	}

	public int getContentViewResourceId() {
		return contentViewResourceId;
	}

	public void setContentViewResourceId(int contentViewResourceId) {
		this.contentViewResourceId = contentViewResourceId;
	}

	public void inflateFromDefaultLayoutResource() {
        LayoutInflater factory = LayoutInflater.from(this.getContext());
        factory.inflate(R.layout.container_and_progressbar, this, true);
	}

	public ViewGroup getViewContainer() {
		return viewContainer;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if (null == findViewById(R.id.view_container))
			inflateFromDefaultLayoutResource();
		
		setupComponents();
	}

	/**
	 *
	 */
	public void setupComponents() {
		
       viewContainer = (ViewGroup) findViewById(viewContainerResId);
        
       progressBarContainer = (ViewGroup) findViewById(progressBarContainerResId);

        /**
		 * there were a bit issues with the centering the progress bar
		 */
       if (null != progressBarContainer) {
//    	   progressBarContainer.setVisibility(View.GONE);
           progressBar = (ProgressBar) progressBarContainer.findViewById(R.id.tyodroid_progress_bar);
           if (null != progressBar) {
               if (progressBarContainer instanceof RelativeLayout) {
                   RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                   params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                   progressBar.setLayoutParams(params);
               }
               else if (progressBarContainer instanceof LinearLayout) {
                   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                   progressBar.setLayoutParams(params);
               }
               else if (progressBarContainer instanceof FrameLayout) {
                   LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                   progressBar.setLayoutParams(params);
               }
           }
       }
	}

	/**
	 *
	 */
	public void hideProgressBar() {
		if (null != progressBarContainer)
			progressBarContainer.setVisibility(View.GONE);
		viewContainer.setVisibility(View.VISIBLE);
	}

	/**
	 *
	 */
	public void showProgressBar() {
		viewContainer.setVisibility(View.GONE);
		if (null != progressBarContainer)
            progressBarContainer.setVisibility(VISIBLE);
	}

	/**
	 *
	 * @param view
	 */
	public void addContentView(View view) {
		contentView = view;
		viewContainer.removeAllViews();
		viewContainer.addView(view);
	}

	/**
	 *
	 * @param resourceid
	 */
	public void addContentView(int resourceid) {
		setContentViewResourceId(resourceid);
        LayoutInflater factory = LayoutInflater.from(this.getContext());
        addContentView(factory.inflate(resourceid, null));
	}
	
	public void hide() {
		this.setVisibility(GONE);
	}
	
	public void show() {
		this.setVisibility(VISIBLE);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public void initializeChildContentView(int resId, Runnable job) {
		this.contentViewResourceId = resId;
	}

	public void startTask(int id, Runnable job, Object... params) {
		new ProgressTask(id, job).execute(params);
	}

	public View getContentView() {
		return contentView;
	}

	public interface Worker {
		Object getResult();
	}

	public interface Caller {
		void onPreExecute();
		void onPostExecute(Object o);
	}

	/**
	 *
	 *                                         execute params,
	 */
	public static class BackgroundTask extends AsyncTask<Object, Integer, Object> {

		private Caller caller;
		private Runnable job;
		private Object result;

		public BackgroundTask(Runnable job) {
			this(job, null);
		}

		public BackgroundTask(Runnable job, Caller caller) {
			this.caller = caller;
			this.job = job;
		}

		public Object getResult() {
			return result;
		}

		public void setResult(Object result) {
			this.result = result;
		}

		public void setCaller(Caller caller) {
            this.caller = caller;
        }

        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (null != caller) caller.onPreExecute();
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			if (null != caller) caller.onPostExecute(result);
		}

		@Override
		protected Object doInBackground(Object... params) {
			job.run();

			if (job instanceof Worker) return ((Worker) job).getResult();
            return result;
		}
	}

	public class ProgressTask extends BackgroundTask implements Caller {

		public ProgressTask(int id, Runnable job) {
			super(job);

			ViewContainerWithProgressBar.this.id = id;
			
            setCaller(this);
		}

		@Override
		public void onPreExecute() {
            showProgressBar();
		}

		@Override
		public void onPostExecute(Object o) {
			// addContentView(contentViewResourceId);

			hideProgressBar();
			
			onTaskFinished(id);
		}
	}

	private void onTaskFinished(int id) {
	}
}
