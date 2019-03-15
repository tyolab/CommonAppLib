package au.com.tyo.app.ui.form;

import android.content.Context;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import au.com.tyo.android.AndroidUtils;
import au.com.tyo.app.R;
import au.com.tyo.json.form.DataFormEx;
import au.com.tyo.json.form.FormGroup;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 15/3/19.
 */
public class FormAbout extends DataFormEx {

    public static final String FORM_ID = "about";

    private final Context context;
    private boolean showAcknowledgement;
    private String acknowledgementTitle;

    public FormAbout(Context context) {
        this.context = context;

        setTitle(context.getString(R.string.about));
        setEditable(false);
        setFormId(FORM_ID);
        setShowAcknowledgement(true);
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

        aboutGroup.addField(context.getString(R.string.version), AndroidUtils.getPackageVersionName(context) + " " + AndroidUtils.getAbi());
        aboutGroup.addField(context.getString(R.string.copyright), context.getString(R.string.app_copyright));
        addGroup(aboutGroup);

        FormGroup contactGroup = new FormGroup(context.getString(R.string.app_contact_us));
        contactGroup.setShowingTitle(true);

        contactGroup.addField(context.getString(R.string.website), context.getString(R.string.tyolab_website));
        contactGroup.addField(context.getString(R.string.email), context.getString(R.string.tyolab_email));
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

    protected void addAboutPageHeader() {
    }

    protected void addAboutPageFooter() {
    }

    @OverridingMethodsMustInvokeSuper
    protected void addAboutPageAcknowledgementFields(FormGroup acknowledgementGroup) {
        acknowledgementGroup.addField("TyoDroid", "https://github.com/tyolab/tyodroid");
    }
}
