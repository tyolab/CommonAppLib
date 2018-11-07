package au.com.tyo.app.model;

import java.util.List;
import java.util.Map;

import au.com.tyo.json.DataJson;

public class DataFormEx extends DataJson {

    private static final String KEY_HEADER = "header";

    private static final String KEY_FOOTER = "footer";

    private static final String KEY_GROUPS = "groups";

    private static final String KEY_TITLE = "title";

    private boolean showGroupTitle = true;

    public boolean isShowGroupTitle() {
        return showGroupTitle;
    }

    public void setShowGroupTitle(boolean showGroupTitle) {
        this.showGroupTitle = showGroupTitle;
    }

    public static class FormGroup extends DataJson {

        public void setTitle(String title) {
            set(KEY_TITLE, title);
        }

        public String getTitle() {
            return getString(KEY_TITLE);
        }

    }

    public void addGroup(String groupName, Map group) {
        addListData(KEY_GROUPS, group);
    }

    public List getGroups() {
        return getListData(KEY_GROUPS);
    }



}
