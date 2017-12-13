package au.com.tyo.app.api;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/12/17.
 */

public class CommonApiJson extends CommonApi {

    protected JSON parser;

    public CommonApiJson() {
    }

    public CommonApiJson(String protocol, String host) {
        super(protocol, host);
    }

    public CommonApiJson(String protocol, String host, String path) {
        super(protocol, host, path);
    }

    public JSON getParser() {
        return parser;
    }

    public void setParser(JSON parser) {
        this.parser = parser;
    }
}
