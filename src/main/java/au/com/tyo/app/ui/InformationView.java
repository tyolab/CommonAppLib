package au.com.tyo.app.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import au.com.tyo.app.Controller;
import au.com.tyo.common.ui.R;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class InformationView extends FrameLayout {
	
	public static final int SPEAK_NOW = 0;

	public static final int ON_ERROR = 1;

	public static final int NO_NETWORK = 2;

	public static final int SHOW_INPUT = 3;

	private static final String LOG_TAG = "InformationView";
	
//	private ViewGroup infoView;
	
	private ViewGroup btnTargetWiki;

	private TextView infoTextView;
	private TextView tvTitle;
	
	private String inputTemplate;
	private String errorTemplate;
	
	private Context context;

	public InformationView(Context context) {
		super(context);
		init(context);
	}

	public InformationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public InformationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}	

	private void init(Context context) {
		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		 
//	    infoTextView = (TextView) findViewById(R.id.input);
//	    btnTargetWiki = (ViewGroup) findViewById(R.id.actionbar_wiki_name_button);
//	    tvTitle = (TextView) findViewById(R.id.wiki_name_text);
//	      
//	    inputTemplate = context.getResources().getString(R.string.input_template);
//	    errorTemplate = context.getResources().getString(R.string.speech_recognition_error);
	}
	
	public void setupComponents(final Controller controller) {

	}
	
	public void onError(String errorInfo) {
		infoTextView.setTextColor(Color.RED);
		infoTextView.setText(errorTemplate);
		Log.e(LOG_TAG, errorInfo);
	}
	
//	public void showNoConnection() {
//		infoTextView.setTextColor(Color.BLUE);
//		infoTextView.setText(R.string.error_no_internet);
//	}
	
	public void showInput(String input) {
		infoTextView.setTextColor(Color.BLUE);
		infoTextView.setText(String.format(inputTemplate, input));
	}
	
	public void showInfo(int infoType, String info) {
		this.btnTargetWiki.setVisibility(View.GONE);
		this.infoTextView.setVisibility(View.VISIBLE);
		
		switch (infoType) {
		case SPEAK_NOW:
			this.showSpeakNowInfo(info);
			break;
		case ON_ERROR:
			this.showErrorInfo(info);
			break;
		case NO_NETWORK:
//			this.showNoNetworkInfo();
			break;
		case SHOW_INPUT:
			this.showInputInfo(info);
			break;
		default:
				break;
		}
	}

	private void showSpeakNowInfo(String info) {
		infoTextView.setTextColor(Color.GREEN);
		infoTextView.setText(info);
	}

	private void showErrorInfo(String errorInfo) {
		infoTextView.setTextColor(this.getResources().getColor(R.color.dark_red));
		infoTextView.setText(String.format(errorTemplate, errorInfo));
	}

//	private void showNoNetworkInfo() {
//		infoTextView.setTextColor(Color.BLUE);
//		infoTextView.setText(R.string.error_no_internet);
//	}

	private void showInputInfo(String input) {
		infoTextView.setTextColor(Color.BLUE);
		infoTextView.setText(String.format(inputTemplate, input));
	}

	public void showTitle(String title, boolean lightTheme) {
		// 		infoTextView.setText(R.string.empty_string);
//		this.setVisibility(View.INVISIBLE);
		this.btnTargetWiki.setVisibility(View.VISIBLE);
		this.infoTextView.setVisibility(View.GONE);
		
		if (lightTheme) {
			tvTitle.setTextColor(context.getResources().getColor(R.color.theme_light_text_color));
			tvTitle.setText(title);		
		}
		else {
			tvTitle.setTextColor(Color.WHITE);
			tvTitle.setText(title);
		}
	}

	public void hide() {
		this.setVisibility(View.GONE);
	}
	
	public void show() {
		this.setVisibility(View.VISIBLE);
	}
}
