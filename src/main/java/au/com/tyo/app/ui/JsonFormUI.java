/*
 * Copyright (c) 2018 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
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
 */

package au.com.tyo.app.ui;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import au.com.tyo.app.Constants;
import au.com.tyo.app.Controller;
import au.com.tyo.app.ui.page.Page;

import static au.com.tyo.app.Constants.REQUEST_NONE;
import static au.com.tyo.app.Constants.REQUEST_SOMETHING;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 2/1/18.
 */

public class JsonFormUI<ControllerType extends Controller> extends UIBase<ControllerType> implements FormUI {

    public JsonFormUI(ControllerType controller) {
        super(controller);
    }

    @Override
    public void editForm(Class activityClass, Object data, boolean editable, boolean needResult) {
        Map map;

        if (data instanceof Map &&
                (((Map) data).containsKey(Constants.DATA)/* ||
                ((Map) data).containsKey(Constants.EXTRA_KEY_EDITABLE)*/)) {
            map = (Map) data;
        }
        else {
            map = new HashMap();
            map.put(Constants.DATA, data);
        }
        map.put(Constants.EXTRA_KEY_EDITABLE, editable);

        gotoPageWithData((Page) getCurrentPage(), activityClass, map, data instanceof Parcelable || data instanceof Serializable ? false : true, needResult ? REQUEST_SOMETHING : REQUEST_NONE, null);
    }

    @Override
    public void showForm(Class activityClass, Object data, boolean needResult) {
        editForm(activityClass, data, false, needResult);
    }

    @Override
    public void editForm(Class activityClass, Object data, boolean needResult) {
        editForm(activityClass, data, true, needResult);
    }
}
