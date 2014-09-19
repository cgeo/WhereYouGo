package menion.android.whereyougo.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class MyListPreference extends ListPreference {

	protected CharSequence summaryTemplate = "";
	
	public MyListPreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    summaryTemplate = super.getSummary();
	    /*
	     * for (int i=0;i<attrs.getAttributeCount();i++) {
	
	        String attr = attrs.getAttributeName(i);
	        String val  = attrs.getAttributeValue(i);
	        if (attr.equalsIgnoreCase("summary")) {
	        	summaryTemplate = val;
	        }
	    }*/
	}	
	
	public CharSequence getSummary () {
		return "("+ getEntry() + ") " + summaryTemplate;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);
	  /*  if(positiveResult) {
	        persistBoolean(!getPersistedBoolean(true));
	    }*/
	 //   Log.d(MainActivity.TAG, "# onDialogClosed: " + positiveResult);
	    setSummary(getSummary());
	}
	
}
