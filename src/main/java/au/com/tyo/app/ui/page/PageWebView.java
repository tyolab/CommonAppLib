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

public class PageWebView<C extends Controller> extends Page<C> implements ValueCallback<String> {

    private static final String TAG = "PageWebView";

    private WebView webView;

    protected WebViewClient webViewClient;

    protected WebChromeClient webChromeClient;

    private String data;

    private String baseUrl;

    private String mimeType;

    private WebPageListener pageMonitor;

    public interface WebPageListener extends ValueCallback<String> {
        void onPageFinishedLoading(WebView webView, String url);
    }

    /**
     * @param controller
     * @param activity
     */
    public PageWebView(C controller, Activity activity) {
        super(controller, activity);
        setContentViewResId(R.layout.webview);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    public WebPageListener getPageMonitor() {
        return pageMonitor;
    }

    public void setPageMonitor(WebPageListener pageMonitor) {
        this.pageMonitor = pageMonitor;
    }

    @Override
    public void setupComponents() {
        super.setupComponents();
        webView = (WebView) findViewById(R.id.webview);

        if (null != webView) {
            webView.setWebChromeClient(webChromeClient == null ? (webChromeClient = new CommonWebChromeClient()) : webChromeClient);
            webView.setWebViewClient(webViewClient == null ? (webViewClient = new CommonWebViewClient(pageMonitor)) : webViewClient);

            // webView.clearCache(true);
            // webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        }
    }

    @Override
    public void bindData(Intent intent) {
        super.bindData(intent);

        String str;
        if (intent.hasExtra(Constants.DATA_ASSETS_PATH)) {
            str = intent.getStringExtra(Constants.DATA_ASSETS_PATH);
            loadDataFromAssets(str);
        }
        if (intent.hasExtra(Constants.DATA_MIME_TYPE))
            mimeType = intent.getStringExtra(Constants.DATA_MIME_TYPE);
        else
            mimeType = "text/html";
    }

    @Override
    protected void onDataParcelReceived(Object o) {
        data = o.toString();
    }

    public void loadDataFromAssets(String fileName) {
        String html = "<html></html>";
        try {
            html = new String(IO.inputStreamToBytes(getActivity().getAssets().open(fileName)));
        } catch (IOException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
        // loadHtml("file:///android_asset/", html, null);
        data = html;
        baseUrl = "file:///android_asset/";
    }

    public void loadHtml(String baseUrl, String html, String url) {
        webView.loadDataWithBaseURL(baseUrl, html, mimeType, "UTF-8", url);
    }

    public void call(String functionName, Object[] params) {
        call(webView, functionName, params, this);
    }

    public void call(String what) {
        call(webView, what);
    }

    public static void call(WebView webView, String functionName, Object[] params, ValueCallback<String> callback) {
        StringBuffer callString = new StringBuffer();

        for (Object obj : params) {
            if (callString.length() > 0)
                callString.append(", ");

            if (obj == null)
                callString.append("null");
            if (obj instanceof String)
                callString.append("'" + ((String) obj).toString() + "'");
            else
                callString.append(String.valueOf(obj));
        }

        String full = functionName + "(" + callString.toString() + ")";

        call(webView, full, callback);
    }
    public static void call(WebView webView, String full) {
        call(webView, full, null);
    }

    public static void call(WebView webView, String full, ValueCallback<String> callback) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(full, callback);
        } else {
            webView.loadUrl("javascript:" + full + "");
        }
    }

    @Override
    public void onReceiveValue(String value) {
        // no ops
    }

    @Override
    public void onActivityStart() {
        super.onActivityStart();

        if (null != data)
            loadHtml(baseUrl, data, null);
    }

    public static class CommonWebViewClient extends WebViewClient {

        private WebPageListener webPageListener;

        public CommonWebViewClient(WebPageListener webPageListener) {
            this.webPageListener = webPageListener;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (null != webPageListener)
                webPageListener.onPageFinishedLoading(view, url);
        }
    }

    public static class CommonWebChromeClient extends WebChromeClient {

    }
}
