package au.com.tyo.app.ui.page;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 19/12/17.
 */

public abstract class PageInitializer {

    private static PageInitializer instance;

    public static PageInitializer getInstance() {
        return instance;
    }

    public static void setInstance(PageInitializer instance) {
        PageInitializer.instance = instance;
    }

    public abstract void initializePageOnConstruct(Page page);

    public abstract void initializePageOnActivityStart(Page page);
}
