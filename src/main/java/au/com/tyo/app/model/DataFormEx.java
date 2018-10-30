package au.com.tyo.app.model;

import java.util.List;
import java.util.Map;

import au.com.tyo.json.DataJson;

public class DataFormEx extends DataJson {

    private static final String KEY_GROUPS = "groups";

    public void addGroup(String groupName, Map group) {
        addListData(KEY_GROUPS, group);
    }

    public List getGroups() {
        return getListData(KEY_GROUPS);
    }

}
