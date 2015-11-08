package com.simplyconnectedsystems.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sk.simplyconnectedsystems.R;

public class CreditPreferenceHelper {

	SharedPreferences _sharedPreferences;

	private String _KEY_MID;
	private String _KEY_TID;
	private String _KEY_PROCESSOR;
	private String _KEY_GATEWAY_ID;
	private String _KEY_TRANSACTION_CENTER_ID;

	Context _context;

	public CreditPreferenceHelper(Context context) {
		_context = context;

		_KEY_MID = _context.getResources().getString(
				R.string.pref_MID_key);

		_KEY_TID = _context.getResources().getString(
				R.string.pref_TID_key);

		_KEY_PROCESSOR = _context.getResources().getString(
				R.string.pref_Processor_key);
		
		_KEY_GATEWAY_ID = _context.getResources().getString(
				R.string.pref_Gateway_id_key);
		
		_KEY_TRANSACTION_CENTER_ID = _context.getResources().getString(
				R.string.pref_Transaction_center_id_key);

		_sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(_context);

	}
	
	public boolean getIsValidSettings()
	{
		
		if (getMID() == null || getMID().equals("") || getMID().equals("EMPTY"))
		{
			return false;
		}
		if (getTID() == null || getTID().equals("") || getTID().equals("EMPTY"))
		{
			return false;
		}
		if (getProcessor() == null || getProcessor().equals("") || getProcessor().equals("EMPTY"))
		{
			return false;
		}
		if (getGateway_id() == null || getGateway_id().equals("") || getGateway_id().equals("EMPTY"))
		{
			return false;
		}
		if (getTransaction_center_id() == null || getTransaction_center_id().equals("") || getTransaction_center_id().equals("EMPTY"))
		{
			return false;
		}
//		if (getUserEncrpyionKey() == null || getUserEncrpyionKey().equals(""))
//		{
//			return false;
//		}
//		else
//		{
//			try {
//				PGEncrypt _pg = new PGEncrypt();
//				_pg.setKey(getUserEncrpyionKey());
//			} catch (IllegalArgumentException e) {
//				return false;
//			}
//		}
		
		return true;
	}

	public String getMID() {
		return _sharedPreferences.getString(_KEY_MID, "EMPTY");
	}

	public String getTID() {
		return _sharedPreferences.getString(_KEY_TID, "EMPTY");
	}

	public String getProcessor() {
		return _sharedPreferences.getString(_KEY_PROCESSOR, "EMPTY");
	}

	public String getGateway_id() {
		return _sharedPreferences.getString(_KEY_GATEWAY_ID, "EMPTY");
	}
	
	public String getTransaction_center_id() {
		return _sharedPreferences.getString(_KEY_TRANSACTION_CENTER_ID, "EMPTY");
	}
	
	public String getKEY_MIDKEY() {
		return _KEY_MID;
	}

	public void setKEY_MIDKEY(String _KEY_MIDKEY) {
		this._KEY_MID = _KEY_MIDKEY;
	}

	public String getKEY_TIDKEY() {
		return _KEY_TID;
	}

	public void setKEY_TID(String _KEY_TIDKEY) {
		this._KEY_TID = _KEY_TIDKEY;
	}

	public String getKEY_PROCESSORKEY() {
		return _KEY_PROCESSOR;
	}

	public void setKEY_PROCESSORKEY(String _KEY_PROCESSORKEYKEY) {
		this._KEY_PROCESSOR = _KEY_PROCESSORKEYKEY;
	}

	public String getKEY_GATEWAY_IDKEY() {
		return _KEY_GATEWAY_ID;
	}

	public void setKEY_GATEWAY_IDKEY(String _KEY_GATEWAY_IDKEY) {
		this._KEY_GATEWAY_ID = _KEY_GATEWAY_IDKEY;
	}
	
	public String getKEY_TRANSACTION_CENTER_IDKEY() {
		return _KEY_TRANSACTION_CENTER_ID;
	}

	public void setKEY_TRANSACTION_CENTER_IDKEY(String _KEY_TRANSACTION_CENTER_IDKEY) {
		this._KEY_TRANSACTION_CENTER_ID = _KEY_TRANSACTION_CENTER_IDKEY;
	}
}
