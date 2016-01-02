package com.simplyconnectedsystems.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sk.simplyconnectedsystems.R;

/**
 * Created by Salman Khalid on 12/27/2015.
 */
public class GeneralPreferenceHelper {


    SharedPreferences _sharedPreferences;

    private String _KEY_CompanyName;
    private String _KEY_Allowed_Mode;
    Context _context;

    public GeneralPreferenceHelper(Context context) {
        _context = context;

        _KEY_CompanyName = _context.getResources().getString(
                R.string.pref_CompanyName_key);

        _KEY_Allowed_Mode = _context.getResources().getString(
                R.string.pref_AllowedMode_key);

        _sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(_context);

    }

    public boolean getIsValidSettings()
    {

        if (getCompanyName() == null || getCompanyName().equals("") || getCompanyName().equals("EMPTY"))
        {
            return false;
        }
        if (getAllowedMode() == null || getAllowedMode().equals("") || getAllowedMode().equals("EMPTY"))
        {
            return false;
        }

        return true;
    }

    public String getCompanyName() {
        return _sharedPreferences.getString(_KEY_CompanyName, _context.getResources().getString(R.string.app_name));
    }

    public String getAllowedMode() {
        return _sharedPreferences.getString(_KEY_Allowed_Mode, "1");
    }

    public String get_KEY_CompanyName() {
        return _KEY_CompanyName;
    }

    public void set_KEY_CompanyName(String _KEY_CompanyName) {
        this._KEY_CompanyName = _KEY_CompanyName;
    }

    public String get_KEY_Allowed_Mode() {
        return _KEY_Allowed_Mode;
    }

    public void set_KEY_Allowed_Mode(String _KEY_Allowed_Mode) {
        this._KEY_Allowed_Mode = _KEY_Allowed_Mode;
    }

}
