package com.simplyconnectedsystems.utility;

import java.math.BigDecimal;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class UtilsClass {
	private static final NumberFormat FORMAT_CURRENCY = NumberFormat
			.getCurrencyInstance();

	/**
	 * Parses an amount into cents.
	 * 
	 * @param p_value
	 *            Amount formatted using the default currency.
	 * @return Value as cents.
	 */
	public static int parseAmountToCents(String p_value) {
		try {
			Number v_value = FORMAT_CURRENCY.parse(p_value);
			BigDecimal v_bigDec = new BigDecimal(v_value.doubleValue());
			v_bigDec = v_bigDec.setScale(2, BigDecimal.ROUND_HALF_UP);
			return v_bigDec.movePointRight(2).intValue();
		} catch (ParseException p_ex) {
			try {
				// p_value doesn't have a currency format.
				BigDecimal v_bigDec = new BigDecimal(p_value);
				v_bigDec = v_bigDec.setScale(2, BigDecimal.ROUND_HALF_UP);
				return v_bigDec.movePointRight(2).intValue();
			} catch (NumberFormatException p_ex1) {
				return -1;
			}
		} catch (java.text.ParseException e) {
			return -1;
		}
	}

	/**
	 * Formats cents into a valid amount using the default currency.
	 * 
	 * @param p_value
	 *            Value as cents
	 * @return Amount formatted using a currency.
	 */
	public static String formatCentsToAmount(int p_value) {
		BigDecimal v_bigDec = new BigDecimal(p_value);
		v_bigDec = v_bigDec.setScale(2, BigDecimal.ROUND_HALF_UP);
		v_bigDec = v_bigDec.movePointLeft(2);
		String v_currency = FORMAT_CURRENCY.format(v_bigDec.doubleValue());
		return v_currency
				.replace(FORMAT_CURRENCY.getCurrency().getSymbol(), "")
				.replace(",", "");
	}

	/**
	 * Formats cents into a valid amount using the default currency.
	 * 
	 * @param p_value
	 *            Value as cents
	 * @return Amount formatted using a currency.
	 */
	public static String formatCentsToCurrency(int p_value) {
		BigDecimal v_bigDec = new BigDecimal(p_value);
		v_bigDec = v_bigDec.setScale(2, BigDecimal.ROUND_HALF_UP);
		v_bigDec = v_bigDec.movePointLeft(2);
		return FORMAT_CURRENCY.format(v_bigDec.doubleValue());
	}


	/**
	 * <p>
	 * to call this function
	 * AppConstants.requestPermission(Activity, Manifest.permission.READ_PHONE_STATE,
	 * "set reason to get permission?", AppConstants.PermissionRequestCodes.INTERNET);
	 *
	 * it will fall back to The Activity through which it has been called
	 * callback function onRequestPermissionsResult on activity
	 * </p>
	 *
	 * @param activity                from which the permission is required
	 * @param permission              which permission is required (Manifest.permission.ACCESS_FINE_LOCATION)
	 * @param showMessage             why do you want this permission
	 * @param PERMISSION_REQUEST_CODE permission request code from PermissionRequestCodes Class
	 * @return true if permission is granted
	 */
	//check function usages for more information and simplicity
	// to request this function
	public static boolean requestPermission(Activity activity, @NonNull String permission, String showMessage, int PERMISSION_REQUEST_CODE) {
		if (Build.VERSION.SDK_INT >= 23) {

			int result = ContextCompat.checkSelfPermission(activity, permission);
			if (result != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

					Toast.makeText(activity, showMessage, Toast.LENGTH_LONG).show();
					ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);

				} else {

					ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
				}
				return false;

			}


		} else {
			return true;
		}
		return true;
	}

	public static boolean checkPermission(Activity activity, @NonNull String permission) {
		if (Build.VERSION.SDK_INT >= 23) {

			int result = ContextCompat.checkSelfPermission(activity, permission);
			if (result != PackageManager.PERMISSION_GRANTED)
				return false;
			else
				return true;
		}

		 else
			return true;

	}
}
