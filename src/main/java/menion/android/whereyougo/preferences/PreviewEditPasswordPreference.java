package menion.android.whereyougo.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class PreviewEditPasswordPreference extends EditTextPreference {

    protected CharSequence summaryTemplate = "";
    protected CharSequence previewTemplate = "";

    public PreviewEditPasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        summaryTemplate = super.getSummary();

        for (int i = 0; i < attrs.getAttributeCount(); i++) {

            String attr = attrs.getAttributeName(i);
            String val = attrs.getAttributeValue(i);
            if (attr.equalsIgnoreCase("previewTemplate")) {
                previewTemplate = val;
            }
        }
    }

    @Override
    public CharSequence getSummary() {
        String preview = previewTemplate.toString();
        String value = getText();
        String newValue = "";

        if (!value.isEmpty()) {
            for (int i = 0; i < value.length(); i++) {
                newValue += "\u2022";
            }
        }

        if (preview.isEmpty()) {
            preview = "(" + newValue + ")";
        } else {
            preview = preview.replace("%1$", newValue);
        }

        return preview + " " + summaryTemplate;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        setSummary(getSummary());
    }

}