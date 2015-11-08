package com.simplyconnectedsystems;

import com.simplyconnectedsystems.utility.ACHPreferanceHelper;
import com.sk.simplyconnectedsystems.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ACHUserSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	ACHPreferanceHelper _preferenceHelper;

	private EditTextPreference mMID;
	private EditTextPreference mProcessor;
	private EditTextPreference mGateway_id;
	private EditTextPreference mTransaction_center_id;


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_ach);
		
		_preferenceHelper = new ACHPreferanceHelper(this.getApplicationContext());

		String userMIDDefaultSummary = this.getResources().getString(
				R.string.pref_MID_summary);

		String userProcessorDefaultSummary = this.getResources().getString(
				R.string.pref_Processor_summary);
		
		String userGateway_idDefaultSummary = this.getResources().getString(
				R.string.pref_Gateway_id_summary);
		
		String userTransaction_center_idDefaultSummary = this.getResources().getString(
				R.string.pref_Transaction_center_id_summary);



		mMID = (EditTextPreference) getPreferenceScreen().findPreference(
				_preferenceHelper.getKEY_MIDKEY());

		if (mMID.getText() != null
				&& mMID.getText().toString().length() > 0) {
			mMID.setSummary(mMID.getText());
		} else {
			mMID.setSummary(userMIDDefaultSummary);
		}

		mProcessor = (EditTextPreference) getPreferenceScreen()
				.findPreference(_preferenceHelper.getKEY_PROCESSORKEY());
		mProcessor.setSummary(mProcessor.getText());

		if (mProcessor.getText() != null
				&& mProcessor.getText().toString().length() > 0) {
			mProcessor.setSummary(mProcessor.getText());
		} else {
			mProcessor.setSummary(userProcessorDefaultSummary);
		}
		
		mGateway_id = (EditTextPreference) getPreferenceScreen()
				.findPreference(_preferenceHelper.getKEY_GATEWAY_IDKEY());
		mGateway_id.setSummary(mGateway_id.getText());

		if (mGateway_id.getText() != null
				&& mGateway_id.getText().toString().length() > 0) {
			mGateway_id.setSummary(mGateway_id.getText());
		} else {
			mGateway_id.setSummary(userGateway_idDefaultSummary);
		}
		
		mTransaction_center_id = (EditTextPreference) getPreferenceScreen()
				.findPreference(_preferenceHelper.getKEY_TRANSACTION_CENTER_IDKEY());
		mTransaction_center_id.setSummary(mTransaction_center_id.getText());

		if (mTransaction_center_id.getText() != null
				&& mTransaction_center_id.getText().toString().length() > 0) {
			mTransaction_center_id.setSummary(mTransaction_center_id.getText());
		} else {
			mTransaction_center_id.setSummary(userTransaction_center_idDefaultSummary);
		}
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		
		updatePreferenceSummary(arg0, arg1);
		
	}

	private void updatePreferenceSummary(SharedPreferences sharedPreferences,
			String key) {

		String userMIDDefaultSummary = this.getResources().getString(
				R.string.pref_MID_summary);

		String userProcessorDefaultSummary = this.getResources().getString(
				R.string.pref_Processor_summary);
		
		String userGateway_idDefaultSummary = this.getResources().getString(
				R.string.pref_Gateway_id_summary);
		
		String userTransaction_center_idDefaultSummary = this.getResources().getString(
				R.string.pref_Transaction_center_id_summary);

		if (key.equals(_preferenceHelper.getKEY_MIDKEY())) {
			mMID.setSummary(sharedPreferences.getString(key,
					userMIDDefaultSummary));
		}

		if (key.equals(_preferenceHelper.getKEY_PROCESSORKEY())) {
			mProcessor.setSummary(sharedPreferences.getString(key,
					userProcessorDefaultSummary));
		}
		
		if (key.equals(_preferenceHelper.getKEY_GATEWAY_IDKEY())) {
			mGateway_id.setSummary(sharedPreferences.getString(key,
					userGateway_idDefaultSummary));
		}
		
		if (key.equals(_preferenceHelper.getKEY_TRANSACTION_CENTER_IDKEY())) {
			mTransaction_center_id.setSummary(sharedPreferences.getString(key,
					userTransaction_center_idDefaultSummary));
		}

	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		this.getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
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
