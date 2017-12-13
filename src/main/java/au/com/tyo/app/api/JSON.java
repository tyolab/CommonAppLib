package au.com.tyo.app.api;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.ObjectParser;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 13/12/17.
 */

public class JSON {

    private static ObjectParser parser = new JsonObjectParser(new JacksonFactory());

    private static Gson gson;

    public JSON() {

    }

    public static Gson getGson() {
        if (null == gson)
            gson = new Gson();
        return gson;
    }

    public static <T> T parse(String json, Class<? extends T> aClass) {
        return getGson().fromJson(json, aClass);
    }

//    public static <T> T parse(String json, Class<T> cls) throws IOException {
//        return parse(IO.bytesAsInputStream(json.getBytes()), cls);
//    }

    public static <T> T parse(InputStream inputStream, Class<T> cls) throws IOException {
        return parser.parseAndClose(inputStream, Charsets.UTF_8, cls);
    }

}
