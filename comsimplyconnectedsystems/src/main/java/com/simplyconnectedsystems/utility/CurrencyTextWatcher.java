package com.simplyconnectedsystems.utility;

import java.text.NumberFormat;

import android.text.Editable;
import android.text.TextWatcher;

public class CurrencyTextWatcher implements TextWatcher {

	boolean mEditing;
	NumberFormat nf;

	public CurrencyTextWatcher() {
		mEditing = false;
		nf = NumberFormat.getCurrencyInstance();
	}

	public synchronized void afterTextChanged(Editable s) {
		if (!mEditing) {
			mEditing = true;

			String digits = s.toString().replaceAll("\\D", "");

			if (s.length() > 10) {
				digits = digits.substring(0, digits.length() - 1);
			}

			try {
				String formatted = nf.format(Double.parseDouble(digits) / 100);
				s.replace(0, s.length(), formatted);
			} catch (NumberFormatException nfe) {
				s.clear();
			}

			mEditing = false;
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

}
