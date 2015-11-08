package com.simplyconnectedsystems;

import net.frakbot.glowpadbackport.GlowPadView;
import net.frakbot.glowpadbackport.GlowPadView.OnTriggerListener;
import com.simplyconnectedsystems.utility.ACHPreferanceHelper;
import com.simplyconnectedsystems.utility.CreditPreferenceHelper;
import com.sk.simplyconnectedsystems.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTriggerListener{

	private static final int RESULT_SETTINGS = 1;
	
	private boolean _isACH = false;
	
	private CreditPreferenceHelper _preferanceHelperCC;
	private ACHPreferanceHelper _preferanceHelperACH;

	private UniMagTopDialog dlgErrorTopShow = null;

	private GlowPadView _glowPadView;

	// ImageView _headerImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		
		_glowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);

		_glowPadView.setOnTriggerListener(this);
		
		// uncomment this to make sure the glowpad doesn't vibrate on touch
		// mGlowPadView.setVibrateEnabled(false);
		
		// uncomment this to hide targets
//		_glowPadView.setShowTargetsOnIdle(true);
		
		_preferanceHelperCC = new CreditPreferenceHelper(this.getApplicationContext());
		_preferanceHelperACH = new ACHPreferanceHelper(this.getApplicationContext());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		if (item.getItemId() == R.id.menu_settingsCC) {
			Intent i = new Intent(this, CCUserSettingsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
		}
		else if (item.getItemId() == R.id.menu_settingsACH) {
			Intent i = new Intent(this, ACHUserSettingsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
		}
		if (item.getItemId() == R.id.menu_close) {
			finish();
		}

		return super.onMenuItemSelected(featureId, item);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		// _headerImage.invalidate();
	}
	
	private void showErrorTopDialog(String strTitle, String errorMessage) {
		try {
			if (dlgErrorTopShow == null) {
				dlgErrorTopShow = new UniMagTopDialog(this);
			}

			dlgErrorTopShow.setCancelable(false);

			Window win = dlgErrorTopShow.getWindow();
			win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
					WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			dlgErrorTopShow.setTitle(strTitle);
			dlgErrorTopShow.setContentView(R.layout.dlgerrortopview);
			TextView myTV = (TextView) dlgErrorTopShow
					.findViewById(R.id.TView_Info);
			Button myBtn = (Button) dlgErrorTopShow
					.findViewById(R.id.btnCancel);

			myTV.setText(errorMessage);
			myBtn.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					if(_isACH){
						Intent i = new Intent(MainActivity.this, ACHUserSettingsActivity.class);
						startActivityForResult(i, RESULT_SETTINGS);
						_isACH = false;
					}else{
					Intent i = new Intent(MainActivity.this, CCUserSettingsActivity.class);
					startActivityForResult(i, RESULT_SETTINGS);
					}
					
					dlgErrorTopShow.dismiss();
				}
			});

			dlgErrorTopShow.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if ((keyCode == KeyEvent.KEYCODE_BACK)) {
						return false;
					}
					return true;
				}
			});
			dlgErrorTopShow.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	};
	
	private class UniMagTopDialog extends Dialog {

		public UniMagTopDialog(Context context) {
			super(context);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK
					|| KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
				return false;
			}
			return super.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onKeyMultiple(int keyCode, int repeatCount,
				KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK
					|| KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {

				return false;
			}
			return super.onKeyMultiple(keyCode, repeatCount, event);
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK
					|| KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
				return false;
			}
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReleased(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTrigger(View v, int target) {
		final int resId = _glowPadView.getResourceIdForTarget(target);
		switch (resId) {
		case R.drawable.item_check:
			if (!_preferanceHelperACH.getIsValidSettings())
			{
				_isACH = true;
				showErrorTopDialog("Settings Required", "Please review settings to continue.");
			}
			else
			{
				Intent intent = new Intent(MainActivity.this
						.getApplicationContext(), SaleActivity.class);
				intent.putExtra("saleType", "check");
				MainActivity.this.startActivity(intent);
			}
			break;

		case R.drawable.item_credit:
			if (!_preferanceHelperCC.getIsValidSettings())
			{
				_isACH = false;
				showErrorTopDialog("Settings Required", "Please review settings to continue.");
			}
			else
			{
				Intent intent = new Intent(MainActivity.this
						.getApplicationContext(), SaleActivity.class);
				intent.putExtra("saleType", "credit");
				MainActivity.this.startActivity(intent);
			}

			break;
		default:
			// Code should never reach here.
		}

	}

	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub
		
	}

}
