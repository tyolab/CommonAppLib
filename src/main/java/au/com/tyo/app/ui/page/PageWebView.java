package au.com.tyo.app.ui.page;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.ValueCallback;
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

public class PageWebView extends Page implements ValueCallback<String> {

    private static final String TAG = "PageWebView";

    private WebView webView;

    protected WebViewClient webViewClient;

    protected WebChromeClient webChromeClient;

    public interface WebPageListener extends ValueCallback<String> {
        void onPageFinishedLoading(WebView webView);
    }

    private WebPageListener webPageListener;

    /**
     * @param controller
     * @param activity
     */
    public PageWebView(Controller controller, Activity activity) {
        super(controller, activity);
        setContentViewResId(R.layout.webview);

        webPageListener = controller.getUi().getWebPageListener();
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    @Override
    public void setupComponents() {
        super.setupComponents();
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebChromeClient(webChromeClient == null ? (webChromeClient = new WebChromeClient()) : webChromeClient);
        webView.setWebViewClient(webViewClient == null ? (webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (null != webPageListener)
                    webPageListener.onPageFinishedLoading(webView);
            }
        }) : webViewClient);

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
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

    public void call(String functionName, Object[] params) {
        call(webView, functionName, params, this);
    }

    public static void call(WebView webView, String functionName, Object[] params, ValueCallback<String> callback) {
        StringBuffer callString = new StringBuffer();

        for (Object obj : params) {
            if (callString.length() > 0)
                callString.append(", ");

            if (obj instanceof String)
                callString.append("'" + ((String) obj).toString() + "'");
            else
                callString.append(String.valueOf(obj));
        }

        String full = functionName + "(" + callString.toString() + ")";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(full, callback);
        } else {
            webView.loadUrl("javascript:" + full + ";");
        }
    }

    @Override
    public void onReceiveValue(String value) {
        // no ops
    }
}
