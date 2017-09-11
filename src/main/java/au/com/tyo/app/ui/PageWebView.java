package au.com.tyo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.R;
import au.com.tyo.io.IO;
import au.com.tyo.utils.StringUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 11/9/17.
 */

public class PageWebView extends Page {

    private static final String TAG = "PageWebView";

    private WebView webView;

    protected WebViewClient wikiWebViewClient;

    protected WebChromeClient wikiWebChromeClient;

    /**
     * @param controller
     * @param activity
     */
    public PageWebView(Controller controller, Activity activity) {
        super(controller, activity);
        setContentViewResId(R.layout.webview);
    }

    @Override
    public void setupComponents() {
        super.setupComponents();
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        String str;
        if (intent.hasExtra(Constants.DATA_ASSETS_PATH)) {
            str = intent.getStringExtra(Constants.DATA_ASSETS_PATH);
            loadFromAssets(str);
        }
        else if ((str = intent.getStringExtra(Constants.DATA)) != null) {
            loadHtml(null, str, null);
        }
    }

    public void loadFromAssets(String fileName) {
        String html = "<html></html>";
        try {
            html = new String(IO.inputStreamToBytes(getActivity().getAssets().open(fileName)));
        } catch (IOException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
        loadHtml("file:///android_asset/", html, null);
    }

    public void loadHtml(String baseUrl, String html, String url) {
        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", url);
    }
}
