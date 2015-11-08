package com.simplyconnectedsystems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.simplyconnectedsystems.domain.TransactionResponse;
import com.sk.simplyconnectedsystems.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TransConfirmActivity extends Activity {

	TransactionResponse _transResponse;

	Button _btnOk;

	TextView _txtSaleAmountValue;
	TextView _txtTransactionDateValue;
	TextView _txtAuthorizationNumberValue;
	TextView _txtTransactionIdValue;
	TextView _txtOrderIdValue;
	ImageView _imgSignatureValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.confirmation_activity);

		Bundle extras = getIntent().getExtras();

		_transResponse = (TransactionResponse) extras
				.getSerializable("transResponse");

		String url = (String) extras.getSerializable("url");
		File f = new File(url, "signature_simplyconnectedsystems.jpg");
		Bitmap b;

		
		_txtSaleAmountValue = (TextView) findViewById(R.id.txtSaleAmountValue);
		_txtSaleAmountValue.setText("$ " + extras.getString("saleAmount"));

		_txtTransactionDateValue = (TextView) findViewById(R.id.txtTransactionDateValue);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		_txtTransactionDateValue.setText(sdf.format(new Date()));

		_txtAuthorizationNumberValue = (TextView) findViewById(R.id.txtAuthorizationNumberValue);
		_txtAuthorizationNumberValue.setText(_transResponse.getAuthcode());

		_txtTransactionIdValue = (TextView) findViewById(R.id.txtTransactionIdValue);
		_txtTransactionIdValue.setText(_transResponse.getTransactionid()); // reference number

		_txtOrderIdValue = (TextView) findViewById(R.id.txtOrderIdValue);
		_txtOrderIdValue.setText(_transResponse.getOrderid());

		_imgSignatureValue = (ImageView) findViewById(R.id.signImage);
		try {
			b = BitmapFactory.decodeStream(new FileInputStream(f));
			_imgSignatureValue.setImageBitmap(b);

		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), "Signature Image \n Not Found", Toast.LENGTH_LONG).show();
		}
		
		_btnOk = (Button) findViewById(R.id.btnOk);
		_btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
