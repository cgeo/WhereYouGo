/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.gui.extension;

import menion.android.whereyougo.hardware.location.LocationState;
import menion.android.whereyougo.settings.SettingItems;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public abstract class CustomPreferenceActivity extends PreferenceActivity {

	public boolean needRestart;
	public boolean needRestartFactoryReset;
	public boolean needGpsRestart;
	
	/**************************************/
	/*        BASIC INITIALIZATION        */
	/**************************************/
	
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			CustomActivity.customOnCreate(this);
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onCreate()", e);
		}
	}
	
	public void onStart() {
		try {
			super.onStart();
			CustomActivity.customOnStart(this);
			
			needRestart = false;
			needRestartFactoryReset = false;
			needGpsRestart = false;
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onStart()", e);
		}
	}
	
	protected void onResume() {
		try {
			super.onResume();
			CustomActivity.customOnResume(this);
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onResume()", e);
		}
	}
	
	protected void onPause() {
		try {
			super.onPause();
			CustomActivity.customOnPause(this);
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onPause()", e);
		}
	}
	
	public void onDestroy() {
		try {
			super.onDestroy();
			if (needRestartFactoryReset) {
	    		A.getMain().showDialogFinish(CustomMain.FINISH_RESTART_FACTORY_RESET);
	    	} else if (needRestart) {
	    		A.getMain().showDialogFinish(CustomMain.FINISH_RESTART);
	    	}
			
			if (needGpsRestart) {
				if (LocationState.isActuallyHardwareGpsOn()) {
					LocationState.setGpsOff(CustomPreferenceActivity.this);
					LocationState.setGpsOn(CustomPreferenceActivity.this);
				}
			}
		} catch (Exception e) {
			Logger.e(getLocalClassName(), "onDestroy()", e);
		}
	}
	
	public void setNeedRestart() {
		this.needRestart = true;
	}
	
	protected void setNeedFactoryReset() {
		this.needRestartFactoryReset = true;
	}
	
	/**************************************/
	/*           SCREEN CREATING          */
	/**************************************/
	
	protected static PreferenceScreen init(PreferenceScreen preferenceScreen) {
        String summary = "";
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
        	summary += preferenceScreen.getPreference(i).getTitle();
        	if (i < preferenceScreen.getPreferenceCount() - 1)
        		summary += ", ";
        }
        preferenceScreen.setSummary(summary);
        return preferenceScreen;
	}

	protected static PreferenceCategory addNewPreferenceCategory(CustomPreferenceActivity context,
			int title, PreferenceScreen prefScreen) {
		PreferenceCategory prefCat = new PreferenceCategory(context);
		prefCat.setTitle(title);
		prefScreen.addPreference(prefCat);
		return prefCat;
	}
	
	/**************************************/
	/*         ADDING PREFERENCES         */
	/**************************************/
	
	public CheckBoxPreference addCheckBoxPreference(PreferenceCategory category,
			int name, int desc, String key, boolean def) {
		CheckBoxPreference preference = new CheckBoxPreference(this);
        preference.setTitle(name);
        preference.setSummary(desc);
        preference.setKey(key);
        preference.setDefaultValue(def);
        category.addPreference(preference);
        return preference;
	}
	
	public void addCheckBoxPreference(PreferenceCategory category,
			int name, int desc, String key, boolean def, Preference.OnPreferenceChangeListener lis) {
		CheckBoxPreference preference = new CheckBoxPreference(this);
        preference.setTitle(name);
        preference.setSummary(desc);
        preference.setKey(key);
        preference.setDefaultValue(def);
        if (lis != null)
        	preference.setOnPreferenceChangeListener(lis);
        category.addPreference(preference);
	}
	
	public EditTextPreference addEditTextPreference(PreferenceCategory category,
			int name, int desc, String key, String def, int inputType,
			OnPreferenceChangeListener preferenceChangeLis) {
		EditTextPreference preference = new EditTextPreference(this);
        preference.setTitle(name);
        preference.setSummary(desc);
        preference.setDialogTitle(name);
        preference.setKey(key);
        preference.setDefaultValue(def);
        preference.getEditText().setInputType(inputType);
        if (preferenceChangeLis != null)
        	preference.setOnPreferenceChangeListener(preferenceChangeLis);
        category.addPreference(preference);
        return preference;
	}

	public ListPreference addListPreference(PreferenceCategory category,
			int name, int desc, String key, String def, CharSequence[] entries,
			CharSequence[] entryValues, OnPreferenceChangeListener preferenceChangeLis) {
		ListPreference preference = new ListPreference(this);
		
        preference.setTitle(name);
        preference.setSummary(desc);
        preference.setDialogTitle(name);
        preference.setKey(key);
        preference.setDefaultValue(def);
        
		preference.setEntries(entries);
		preference.setEntryValues(entryValues);
		if (preferenceChangeLis != null)
			preference.setOnPreferenceChangeListener(preferenceChangeLis);
		category.addPreference(preference);
		return preference;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		SettingItems.handleResponse(this, requestCode, resultCode, data);
	}
}
