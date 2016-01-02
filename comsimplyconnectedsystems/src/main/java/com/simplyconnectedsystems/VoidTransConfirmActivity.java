package com.simplyconnectedsystems;

import com.simplyconnectedsystems.domain.VoidResponse;
import com.simplyconnectedsystems.utility.GeneralPreferenceHelper;
import com.sk.simplyconnectedsystems.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VoidTransConfirmActivity extends AppCompatActivity {

	VoidResponse _voidTransResponse;

	Button _btnOk;

	TextView _txtSaleAmountValue;
	TextView _txtTransactionDateValue;
	TextView _txtAuthorizationNumberValue;
	TextView _txtTransactionIdValue;
	TextView _txtOrderIdValue;
	ImageView _imgSignatureValue;
	private GeneralPreferenceHelper _preferanceHelperGeneral;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.void_confirmation_activity);


		_preferanceHelperGeneral = new GeneralPreferenceHelper(this.getApplicationContext());
		setTitle(_preferanceHelperGeneral.getCompanyName());

		Bundle extras = getIntent().getExtras();

		_voidTransResponse = (VoidResponse) extras
				.getSerializable("voidResponse");

		_txtTransactionIdValue = (TextView) findViewById(R.id.txtTransactionIdValue);
		_txtTransactionIdValue.setText(_voidTransResponse.getTransactionid()); // reference number
	
		_btnOk = (Button) findViewById(R.id.btnOk);
		_btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
