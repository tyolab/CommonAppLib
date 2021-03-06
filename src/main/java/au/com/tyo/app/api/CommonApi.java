package au.com.tyo.app.api;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpPool;
import au.com.tyo.utils.StringUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/12/17.
 */

public class CommonApi {

    private static final String TAG = "CommonApi";

    protected String host;

    protected String protocol;

    protected String baseUrl;

    protected String path = "";

    public CommonApi() {
    }

    public CommonApi(String protocol, String host) {
        this.protocol = protocol;
        this.host = host;
    }

    public CommonApi(String protocol, String host, String path) {
        this.host = host;
        this.protocol = protocol;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void buildBaseUrl() {
        baseUrl = buildBaseUrl(host, path);
    }

    public String buildBaseUrl(String host, String path) {
        return protocol + "://" + host + (null != path ? path : "");
    }

    public String getBaseUrl() {
        if (null == baseUrl)
            buildBaseUrl();

        return baseUrl;
    }

    /**
     *
     * @param url
     * @return
     */
    protected String loadUrl(String url) {
        try {
            HttpConnection http = HttpPool.getConnection();
            return http.get(url);
        }
        catch (Exception ex) {
            return "";
        }
    }

    protected String post(String url, String json) {
        InputStream inputStream = null;
        String result = null;
        try {
            HttpConnection http = HttpPool.getConnection();
            result = http.postJSONForResult(url, json);
            // inputStream = http.postJSON(url, json);
            //result = HttpConnection.httpInputStreamToText(inputStream);
        }
        catch (Exception ex) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(ex));
            return "";
        }
        finally {
            if (null != inputStream)
                try {
                    inputStream.close();
                } catch (IOException e) { }
        }
        return result;
    }

    public String get(String url) {
        return loadUrl(url);
    }
}
