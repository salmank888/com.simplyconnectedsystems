package com.simplyconnectedsystems.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.simplyconnectedsystems.TransConfirmActivity;
import com.simplyconnectedsystems.domain.SignatureRequest;
import com.simplyconnectedsystems.domain.SignatureResponse;
import com.simplyconnectedsystems.domain.TransactionResponse;
import com.sk.simplyconnectedsystems.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureSignature extends Activity {

	LinearLayout mContent;
	signature mSignature;
	Button mClear, mGetSign, mCancel;
	public static String tempDir;
	public int count = 1;
	public String current = null;
	private Bitmap mBitmap;
	View mView;
	File filePath;
	File directory;

	private TextView referenceNum;
	private UniMagTopDialog dlgErrorTopShow;
	private CreditPreferenceHelper _creditPreferenceHelper;
    private ACHPreferanceHelper _achPreferenceHelper;
    private TransactionResponse _transResponse;
	private String _saleAmount;
    private String _transType;

    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signature_layout);

		_creditPreferenceHelper = new CreditPreferenceHelper(this.getApplicationContext());
        _achPreferenceHelper = new ACHPreferanceHelper(this.getApplicationContext());

        Bundle extras = getIntent().getExtras();
		_transResponse = (TransactionResponse) extras
				.getSerializable("transResponse");
		_saleAmount = (String) extras.getSerializable("saleAmount");
        _transType = (String) extras.getSerializable("transType");
 		mContent = (LinearLayout) findViewById(R.id.linearLayout);
		mSignature = new signature(this, null);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
			mSignature.setBackground(getResources().getDrawable(R.layout.signature_border));
		else
			mSignature.setBackgroundDrawable(getResources().getDrawable(R.layout.signature_border));
		mContent.addView(mSignature, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mClear = (Button) findViewById(R.id.clear);
		mGetSign = (Button) findViewById(R.id.getsign);
		mGetSign.setEnabled(false);
		mCancel = (Button) findViewById(R.id.cancel);
		mView = mContent;

		referenceNum = (TextView) findViewById(R.id.referenceNumber);
		referenceNum.setTextSize((float) 15.0);
		
		referenceNum.setText("      Transaction ID            " + _transResponse.getTransactionid());
		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v("log_tag", "Panel Cleared");
				mSignature.clear();
				mGetSign.setEnabled(false);
			}
		});

		mGetSign.setOnClickListener(new OnClickListener() {
			private SignatureRequest signRequest;

			public void onClick(View v) {
				Log.v("log_tag", "Panel Saved");
				boolean error = captureSignature();
				if (!error) {
					mView.setDrawingCacheEnabled(true);
					mSignature.save(mView);
					
					Bitmap bm;
					BitmapFactory.Options options = new BitmapFactory.Options();

					options.inSampleSize = 1;
					options.inPurgeable = true;

						File f = new File(directory.getAbsolutePath(), "signature_simplyconnectedsystems.jpg");
						try {
							bm = BitmapFactory.decodeStream(new FileInputStream(f));
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							bm.compress(Bitmap.CompressFormat.PNG, 40, baos);
							// generate base64 string of image
							String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
							signRequest = new SignatureRequest();
                            if(_transType.equalsIgnoreCase("credit")) {
                                signRequest.set_gateway_id(_creditPreferenceHelper.getGateway_id());
                                signRequest.set_merchant(_creditPreferenceHelper.getTransaction_center_id());
                            }else
                            {
                                signRequest.set_gateway_id(_achPreferenceHelper.getGateway_id());
                                signRequest.set_merchant(_achPreferenceHelper.getTransaction_center_id());
                            }
							signRequest.set_operation_type("add_signature");
							signRequest.set_processor("fifththird");
							signRequest.set_reference_number(_transResponse.getTransactionid());
							signRequest.set_signature(encodedImage);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						new SendToGatewayAsync().execute(Constants._1stPAY_URL, signRequest.getAddSignatureUrl());

				}
			}
		});

		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.v("log_tag", "Panel Canceled");
				Bundle b = new Bundle();
				b.putString("url", "");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

	private void showErrorTopDialog(String strTitle, String errorMessage) {
//		hideSwipeTopDialog();
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
	@Override
	protected void onDestroy() {
		Log.w("GetSignature", "onDestory");
		super.onDestroy();
	}

	private boolean captureSignature() {

		boolean error = false;
		String errorMessage = "";

		if (error) {
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 105, 50);
			toast.show();
		}

		return error;
	}

	public class signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private final Paint paint = new Paint();
		private final Path path = new Path();

		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public signature(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public void save(View v) {
			Log.v("log_tag", "Width: " + v.getWidth());
			Log.v("log_tag", "Height: " + v.getHeight());

			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
			}
			Canvas canvas = new Canvas(mBitmap);
			try {
				v.draw(canvas);

				ContextWrapper cw = new ContextWrapper(getApplicationContext());
				// path to /data/data/yourapp/app_data/imageDir
				directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
				// Create imageDir
				File mypath = new File(directory, "signature_simplyconnectedsystems.jpg");

				FileOutputStream fos = null;

				fos = new FileOutputStream(mypath);

				// Use the compress method on the BitMap object to write image to the OutputStream
				mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
				fos.close();


			} catch (Exception e) {
				Log.v("log_tag", e.toString());
			}
		}
		public void clear() {
			path.reset();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			mGetSign.setEnabled(true);

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN :
					path.moveTo(eventX, eventY);
					lastTouchX = eventX;
					lastTouchY = eventY;
					return true;

				case MotionEvent.ACTION_MOVE :

				case MotionEvent.ACTION_UP :

					resetDirtyRect(eventX, eventY);
					int historySize = event.getHistorySize();
					for (int i = 0; i < historySize; i++) {
						float historicalX = event.getHistoricalX(i);
						float historicalY = event.getHistoricalY(i);
						expandDirtyRect(historicalX, historicalY);
						path.lineTo(historicalX, historicalY);
					}
					path.lineTo(eventX, eventY);
					break;

				default :
					debug("Ignored touch event: " + event.toString());
					return false;
			}

			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH), (int) (dirtyRect.right + HALF_STROKE_WIDTH), (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		private void debug(String string) {
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}

			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
		
	}
	
	
	class SendToGatewayAsync extends AsyncTask<String, String, String> {

		private ProgressDialog _authProgressDialog;

		@Override
		protected void onPreExecute() {

			_authProgressDialog = ProgressDialog.show(CaptureSignature.this, "",
					"Saving Signature...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String _transactionURL = params[0];
			String _transactionDATA = params[1];

			String _response = null;

			HttpClient httpclient = new DefaultHttpClient();
//			httpclient.getParams().setIntParameter("http.socket.timeout",
//			5000);
			HttpPost httppost = new HttpPost(_transactionURL);

			try {
			    StringEntity se = new StringEntity(_transactionDATA, HTTP.UTF_8);
				se.setContentType("application/xml");
			    httppost.setEntity(se);

			    HttpResponse _httpresponse = httpclient.execute(httppost);
			    HttpEntity _resEntity = _httpresponse.getEntity();
				if (_resEntity != null) {
				_response = EntityUtils.toString(_resEntity);
			}
			} catch (ClientProtocolException e) {
				_response = null;
			} catch (IOException e) {
				_response = null;
			}

			return _response;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {

				@SuppressWarnings("unused")
				SignatureResponse transResponse = null;
				try {
					transResponse = new SignatureResponse(
							result);
				} catch (Exception e) {
					showErrorTopDialog("Processing Error",
							e.toString());
				}

				_authProgressDialog.dismiss();
//				new SendToGatewayAsync().execute(transRequest
//						.getBaseUrl(), transRequest.getTransactionUrl(true));
				
				if (transResponse.getStatus().equalsIgnoreCase("1")) {
					Intent intent = new Intent(CaptureSignature.this, TransConfirmActivity.class);
					intent.putExtra("transResponse", _transResponse);
					intent.putExtra("saleAmount", _saleAmount);
					intent.putExtra("url", directory.getAbsolutePath());
					startActivity(intent);
					finish();
				} else {

					showErrorTopDialog("Processing Error",
							transResponse.getResponsetext());
				}
			} else {
				_authProgressDialog.dismiss();
				showErrorTopDialog("Processing Error", "Connection Error");
			}

			super.onPostExecute(result);
		}

	}
	
	
	
	
	
	
	
	
}