/*
 * Copyright (c) 2017 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *
 */

package au.com.tyo.app;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import au.com.tyo.android.CommonCache;
import au.com.tyo.io.IO;
import au.com.tyo.io.Indexable;
import au.com.tyo.utils.StringUtils;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 19/12/17.
 */

public class CommonAppData extends Observable {

    private static final String TAG = "CommonAppData";

    private Context context;

    private CommonCache cacheManager;

    private static CommonAppData instance;

    public static CommonAppData getInstance() {
        return instance;
    }

    public CommonAppData(Context context) {
        this.context = context;

        cacheManager = new CommonCache(context);

        instance = this;
    }

    public Context getContext() {
        return context;
    }

    public CommonCache getCacheManager() {
        return cacheManager;
    }
    
    public InputStream assetToInputStream(String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        }
        catch (Exception e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
        return is;
    }
    
    public String assetToString(String fileName) {
        String str = null;
        InputStream is = null;
        try {
            is = assetToInputStream(fileName);
            if (is != null)
                str = new String(IO.inputStreamToBytes(is));
        } catch (IOException e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
        finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) { }
        }
        return str;
    }

    private Object loadObject(String fileName, String jsonFile, Type listType) {
        InputStream is = null;
        Object object = null;

        is = assetToInputStream(fileName);
        try {
            object = IO.readObject(is);
        } catch (Exception e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }

        if (null == object)
            object = loadCacheFile(fileName);

        if (null == object) {
            object = new ArrayList<>();
            try {
                is = context.getAssets().open(jsonFile);
                Reader reader = new InputStreamReader(is);
                object = new Gson().fromJson(reader, listType);

                if (object instanceof List) {
                    List list = (List) object;
                    for (int i = 0; i < list.size(); ++i) {
                        Indexable indexable = (Indexable) list.get(i);
                        indexable.setIndex(i);
                    }
                }

                if (!cacheManager.exists(fileName))
                    writeCacheFile(fileName, object);
                reader.close();
                is.close();
            } catch (Exception e) {
                Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
            }

        }

        return object;
    }

    public void writeCacheFile(String fileName, Object object) {
        try {
            cacheManager.write(fileName, object);
        }
        catch (Exception e) {
            Log.e(TAG, StringUtils.exceptionStackTraceToString(e));
        }
    }

    public void deleteCacheFile(String fileName) {
        cacheManager.delete(fileName);
    }

    public Object loadCacheFile(String fileName) {
        Object object = null;
        try {
            object = cacheManager.read(fileName);
        } catch (Exception e) {
            Log.d(TAG, StringUtils.exceptionStackTraceToString(e));
            deleteCacheFile(fileName);
        }
        return object;
    }

    public boolean existsCacheFile(String filename) {
        return cacheManager.exists(filename);
    }

    public void notifyDataCacheObservers() {
        notifyDataCacheObservers(null);
    }

    public void notifyDataCacheObservers(Object object) {
        setChanged();

        if (null == object)
            notifyObservers();
        else
            notifyObservers(object);
    }
}
