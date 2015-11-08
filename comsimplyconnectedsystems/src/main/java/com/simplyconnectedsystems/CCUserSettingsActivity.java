package com.simplyconnectedsystems;

import com.simplyconnectedsystems.utility.CreditPreferenceHelper;
import com.sk.simplyconnectedsystems.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class CCUserSettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	
	CreditPreferenceHelper _preferenceHelper;

	private EditTextPreference mMID;
	private EditTextPreference mTID;
	private EditTextPreference mProcessor;
	private EditTextPreference mGateway_id;
	private EditTextPreference mTransaction_center_id;


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		
		_preferenceHelper = new CreditPreferenceHelper(this.getApplicationContext());

		String userMIDDefaultSummary = this.getResources().getString(
				R.string.pref_MID_summary);

		String userTIDDefaultSummary = this.getResources().getString(
				R.string.pref_TID_summary);

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

		mTID = (EditTextPreference) getPreferenceScreen()
				.findPreference(_preferenceHelper.getKEY_TIDKEY());
		if (mTID.getText() != null
				&& mTID.getText().toString().length() > 0) {
			mTID.setSummary(mTID.getText());
		} else {
			mTID.setSummary(userTIDDefaultSummary);
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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		updatePreferenceSummary(sharedPreferences, key);

	}

	private void updatePreferenceSummary(SharedPreferences sharedPreferences,
			String key) {

		String userMIDDefaultSummary = this.getResources().getString(
				R.string.pref_MID_summary);

		String userTIDDefaultSummary = this.getResources().getString(
				R.string.pref_TID_summary);

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

		if (key.equals(_preferenceHelper.getKEY_TIDKEY())) {
			mTID.setSummary(sharedPreferences.getString(key,
					userTIDDefaultSummary));
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
