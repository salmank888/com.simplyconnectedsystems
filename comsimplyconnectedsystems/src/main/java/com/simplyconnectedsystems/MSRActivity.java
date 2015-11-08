package com.simplyconnectedsystems;

import jpos.JposConst;
import jpos.JposException;
import jpos.MSR;
import jpos.MSRConst;
import jpos.POSPrinter;
import jpos.events.DataEvent;
import jpos.events.DataListener;

import com.simplyconnectedsystems.utility.UniMagTopDialog;
import com.sk.simplyconnectedsystems.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class MSRActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, DataListener{

	private MSR msr;
	
	private EditText logicalNameEditText;
	private TextView stateTextView;
	
	private CheckBox deviceEnabledCheckBox;
	private CheckBox autoDisableCheckBox;
	private CheckBox dataEventEnabledCheckBox;
	
	private TextView track1DataLengthTextView;
	private TextView track1DataTextView;
	private TextView track2DataLengthTextView;
	private TextView track2DataTextView;
	private TextView track3DataLengthTextView;
	private TextView track3DataTextView;
	
	private CountDownTimer countDownTimer;

	private UniMagTopDialog dlgErrorTopShow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msr_activity);	
		POSPrinter posPrinter = new POSPrinter(this);
		msr = new MSR();
		msr.addDataListener(this);
		
     logicalNameEditText = (EditText) findViewById(R.id.editTextLogicalName);
		
		findViewById(R.id.buttonOpen).setOnClickListener(this);
		findViewById(R.id.buttonClaim).setOnClickListener(this);
		findViewById(R.id.buttonRelease).setOnClickListener(this);
		findViewById(R.id.buttonClose).setOnClickListener(this);
		findViewById(R.id.buttonInfo).setOnClickListener(this);
		findViewById(R.id.buttonCheckHealth).setOnClickListener(this);
		findViewById(R.id.buttonClearFields).setOnClickListener(this);
		findViewById(R.id.buttonRefreshFields).setOnClickListener(this);
		
		deviceEnabledCheckBox = (CheckBox) findViewById(R.id.checkBoxDeviceEnabled);
		deviceEnabledCheckBox.setOnCheckedChangeListener(this);
		autoDisableCheckBox = (CheckBox) findViewById(R.id.checkBoxAutoDisable);
		autoDisableCheckBox.setOnCheckedChangeListener(this);
		dataEventEnabledCheckBox = (CheckBox) findViewById(R.id.checkBoxDataEventEnabled);
		dataEventEnabledCheckBox.setOnCheckedChangeListener(this);
		
		track1DataLengthTextView = (TextView) findViewById(R.id.textViewTrack1DataLength);
		track1DataTextView = (TextView) findViewById(R.id.textViewTrack1Data);
		track2DataLengthTextView = (TextView) findViewById(R.id.textViewTrack2DataLength);
		track2DataTextView = (TextView) findViewById(R.id.textViewTrack2Data);
		track3DataLengthTextView = (TextView) findViewById(R.id.textViewTrack3DataLength);
		track3DataTextView = (TextView) findViewById(R.id.textViewTrack3Data);
		
		stateTextView = (TextView) findViewById(R.id.textViewState);
		
		}

	@Override
	public void onResume() {
		super.onResume();
		
		countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				stateTextView.setText(getStatusString(msr.getState()));
			}
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
		}.start();
	}
	

	@Override
	public void onPause() {
		super.onPause();
		
		countDownTimer.cancel();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		try {
			msr.close();
		} catch (JposException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.checkBoxDeviceEnabled:
			try {
				msr.setDeviceEnabled(isChecked);
			} catch (JposException e) {
				e.printStackTrace();
				try {
					msr.setDeviceEnabled(!isChecked);
				} catch (JposException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.checkBoxAutoDisable:
			try {
				msr.setAutoDisable(isChecked);
			} catch (JposException e) {
				e.printStackTrace();
				try {
					msr.setAutoDisable(!isChecked);
				} catch (JposException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.checkBoxDataEventEnabled:
			try {
				msr.setDataEventEnabled(isChecked);
			} catch (JposException e) {
				e.printStackTrace();
				try {
					msr.setDataEventEnabled(!isChecked);
				} catch (JposException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonOpen:
			String logicalDeviceName = logicalNameEditText.getText().toString();
			try {
				msr.open(logicalDeviceName);
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonClaim:
			try {
				msr.claim(0);
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonRelease:
			try {
				msr.release();
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonClose:
			try {
				msr.close();
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonInfo:
			try {
				info();
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonCheckHealth:
			checkHealth();
			break;
			
		case R.id.buttonRefreshFields:
			try {
				refreshFields();
			} catch (JposException e) {
				e.printStackTrace();
				showErrorTopDialog("Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonClearFields:
			track1DataTextView.setText("");
			track2DataTextView.setText("");
			track3DataTextView.setText("");
			break;
		}
	}
	
	@Override
	public void dataOccurred(final DataEvent e) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					refreshFields();
					
					track1DataLengthTextView.setText(Integer.toString(e.getStatus() & 0xff));
					track2DataLengthTextView.setText(Integer.toString((e.getStatus() & 0xff00) >> 8));
					track3DataLengthTextView.setText(Integer.toString((e.getStatus() & 0xff0000) >> 16));
					
					deviceEnabledCheckBox.setChecked(msr.getDeviceEnabled());
					dataEventEnabledCheckBox.setChecked(msr.getDataEventEnabled());
					autoDisableCheckBox.setChecked(msr.getAutoDisable());
				} catch (JposException e) {
					e.printStackTrace();
					showErrorTopDialog("Excepction", e.getMessage());
				}
			}
		});
	}
	
	private void info() throws JposException {
		String message = "deviceServiceDescription: " + msr.getDeviceServiceDescription()
				+ "\ndeviceServiceVersion: " + msr.getDeviceServiceVersion()
				+ "\nphysicalDeviceDescription: " + msr.getPhysicalDeviceDescription()
				+ "\nphysicalDeviceName: " + msr.getPhysicalDeviceName()
				+ "\npowerState: " + getPowerStateString(msr.getPowerState())
				+ "\ncapDataEncryption: " + getDataEncryptionString(msr.getCapDataEncryption())
				+ "\ndataEncryptionAlgorithm: " + getDataEncryptionString(msr.getDataEncryptionAlgorithm())
				+ "\ntracksToRead: " + getTrackToReadString(msr.getTracksToRead());
		       showErrorTopDialog("Info", message);

	}
	
	private String getDataEncryptionString(int dataEncryption) {
		switch (dataEncryption) {
		case MSRConst.MSR_DE_NONE:
			return "Data encryption is not enabled";
			
		case MSRConst.MSR_DE_3DEA_DUKPT:
			return "Triple DEA encryption";
			
			default:
				return "Additional encryption algorithms supported";
		}
	}
	
	private void checkHealth() {
		try {
			msr.checkHealth(JposConst.JPOS_CH_INTERNAL);
		       showErrorTopDialog("checkHealth", msr.getCheckHealthText());
//			MessageDialogFragment.showDialog(getFragmentManager(), "checkHealth", msr.getCheckHealthText());
		} catch (JposException e) {
			showErrorTopDialog("Excepction", e.getMessage());
		}
	}
	
	private void refreshFields() throws JposException {
		track1DataTextView.setText(new String(msr.getTrack1Data()));
		track2DataTextView.setText(new String(msr.getTrack2Data()));
		track3DataTextView.setText(new String(msr.getTrack3Data()));
	}
	
	private String getTrackToReadString(int tracksToRead) {
		switch (tracksToRead) {
		case MSRConst.MSR_TR_1:
			return "Track 1";
			
		case MSRConst.MSR_TR_2:
			return "Track 2";
			
		case MSRConst.MSR_TR_3:
			return "Track 3";
			
		case MSRConst.MSR_TR_1_2:
			return "Track 1 and 2";
			
		case MSRConst.MSR_TR_2_3:
			return "Track 2 and 3";
			
		case MSRConst.MSR_TR_1_2_3:
			return "Track 1, 2 and 3";
			
			default:
				return "MSR does not support reading track data";
		}
	}

	static String getStatusString(int state){
		switch (state){
		case JposConst.JPOS_S_BUSY:
			return "JPOS_S_BUSY";
			
		case JposConst.JPOS_S_CLOSED:
			return "JPOS_S_CLOSED";
			
		case JposConst.JPOS_S_ERROR:
			return "JPOS_S_ERROR";
			
		case JposConst.JPOS_S_IDLE:
			return "JPOS_S_IDLE";
			
		default:
			return "Unknown State";
		}
    }
	
	static String getPowerStateString(int powerState) {
		switch (powerState) {
		case JposConst.JPOS_PS_OFF_OFFLINE:
			return "OFFLINE";
			
		case JposConst.JPOS_PS_ONLINE:
			return "ONLINE";
			
			default:
				return "Unknown";
		}
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
}
