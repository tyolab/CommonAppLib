package au.com.tyo.app.api;

import java.io.IOException;
import java.io.InputStream;

import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpPool;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/12/17.
 */

public class CommonApi {

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
            inputStream = http.postJSON(url, json);
            result = HttpConnection.httpInputStreamToText(inputStream);
        }
        catch (Exception ex) {
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

}
