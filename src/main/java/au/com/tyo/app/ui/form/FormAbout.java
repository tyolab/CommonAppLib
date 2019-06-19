package au.com.tyo.app.ui.form;

import android.content.Context;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.BuildConfig;
import au.com.tyo.app.R;
import au.com.tyo.json.form.DataFormEx;
import au.com.tyo.json.form.FormField;
import au.com.tyo.json.form.FormGroup;
import au.com.tyo.json.jsonform.JsonFormField;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 15/3/19.
 */
public class FormAbout extends DataFormEx {

    public static final String FORM_ID = "about";

    public static final String FORM_KEY_EMAIL = "email";
    public static final String FORM_KEY_WEBSITE = "website";

    private final Context context;
    private boolean showAcknowledgement;
    private String acknowledgementTitle;

    public FormAbout(Context context) {
        this.context = context;

        setTitle(context.getString(R.string.about));
        setEditable(false);
        setFormId(FORM_ID);
        setShowAcknowledgement(true);
        setEditable(false);
        setLocked(true);
    }

    public boolean isShowAcknowledgement() {
        return showAcknowledgement;
    }

    public void setShowAcknowledgement(boolean showAcknowledgement) {
        this.showAcknowledgement = showAcknowledgement;
    }

    @Override
    public void initializeForm() {
        FormGroup aboutGroup = new FormGroup(context.getString(R.string.app_information));
        aboutGroup.setShowingTitle(true);

        addAboutPageHeader();

        aboutGroup.addField(context.getString(R.string.version), getAppVersion());
        aboutGroup.addField(context.getString(R.string.copyright), context.getString(R.string.app_copyright));
        addGroup(aboutGroup);

        FormGroup contactGroup = new FormGroup(context.getString(R.string.app_contact_us));
        contactGroup.setShowingTitle(true);

        FormField field = contactGroup.addField(FORM_KEY_WEBSITE, context.getString(R.string.website), context.getString(R.string.tyolab_website));
        field.setClickable(JsonFormField.CLICKABLE_ROW);

        field = contactGroup.addField(FORM_KEY_EMAIL, context.getString(R.string.email), context.getString(R.string.tyolab_email));
        field.setClickable(JsonFormField.CLICKABLE_ROW);

        addGroup(contactGroup);

        if (showAcknowledgement) {
            FormGroup acknowledgementGroup = new FormGroup(context.getString(R.string.app_acknowledgement_title));
            acknowledgementGroup.setShowingTitle(true);

            if (null != acknowledgementTitle) {
                acknowledgementGroup.setTitle(acknowledgementTitle);
            }

            addAboutPageAcknowledgementFields(acknowledgementGroup);
            addGroup(acknowledgementGroup);
        }

        addAboutPageFooter();
    }

    protected String getAppVersion() {
        return AndroidUtils.getPackageVersionName(context) + "_" + AndroidUtils.getAbi();
    }

    protected void addAboutPageHeader() {
    }

    protected void addAboutPageFooter() {
    }

    @OverridingMethodsMustInvokeSuper
    protected void addAboutPageAcknowledgementFields(FormGroup acknowledgementGroup) {
        FormField field = acknowledgementGroup.addField("TyoDroid", "https://github.com/tyolab/tyodroid");
        field.setOrientation(JsonFormField.VALUE_NAME_ORIENTATION_VERTICAL);
        field.setClickable(JsonFormField.CLICKABLE_ROW);
    }
}
