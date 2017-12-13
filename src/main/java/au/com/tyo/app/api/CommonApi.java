package au.com.tyo.app.api;

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
            HttpConnection http = HttpPool.getInstance().getConnection();
            return http.get(url);
        }
        catch (Exception ex) {
            return "";
        }
    }

}
