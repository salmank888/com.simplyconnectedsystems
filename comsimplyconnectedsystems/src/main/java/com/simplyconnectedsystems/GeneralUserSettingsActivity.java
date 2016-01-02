package com.simplyconnectedsystems;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.simplyconnectedsystems.utility.GeneralPreferenceHelper;
import com.sk.simplyconnectedsystems.R;

public class GeneralUserSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    GeneralPreferenceHelper _preferenceHelper;
    private EditTextPreference companyName;
    private ListPreference allowed_mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        _preferenceHelper = new GeneralPreferenceHelper(this.getApplicationContext());

        String userCompanyNameDefaultSummary = this.getResources().getString(
                R.string.pref_CompanyName_summary);

        String userAllowedModeDefaultSummary = this.getResources().getString(
                R.string.pref_AllowedMode_summary);

        companyName = (EditTextPreference) getPreferenceScreen().findPreference(
                _preferenceHelper.get_KEY_CompanyName());

        if (companyName.getText() != null
                && companyName.getText().toString().length() > 0) {
            companyName.setSummary(companyName.getText());
        } else {
            companyName.setSummary(userCompanyNameDefaultSummary);
        }

        allowed_mode = (ListPreference) getPreferenceScreen()
                .findPreference(_preferenceHelper.get_KEY_Allowed_Mode());
        if (allowed_mode.getEntry() != null
                && allowed_mode.getEntry().toString().length() > 0) {
            allowed_mode.setSummary(allowed_mode.getEntry());
        } else {
            allowed_mode.setSummary(userAllowedModeDefaultSummary);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        updatePreferenceSummary(sharedPreferences, key);

    }

    private void updatePreferenceSummary(SharedPreferences sharedPreferences,
                                         String key) {

        String userCompanyNameDefaultSummary = this.getResources().getString(
                R.string.pref_CompanyName);

        String userAllowedModeDefaultSummary = this.getResources().getString(
                R.string.pref_AllowedMode);

        if (key.equals(_preferenceHelper.get_KEY_CompanyName())) {
            companyName.setSummary(sharedPreferences.getString(key,
                    userCompanyNameDefaultSummary));
        }

        if (key.equals(_preferenceHelper.get_KEY_Allowed_Mode())) {
            allowed_mode.setSummary(allowed_mode.getEntries()[allowed_mode.findIndexOfValue(sharedPreferences.getString(key,
                    userAllowedModeDefaultSummary))]);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        if (item.getItemId() == R.id.menu_close) {
            finish();
        }

        return super.onMenuItemSelected(featureId, item);

    }
}
