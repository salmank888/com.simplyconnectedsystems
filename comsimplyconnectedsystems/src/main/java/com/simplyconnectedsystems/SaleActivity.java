package com.simplyconnectedsystems;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.simplyconnectedsystems.achsupport.ACH;
import com.simplyconnectedsystems.cardsupport.PGCard;
import com.simplyconnectedsystems.cardsupport.PGEncrypt;
import com.simplyconnectedsystems.cardsupport.PGKeyedCard;
import com.simplyconnectedsystems.cardsupport.PGSwipedCard;
import com.simplyconnectedsystems.cardsupport.Recurring;
import com.simplyconnectedsystems.cardsupport.Void;
import com.simplyconnectedsystems.domain.TransactionRequest;
import com.simplyconnectedsystems.domain.TransactionResponse;
import com.simplyconnectedsystems.domain.VoidResponse;
import com.simplyconnectedsystems.micr.MicrDemoActivity;
import com.simplyconnectedsystems.utility.ACHPreferanceHelper;
import com.simplyconnectedsystems.utility.CaptureSignature;
import com.simplyconnectedsystems.utility.CardData;
import com.simplyconnectedsystems.utility.Constants;
import com.simplyconnectedsystems.utility.CreditPreferenceHelper;
import com.simplyconnectedsystems.utility.CurrencyTextWatcher;
import com.simplyconnectedsystems.utility.GeneralPreferenceHelper;
import com.simplyconnectedsystems.utility.MagStripeCard;
import com.simplyconnectedsystems.utility.ProfileDatabase;
import com.simplyconnectedsystems.utility.SegmentedGroup;
import com.simplyconnectedsystems.utility.UniMagTopDialog;
import com.simplyconnectedsystems.utility.UtilsClass;
import com.sk.simplyconnectedsystems.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import leadtools.demos.Messager;

public class SaleActivity extends AppCompatActivity implements uniMagReaderMsg, OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private uniMagReader _myUniMagReader = null;
    private ProfileDatabase profileDatabase = null;
    private StructConfigParameters profile = null;

    CreditPreferenceHelper _creditPreferenceHelper;
    ACHPreferanceHelper _achPreferenceHelper;
    Recurring _creditRecurring;
    Recurring _achRecurring;
    Void _achVoid;
    Void _creditVoid;
    PGEncrypt _pg;
    String _transInfo;

    ProgressDialog _authProgressDialog;
    ProgressDialog _cardProgressDialog;

    ScrollView _scrollViewSale;
    SegmentedGroup _segmented3;
    RadioButton _radioBtnSale;
    RadioButton _radioBtnVoid;
    RadioButton _radioBtnRecurring;
    RadioButton _radioBtnAuthOnly;
    RadioButton _radioBtnCurrent;
    EditText _saleAmount;
    EditText _ccnumber;
    EditText _ccexp;
    EditText _cvv;
    EditText _orderId;
    EditText _aba;
    EditText _dda;
    EditText _chequeNumber;
    RadioGroup _accountType;
    //    EditText _closeDate;
//    EditText _achName;
    EditText _ownerName;
    //    EditText _street1;
//    EditText _street2;
/*    EditText _city;
    Spinner _state;
    EditText _zip;*/
/*    EditText _email;
    EditText _phone;*/
    EditText _recurring;
    EditText _recurringStartDate;
    Spinner _recurringType;
    EditText _recurringEndDate;
    EditText _referenceNumber;

    TextView _saleAmountLbl;
    TextView _orderIdLbl;
    TextView _ccnumberLbl;
    TextView _ccexpLbl;
    TextView _cvvLbl;
    TextView _abaLbl;
    TextView _ddaLbl;
    TextView _chequeNumberLbl;
    TextView _accountTypeLbl;
    //    TextView _closeDateLbl;
//    TextView _achNameLbl;
    TextView _ownerNameLbl;
    //    TextView _street1Lbl;
//    TextView _street2Lbl;
/*    TextView _cityLbl;
    TextView _stateLbl;
    TextView _zipLbl;*/
/*    TextView _emailLbl;
    TextView _phoneLbl;*/
    TextView _recurringLbl;
    TextView _recurringStartDateLbl;
    TextView _recurringTypeLbl;
    TextView _recurringEndDateLbl;
    TextView _referenceNumberLbl;


    //    LinearLayout _layoutPaymentBtns;
    Button _btnSubmitCard;
    Button _btnReadCheck;
    //    Button _btnSwipeCardMSR;
    Button _btnScanCard;
    Button _btnSwipeCardAJR;


    // // // update the powerup status
    // private int percent = 0;
    // private long beginTime = 0;
    private String a;
    private String popupDialogMsg = null;
    private int keyDel;

    private Handler handler = new Handler();

    private UniMagTopDialog dlgTopShow = null;
    private UniMagTopDialog dlgSwipeTopShow = null;
    private UniMagTopDialog dlgErrorTopShow = null;
    private boolean isWaitingForCommandResult = false;
    private boolean isReaderConnected = false;

    private byte[] msrData = null;
    private byte[] ksnData = null;
    private String errorMsg = null;

    MagStripeCard _magStripeCard = null;
    CardData _cardData = null;
    TransactionRequest transRequest;
    private String _saleActivityType;


    private boolean isUseAutoConfigProfileChecked;
    private boolean creditSaleRequest;
    private boolean creditVoidRequest;
    private boolean creditRecurringRequest;


    private boolean achSaleRequest;
    private boolean achVoidRequest;
    private boolean achRecurringRequest;

    private int readerType = -1; // 0: UniMag, 1: UniMag II
    private long beginTime = 0;
    private String accType;
    private StringBuffer hexKsnString;
    private final Handler handlerForCCNum = new Handler();
    private final char[] creditCardNumber = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private boolean isCCNumEnter = true;
    private String ccNumber;
    private GeneralPreferenceHelper _preferanceHelperGeneral;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity);

        _preferanceHelperGeneral = new GeneralPreferenceHelper(this.getApplicationContext());
        setTitle(_preferanceHelperGeneral.getCompanyName());


        UtilsClass.requestPermission(SaleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, "Request for Writing External Storage", Constants.PermissionRequestCodes.WRITE_EXTERNAL_STORAGE);
        UtilsClass.requestPermission(SaleActivity.this, Manifest.permission.RECORD_AUDIO, "Request for Accessing Microphone", Constants.PermissionRequestCodes.SYSTEM_MICROPHONE);

        _creditPreferenceHelper = new CreditPreferenceHelper(this.getApplicationContext());
        _achPreferenceHelper = new ACHPreferanceHelper(this.getApplicationContext());

        Bundle extras = getIntent().getExtras();

        _saleActivityType = (String) extras
                .getSerializable("saleType");


        profileDatabase = new ProfileDatabase(this);
        profileDatabase.initializeDB();
        profileDatabase.checkOnUseAutoConfigProfile();
//        isUseAutoConfigProfileChecked = profileDatabase.getIsUseAutoConfigProfile();

        if (_saleActivityType.equalsIgnoreCase("credit")) {

            if (UtilsClass.checkPermission(SaleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && UtilsClass.checkPermission(SaleActivity.this, Manifest.permission.RECORD_AUDIO))
                initializeReader();
            else{
                Messager.showGrantPermissionsMessage(this, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

//                return;
            }


        }
//            openReaderSelectDialog();

        _pg = new PGEncrypt();

        // to prevent screen timeout
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // get values from view
        _scrollViewSale = (ScrollView) findViewById(R.id.scrollViewSaleActivity);


        _segmented3 = (SegmentedGroup) findViewById(R.id.segmented2);
        _segmented3.setTintColor(Color.DKGRAY);
        _segmented3.setOnCheckedChangeListener(this);

        _radioBtnSale = (RadioButton) findViewById(R.id.btnSale);

        _radioBtnVoid = (RadioButton) findViewById(R.id.btnVoid);

        _radioBtnRecurring = (RadioButton) findViewById(R.id.btnRecurring);

        _saleAmountLbl = (TextView) findViewById(R.id.txtSaleAmountLabel);
        _saleAmount = (EditText) findViewById(R.id.txtSaleAmount);
        _saleAmount.setText("0.00");

        _saleAmount.setSelection(_saleAmount.getText().length());
        _saleAmount.addTextChangedListener(new CurrencyTextWatcher());

        _ccnumber = (EditText) findViewById(R.id.txtCreditCardNumber);
        _ccnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    handlerForCCNum.removeCallbacksAndMessages(null);

                    String TempCCNum = "";

                    for (int i = 0; i < _ccnumber.getText().toString().trim().length(); i++) {

                        // if (i == 0) {
                        // if (ttfCCNumber.getText().toString().trim().length() < creditCardNumber.toString().trim().length()) {
                        // for (int j = ttfCCNumber.getText().toString().trim().length(); j < 16; j++)
                        // creditCardNumber[j] = ' ';
                        // }
                        // }

                        char c = s.charAt(i);
                        if (i <= 11) {
                            if (i < _ccnumber.getText().toString().trim().length() - 1) {
                                TempCCNum = TempCCNum + "*";
                            } else {
                                TempCCNum = TempCCNum + c;
                                handlerForCCNum.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        String TempCCNum = "";
                                        for (int i = 0; i < _ccnumber.getText().toString().trim().length(); i++) {

                                            char c = _ccnumber.getText().toString().charAt(i);
                                            if (i <= 11) {

                                                TempCCNum = TempCCNum + "*";

                                            } else {
                                                TempCCNum = TempCCNum + c;
                                            }
                                            if (c != '*')
                                                creditCardNumber[i] = c;

                                            if (i == _ccnumber.getText().toString().trim().length() - 1) {
                                                for (int j = _ccnumber.getText().toString().trim().length(); j < 16; j++)
                                                    creditCardNumber[j] = ' ';
                                            }
                                        }

                                        if (isCCNumEnter) {
                                            isCCNumEnter = false;
                                            _ccnumber.setText(TempCCNum);
                                            _ccnumber.setSelection(TempCCNum.length());
                                        } else
                                            isCCNumEnter = true;

                                        if (_ccnumber.getText().toString().trim().length() == 0) {
                                            for (int j = 0; j < 16; j++)
                                                creditCardNumber[j] = ' ';
                                        }
                                    }
                                }, 1000);
                            }
                        } else {
                            TempCCNum = TempCCNum + c;
                        }
                        if (c != '*')
                            creditCardNumber[i] = c;

                        if (i == _ccnumber.getText().toString().trim().length() - 1) {
                            for (int j = _ccnumber.getText().toString().trim().length(); j < 16; j++)
                                creditCardNumber[j] = ' ';
                        }
                    }

                    if (isCCNumEnter) {
                        isCCNumEnter = false;
                        _ccnumber.setText(TempCCNum);
                        _ccnumber.setSelection(TempCCNum.length());
                    } else
                        isCCNumEnter = true;

                    if (_ccnumber.getText().toString().trim().length() == 0) {
                        for (int j = 0; j < 16; j++)
                            creditCardNumber[j] = ' ';
                    }

                    if ((_ccnumber.getText().length() > 12)) {
                        ccNumber = new String(creditCardNumber);
                    }

                    // creditCardNumber = ttfCCNumber.getText().toString();
                } catch (Exception e) {
//                    handleException("[Exception in ttffcnumber on text changed listener]" + "[createpaymentview]" + "[" + e.getLocalizedMessage() + "]");
                }

            }
        });
        _ccnumberLbl = (TextView) findViewById(R.id.txtCreditCardNumberLabel);

        _ccexp = (EditText) findViewById(R.id.txtCreditCardExpiration);
        _ccexpLbl = (TextView) findViewById(R.id.txtCreditCardExpirationLabel);


        _cvv = (EditText) findViewById(R.id.txtCCVNumber);
        _cvvLbl = (TextView) findViewById(R.id.txtCCVNumberLabel);

        _orderIdLbl = (TextView) findViewById(R.id.txtOrderIdLabel);
        _orderId = (EditText) findViewById(R.id.txtOrderId);
        // _orderId.setText("1234");
        _aba = (EditText) findViewById(R.id.txtABAId);
        _abaLbl = (TextView) findViewById(R.id.txtABALabel);

        _dda = (EditText) findViewById(R.id.txtDDAId);
        _ddaLbl = (TextView) findViewById(R.id.txtDDALabel);

        _chequeNumber = (EditText) findViewById(R.id.txtChequeNumId);
        _chequeNumberLbl = (TextView) findViewById(R.id.txtChequeNumLabel);

        _radioBtnCurrent = (RadioButton) findViewById(R.id.radio_button_current);

        _accountType = (RadioGroup) findViewById(R.id.txtAccountTypeId);
        _accountType.setOnCheckedChangeListener(this);

        _accountTypeLbl = (TextView) findViewById(R.id.txtAccountTypeLabel);

/*        _closeDate = (EditText) findViewById(R.id.txtCloseDateId);
        _closeDateLbl = (TextView) findViewById(R.id.txtCloseDateLabel);
        _closeDate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = _closeDate.getText().toString().split("/");
                if (flag) {
                    _closeDate.setOnKeyListener(new View.OnKeyListener() {

                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((_closeDate.getText().length() + 1) % 3) == 0) {
                            if (_closeDate.getText().toString().split("/").length <= 2) {
                                _closeDate.setText(_closeDate.getText() + "/");
                                _closeDate.setSelection(_closeDate.getText().length());
                            }
                        }
                        a = _closeDate.getText().toString();
                    } else {
                        a = _closeDate.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    _closeDate.setText(a);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }

        });*/

/*        _achName = (EditText) findViewById(R.id.txtACHNameId);
        _achNameLbl = (TextView) findViewById(R.id.txtACHNameLabel);*/

        _ownerName = (EditText) findViewById(R.id.txtOwnerNameId);
        _ownerNameLbl = (TextView) findViewById(R.id.txtOwnerNameLabel);

//        _street1 = (EditText) findViewById(R.id.txtStreet1Id);
//        _street1Lbl = (TextView) findViewById(R.id.txtStreet1Label);

/*        _street2 = (EditText) findViewById(R.id.txtStreet2Id);
        _street2Lbl = (TextView) findViewById(R.id.txtStreet2Label);*/

/*        _city = (EditText) findViewById(R.id.txtCityId);
        _cityLbl = (TextView) findViewById(R.id.txtCityLabel);

        _state = (Spinner) findViewById(R.id.txtStateId);*/
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> statesAdapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the statesAdapter to the spinner
/*        _state.setAdapter(statesAdapter);
        _state.setOnItemSelectedListener(this);

        _stateLbl = (TextView) findViewById(R.id.txtStateLabel);

        _zip = (EditText) findViewById(R.id.txtZipId);
        _zipLbl = (TextView) findViewById(R.id.txtZipLabel);*/

/*
        _email = (EditText) findViewById(R.id.txtEmailId);
        _emailLbl = (TextView) findViewById(R.id.txtEmailLabel);
*/

        /*_phone = (EditText) findViewById(R.id.txtPhoneId);
        _phoneLbl = (TextView) findViewById(R.id.txtPhoneLabel);
        _phone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = _phone.getText().toString().split("-");
                if (flag) {
                    _phone.setOnKeyListener(new View.OnKeyListener() {

                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((_phone.getText().length() + 1) % 4) == 0) {
                            if (_phone.getText().toString().split("-").length <= 2) {
                                _phone.setText(_phone.getText() + "-");
                                _phone.setSelection(_phone.getText().length());
                            }
                        }
                        a = _phone.getText().toString();
                    } else {
                        a = _phone.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    _phone.setText(a);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }

        });*/

        _recurring = (EditText) findViewById(R.id.txtRecurringId);
        _recurringLbl = (TextView) findViewById(R.id.txtRecurringLabel);

        _recurringStartDate = (EditText) findViewById(R.id.txtRecurringStartDateId);
        _recurringStartDateLbl = (TextView) findViewById(R.id.txtRecurringStartDateLabel);
        _recurringStartDate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = _recurringStartDate.getText().toString().split("/");
                if (flag) {
                    _recurringStartDate.setOnKeyListener(new View.OnKeyListener() {

                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((_recurringStartDate.getText().length() + 1) % 3) == 0) {
                            if (_recurringStartDate.getText().toString().split("/").length <= 2) {
                                _recurringStartDate.setText(_recurringStartDate.getText() + "/");
                                _recurringStartDate.setSelection(_recurringStartDate.getText().length());
                            }
                        }
                        a = _recurringStartDate.getText().toString();
                    } else {
                        a = _recurringStartDate.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    _recurringStartDate.setText(a);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }

        });

        _recurringType = (Spinner) findViewById(R.id.txtRecurringTypeId);

        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.frequency, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _recurringType.setAdapter(frequencyAdapter);
        _recurringType.setOnItemSelectedListener(this);

        _recurringTypeLbl = (TextView) findViewById(R.id.txtRecurringTypeLabel);

        _recurringEndDate = (EditText) findViewById(R.id.txtRecurringEndDateId);
        _recurringEndDateLbl = (TextView) findViewById(R.id.txtRecurringEndDateLabel);
        _recurringEndDate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = _recurringEndDate.getText().toString().split("/");
                if (flag) {
                    _recurringEndDate.setOnKeyListener(new View.OnKeyListener() {

                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((_recurringEndDate.getText().length() + 1) % 3) == 0) {
                            if (_recurringEndDate.getText().toString().split("/").length <= 2) {
                                _recurringEndDate.setText(_recurringEndDate.getText() + "/");
                                _recurringEndDate.setSelection(_recurringEndDate.getText().length());
                            }
                        }
                        a = _recurringEndDate.getText().toString();
                    } else {
                        a = _recurringEndDate.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    _recurringEndDate.setText(a);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }

        });

        _referenceNumberLbl = (TextView) findViewById(R.id.txtReferenceNumberLabel);
        _referenceNumber = (EditText) findViewById(R.id.txtReferenceNumberId);

//        _layoutPaymentBtns = (LinearLayout) findViewById(R.id.layoutPaymentBtns);
//        _btnSwipeCardMSR = (Button) findViewById(R.id.btnSwipeCardMSR);
        _btnScanCard = (Button) findViewById(R.id.btnScanCard);
        _btnSwipeCardAJR = (Button) findViewById(R.id.btnSwipeCardAJR);
        _btnReadCheck = (Button) findViewById(R.id.btnReadChecque);

        _btnScanCard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(SaleActivity.this, CardIOActivity.class);

                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: true
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

                // hides the manual entry button
                // if set, developers should provide their own manual entry mechanism in the app
                scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(scanIntent, Constants.MY_SCAN_REQUEST_CODE);
            }
        });

        if (_saleActivityType.equalsIgnoreCase("credit")) {

//			 openReaderSelectDialog();

            _ccnumber.setVisibility(View.VISIBLE);
            _ccexp.setVisibility(View.VISIBLE);
            _cvv.setVisibility(View.VISIBLE);

            _ccnumberLbl.setVisibility(View.VISIBLE);
            _ccexpLbl.setVisibility(View.VISIBLE);
            _cvvLbl.setVisibility(View.VISIBLE);

//			 _btnSwipeCardMSR.setVisibility(View.VISIBLE);
            _btnSwipeCardAJR.setVisibility(View.VISIBLE);

            _scrollViewSale.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_SCROLL:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_DOWN:
//                            _layoutPaymentBtns.animate()
//                                    .alpha(0f)
//                                    .setDuration(1000)
//                                    .setListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            _layoutPaymentBtns.setVisibility(View.GONE);
//                                        }
//                                    });
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                        case MotionEvent.ACTION_UP:
//                            _layoutPaymentBtns.animate()
//                                    .alpha(1f)
//                                    .setDuration(1000)
//                                    .setListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            _layoutPaymentBtns.setVisibility(View.VISIBLE);
//                                        }
//                                    });
                            break;
                    }
                    return false;
                }

            });

        } else {
            _aba.setVisibility(View.VISIBLE);
            _dda.setVisibility(View.VISIBLE);
            _accountType.setVisibility(View.VISIBLE);
/*            _closeDate.setVisibility(View.VISIBLE);
            _achName.setVisibility(View.VISIBLE);*/
            _ownerName.setVisibility(View.VISIBLE);
//            _street1.setVisibility(View.VISIBLE);
//            _street2.setVisibility(View.VISIBLE);
/*            _city.setVisibility(View.VISIBLE);
            _state.setVisibility(View.VISIBLE);
            _zip.setVisibility(View.VISIBLE);*/
/*            _email.setVisibility(View.VISIBLE);
            _phone.setVisibility(View.VISIBLE);*/

            _abaLbl.setVisibility(View.VISIBLE);
            _ddaLbl.setVisibility(View.VISIBLE);
            _accountTypeLbl.setVisibility(View.VISIBLE);
/*            _closeDateLbl.setVisibility(View.VISIBLE);
            _achNameLbl.setVisibility(View.VISIBLE);*/
            _ownerNameLbl.setVisibility(View.VISIBLE);
//            _street1Lbl.setVisibility(View.VISIBLE);
//            _street2Lbl.setVisibility(View.VISIBLE);
/*            _cityLbl.setVisibility(View.VISIBLE);
            _stateLbl.setVisibility(View.VISIBLE);
            _zipLbl.setVisibility(View.VISIBLE);*/
/*            _emailLbl.setVisibility(View.VISIBLE);
            _phoneLbl.setVisibility(View.VISIBLE);*/
            _btnReadCheck.setVisibility(View.VISIBLE);
        }

        _btnSubmitCard = (Button) findViewById(R.id.btnSubmit);
        _btnSubmitCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_saleActivityType.equalsIgnoreCase("credit")) {

                    if (_radioBtnSale.isChecked() && _ccnumber.getText().toString().length() > 0
                            && _saleAmount.getText().toString().length() > 0
                            && _ccexp.getText().toString().length() > 0
                            && _ownerName.getText().toString().length() > 0) {

                        creditSaleRequest = true;
                        creditRecurringRequest = false;
                        creditVoidRequest = false;
                    } else if (_radioBtnRecurring.isChecked() && _ccnumber.getText().toString().length() > 0
                            && _saleAmount.getText().toString().length() > 0
                            && _ccexp.getText().toString().length() > 0
                            && _recurring.getText().toString().length() > 0
                            && _recurringStartDate.getText().toString().length() > 0
                            && _recurringType.getSelectedItem().toString().length() > 0
                            && _recurringEndDate.getText().toString().length() > 0) {

                        creditVoidRequest = false;
                        creditSaleRequest = false;
                        creditRecurringRequest = true;
                    } else if (_radioBtnVoid.isChecked() && _referenceNumber.getText().toString().length() > 0) {
                        creditVoidRequest = true;
                        creditSaleRequest = false;
                        creditRecurringRequest = false;
                    }

                    if (creditSaleRequest || creditRecurringRequest) {
                        hideSoftKeyboard();

                        PGCard card = null;

                        if (!isReaderConnected) {
                            card = new PGKeyedCard(ccNumber, _ccexp.getText().toString(), _cvv
                                    .getText().toString(), _ownerName.getText().toString());
                        } else {
                            if (_cardData != null) {
                                card = new PGSwipedCard(_cardData.getEncryptedTrack1(), _cardData.getEncryptedTrack2(), "", _cvv
                                        .getText().toString(), _cardData.getKSN());
                            }
                        }

                        transRequest = new TransactionRequest();

                        transRequest.set_MID(_creditPreferenceHelper.getMID());
                        transRequest.set_TID(_creditPreferenceHelper.getTID());
                        transRequest.set_gateway_id(_creditPreferenceHelper.getGateway_id());
                        transRequest.set_processor(_creditPreferenceHelper.getProcessor());
                        transRequest.set_transaction_center_id(_creditPreferenceHelper.getTransaction_center_id());

                        transRequest.set_type("retail_sale");
                        transRequest
                                .set_amount(_saleAmount.getText().toString());
                        transRequest.set_orderId(_orderId.getText().toString());

                        if (card instanceof PGSwipedCard) {
                            //transRequest.setUnencrypted_payment(_pg.encrypt(card, true));
                            if (creditRecurringRequest) {
                                _creditRecurring = new Recurring(_recurring.getText().toString(), _recurringStartDate.getText().toString(), _recurringType.getSelectedItem().toString(), _recurringEndDate.getText().toString());
                                transRequest.setUnencrypted_payment(((PGSwipedCard) card).getDirectPostString(false) + _creditRecurring.getRecurringDirectPostString());
                            } else
                                transRequest.setUnencrypted_payment(((PGSwipedCard) card).getEncryptedDirectPostString());
                        } else {
                            if (creditRecurringRequest) {
                                _creditRecurring = new Recurring(_recurring.getText().toString(), _recurringStartDate.getText().toString(), _recurringType.getSelectedItem().toString(), _recurringEndDate.getText().toString());
                                transRequest.setUnencrypted_payment(((PGKeyedCard) card).getDirectPostString(false) + _creditRecurring.getRecurringDirectPostString());
                            } else
                                transRequest.setUnencrypted_payment(((PGKeyedCard) card).getDirectPostString(false));
                        }

                        new SendToGatewayAsync().execute(Constants._1stPAY_URL, transRequest.getTransactionUrl(true, false));
                    } else if (creditVoidRequest) {
                        transRequest = new TransactionRequest();

                        transRequest.set_MID(_creditPreferenceHelper.getMID());
                        transRequest.set_TID(_creditPreferenceHelper.getTID());
                        transRequest.set_gateway_id(_creditPreferenceHelper.getGateway_id());
                        transRequest.set_processor(_creditPreferenceHelper.getProcessor());
                        transRequest.set_transaction_center_id(_creditPreferenceHelper.getTransaction_center_id());

                        transRequest.set_type("void");

                        _creditVoid = new Void(_referenceNumber.getText().toString());
                        transRequest.setUnencrypted_payment(_creditVoid.getVoidDirectPostString());

                        new SendToGatewayAsync().execute(Constants._1stPAY_URL, transRequest.getTransactionUrl(true, true));

                    } else {

                        showErrorTopDialog("Fields Required",
                                "Please complete all required fields");
                    }

                } else {

                    if (_radioBtnSale.isChecked() && _aba.getText().toString().length() > 0
                            && _dda.getText().toString().length() > 0
                            && accType.length() > 0
                            && _ownerName.getText().toString().length() > 0
                            ) {

                        achSaleRequest = true;
                        achRecurringRequest = false;
                        achVoidRequest = false;
                    } else if (_radioBtnRecurring.isChecked() && _aba.getText().toString().length() > 0
                            && _dda.getText().toString().length() > 0
                            && accType.length() > 0
                            && _ownerName.getText().toString().length() > 0
                            && _recurring.getText().toString().length() > 0
                            && _recurringStartDate.getText().toString().length() > 0
                            && _recurringType.getSelectedItem().toString().length() > 0
                            && _recurringEndDate.getText().toString().length() > 0) {

                        achVoidRequest = false;
                        achSaleRequest = false;
                        achRecurringRequest = true;
                    } else if (_radioBtnVoid.isChecked() && _referenceNumber.getText().toString().length() > 0) {
                        achVoidRequest = true;
                        achSaleRequest = false;
                        achRecurringRequest = false;
                    }
                    if (achSaleRequest || achRecurringRequest) {

                        hideSoftKeyboard();

                        ACH check = null;

                        check = new ACH("Web Payments", _aba.getText().toString(), _dda.getText().toString(), _chequeNumber.getText().toString(), accType, _ownerName.getText().toString(), "NP", "NP", "NP", "NP", "NP");

                        TransactionRequest transRequest = new TransactionRequest();

                        transRequest.set_MID(_achPreferenceHelper.getMID());
                        transRequest.set_gateway_id(_achPreferenceHelper.getGateway_id());
                        transRequest.set_processor(_achPreferenceHelper.getProcessor());
                        transRequest.set_transaction_center_id(_achPreferenceHelper.getTransaction_center_id());

                        transRequest.set_type("ach_debit");
                        transRequest
                                .set_amount(_saleAmount.getText().toString());
                        transRequest.set_orderId(_orderId.getText().toString());
                        if (check instanceof ACH) {
                            if (achRecurringRequest) {
                                _achRecurring = new Recurring(_recurring.getText().toString(), _recurringStartDate.getText().toString(), _recurringType.getSelectedItem().toString(), _recurringEndDate.getText().toString());
                                transRequest.setUnencrypted_payment(((ACH) check).getDirectPostString() + _achRecurring.getRecurringDirectPostString());
                            } else
                                transRequest.setUnencrypted_payment(((ACH) check).getDirectPostString());

                        }

                        new SendToGatewayAsync().execute(Constants._1stPAY_URL, transRequest.getTransactionUrl(false, false));
                    } else if (achVoidRequest) {
                        transRequest = new TransactionRequest();

                        transRequest.set_MID(_achPreferenceHelper.getMID());
                        transRequest.set_gateway_id(_achPreferenceHelper.getGateway_id());
                        transRequest.set_processor(_achPreferenceHelper.getProcessor());
                        transRequest.set_transaction_center_id(_achPreferenceHelper.getTransaction_center_id());

                        transRequest.set_type("ach_void");

                        _achVoid = new Void(_referenceNumber.getText().toString());
                        transRequest.setUnencrypted_payment(_achVoid.getVoidDirectPostString());

                        new SendToGatewayAsync().execute(Constants._1stPAY_URL, transRequest.getTransactionUrl(false, true));


                    } /*else if (!_email.getText().toString().equalsIgnoreCase("") && !isEmailValid(_email.getText().toString())) {
                        showErrorTopDialog("Invalid Email",
                                "Please provide valid email address");
                    }*/ else {

                        showErrorTopDialog("Fields Required",
                                "Please complete all required fields");
                    }
                }

            }
        });

        _btnReadCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SaleActivity.this, MicrDemoActivity.class), Constants.SCAN_CHEQUE);
            }
        });
        _btnSwipeCardAJR.setEnabled(false);
        _btnSwipeCardAJR.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (_myUniMagReader != null) {
                    if (!isWaitingForCommandResult) {
                        beginTime = getCurrentTime();

                        if (_myUniMagReader.startSwipeCard()) {
                            Log.d("Sale Activity", "to startSwipeCard");
                        } else
                            Log.d("Sale Activity", "cannot startSwipeCard");
                    }
                }
            }
        });

//        _btnSwipeCardMSR.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MSRActivity.class);
//                startActivity(intent);
//            }
//        });

        _radioBtnSale.setChecked(true);
        _radioBtnCurrent.setChecked(true);

//		try {
//			_pg.setKey(_preferenceHelper.getUserEncrpyionKey());
//		} catch (IllegalArgumentException e) {
//			_btnSubmitCard.setEnabled(false);
//			showErrorTopDialog("Invalid Encryption Key",
//					"Please update encryption key in settings_cc.");
//		}
    }


    private void initializeReader() {
        if (_myUniMagReader != null) {
            _myUniMagReader.unregisterListen();
            _myUniMagReader.release();
            _myUniMagReader = null;
        }
//		if (isConnectWithCommand)
//        _myUniMagReader = new uniMagReader(this,this,true);
//		else
        _myUniMagReader = new uniMagReader(this, this);
        if (_myUniMagReader == null)
            return;

        _myUniMagReader.setVerboseLoggingEnable(true);
        _myUniMagReader.registerListen();

        //load the XML configuratin file
        String fileNameWithPath = getConfigurationFileFromRaw();
        if (!isFileExist(fileNameWithPath)) {
            fileNameWithPath = null;
        }

        if (isUseAutoConfigProfileChecked) {
            if (profileDatabase.updateProfileFromDB()) {
                this.profile = profileDatabase.getProfile();
                Toast.makeText(this, "AutoConfig profile has been loaded.", Toast.LENGTH_LONG).show();
                handler.post(doConnectUsingProfile);
            } else {
                _myUniMagReader.startAutoConfig(fileNameWithPath, true);
            }
        } else {
            /////////////////////////////////////////////////////////////////////////////////
            // Network operation is prohibited in the UI Thread if target API is 11 or above.
            // If target API is 11 or above, please use AsyncTask to avoid errors.


                _myUniMagReader.setXMLFileNameWithPath(getConfigurationFileFromRaw());
            try{
                _myUniMagReader.loadingConfigurationXMLFile(false);
            } catch(Exception e){
                Toast.makeText(SaleActivity.this, "Configuration File not Found for this device.", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void initializeReader(uniMagReader.ReaderType type) {
        if (_myUniMagReader != null) {
            _myUniMagReader.unregisterListen();
            _myUniMagReader.release();
            _myUniMagReader = null;
        }
        _myUniMagReader = new uniMagReader(this, this, type);

        if (_myUniMagReader == null)
            return;

        _myUniMagReader.setVerboseLoggingEnable(true);
        _myUniMagReader.registerListen();

        //load the XML configuratin file
        String fileNameWithPath = getConfigurationFileFromRaw();
        if (!isFileExist(fileNameWithPath)) {
            fileNameWithPath = null;
        }

        boolean startAcRet = _myUniMagReader.startAutoConfig(fileNameWithPath, true);

//        if (isUseAutoConfigProfileChecked) {
//            if (profileDatabase.updateProfileFromDB()) {
//                this.profile = profileDatabase.getProfile();
//                Toast.makeText(this, "AutoConfig profile has been loaded.", Toast.LENGTH_LONG).show();
//                handler.post(doConnectUsingProfile);
//            } else {
//                Toast.makeText(this, "No profile found. Please run AutoConfig first.", Toast.LENGTH_LONG).show();
//            }
//        }
        if (!startAcRet) {
            /////////////////////////////////////////////////////////////////////////////////
            // Network operation is prohibited in the UI Thread if target API is 11 or above.
            // If target API is 11 or above, please use AsyncTask to avoid errors.
            _myUniMagReader.setXMLFileNameWithPath(fileNameWithPath);
//			_myUniMagReader.loadingConfigurationXMLFile(true);
            new LoadConfigurationAsync().execute();

        }
        //Initializing SDKTool for firmware update
//		firmwareUpdateTool = new uniMagSDKTools(this,this);
//		firmwareUpdateTool.setUniMagReader(_myUniMagReader);
//		_myUniMagReader.setSDKToolProxy(firmwareUpdateTool.getSDKToolProxy());

    }


    void openReaderSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a reader:");
        builder.setCancelable(false);
        builder.setItems(R.array.reader_type, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        readerType = 0;
                        initializeReader(uniMagReader.ReaderType.UM_OR_PRO);
                        Toast.makeText(getApplicationContext(), "UniMag / UniMag Pro selected", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        readerType = 1;
                        initializeReader(uniMagReader.ReaderType.SHUTTLE);
                        Toast.makeText(getApplicationContext(), "UniMag II / Shuttle selected", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        readerType = 1;
                        initializeReader(uniMagReader.ReaderType.SHUTTLE);
                        Toast.makeText(getApplicationContext(), "UniMag II / Shuttle selected", Toast.LENGTH_SHORT).show();
                        break;
                }
//				showAboutInfo();
            }
        });
        builder.create().show();
    }

    private void clearFields() {
        // _saleAmount.setText("00.0");
        _ccnumber.setText(null);
        _ccexp.setText(null);
        _cvv.setText(null);
        _orderId.setText(null);
    }

    @Override
    protected void onResume() {

        //initializeReader();

        if (_myUniMagReader != null) {
            _myUniMagReader.setSaveLogEnable(false);
        }

        if (CardIOActivity.canReadCardWithCamera(this) && _saleActivityType.equalsIgnoreCase("credit")) {
            _btnScanCard.setVisibility(View.VISIBLE);
        }

        isWaitingForCommandResult = false;
        super.onResume();
    }

    @Override
    protected void onPause() {

        if (_myUniMagReader != null) {
            // stop swipe card when the application go to background
            _myUniMagReader.stopSwipeCard();
        }

        hideTopDialog();
        hideSwipeTopDialog();

        unIinitializeReader();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (_myUniMagReader != null)
            _myUniMagReader.release();
        profileDatabase.closeDB();

        super.onDestroy();
    }

    private void unIinitializeReader() {

        if (_myUniMagReader != null) {
            _myUniMagReader.unregisterListen();
            _myUniMagReader.release();
            _myUniMagReader = null;
        }
    }

    private String getConfigurationFileFromRaw() {
        return getXMLFileFromRaw("umcfg_5_0_5.xml");

    }

    // If 'idt_unimagcfg_default.xml' file is found in the 'raw' folder, it
    // returns the file path.
    private String getXMLFileFromRaw(String fileName) {
        // the target filename in the application path
        String fileNameWithPath = null;
        fileNameWithPath = fileName;

        try {
            InputStream in = getResources().openRawResource(
                    R.raw.idt_unimagcfg_default);

            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();
            deleteFile(fileNameWithPath);
            FileOutputStream fout = openFileOutput(fileNameWithPath,
                    MODE_PRIVATE);
            fout.write(buffer);
            fout.close();

            // to refer to the application path
            File fileDir = this.getFilesDir();
            fileNameWithPath = fileDir.getParent() + java.io.File.separator
                    + fileDir.getName();
            fileNameWithPath += java.io.File.separator + fileName;
            // + "idt_unimagcfg_default.xml";

        } catch (Exception e) {
            e.printStackTrace();
            fileNameWithPath = null;
        }
        return fileNameWithPath;
    }

    private boolean isFileExist(String path) {
        if (path == null)
            return false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sale_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_close) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        if (item.getItemId() == R.id.menu_close) {
//            finish();
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class SendToGatewayAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            _authProgressDialog = ProgressDialog.show(SaleActivity.this, "",
                    "Processing Transaction...");
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

                if (result.contains("total_transactions_voided")) {
                    VoidResponse voidResponse = null;

                    try {
                        voidResponse = new VoidResponse(result);
                    } catch (Exception e) {
                        showErrorTopDialog("Processing Error",
                                e.toString());
                    }

                    _authProgressDialog.dismiss();

                    if (voidResponse.getStatus().equalsIgnoreCase("1")) {
                        Intent intent = new Intent(SaleActivity.this, VoidTransConfirmActivity.class);
                        intent.putExtra("voidResponse", voidResponse);
                        startActivity(intent);
                        finish();

                    } else {

                        showErrorTopDialog("Processing Error",
                                voidResponse.getResponsetext());
                    }
                } else if (!result.contains("total_transactions_voided")) {
                    TransactionResponse transResponse = null;
                    try {
                        transResponse = new TransactionResponse(
                                result);
                    } catch (Exception e) {
                        showErrorTopDialog("Processing Error",
                                e.toString());
                    }

                    _authProgressDialog.dismiss();
//				new SendToGatewayAsync().execute(transRequest
//						.getBaseUrl(), transRequest.getTransactionUrl(true));

                    if (transResponse.getStatus().equalsIgnoreCase("1")) {

                        launchTransConfirmation(transResponse);
                    } else {

                        showErrorTopDialog("Processing Error",
                                transResponse.getResponsetext());
                    }
                }
            } else {
                _authProgressDialog.dismiss();
                showErrorTopDialog("Processing Error", "Connection Error");
            }

            super.onPostExecute(result);
        }

    }

    class LoadConfigurationAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            _authProgressDialog = ProgressDialog.show(SaleActivity.this, "",
                    "Loading Configration...");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                _myUniMagReader.loadingConfigurationXMLFile(true);

            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                _authProgressDialog.dismiss();
            } else {
                _authProgressDialog.dismiss();
                showErrorTopDialog("Processing Error", "Connection Error");
            }

            super.onPostExecute(result);
        }

    }

    private void launchTransConfirmation(TransactionResponse transResponse) {
        Intent intent = new Intent(this, CaptureSignature.class);
        intent.putExtra("transResponse", transResponse);
        intent.putExtra("saleAmount", _saleAmount.getText().toString());
        intent.putExtra("transType", _saleActivityType);
        startActivity(intent);
        finish();

    }

    private void hideSoftKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
                .getWindowToken(), 0);
    }

    @Override
    public boolean getUserGrant(int type, String strMessage) {
        boolean getUserGranted = false;
        switch (type) {
            case uniMagReaderMsg.typeToPowerupUniMag:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToUpdateXML:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToOverwriteXML:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToReportToIdtech:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            default:
                getUserGranted = false;
                break;
        }
        return getUserGranted;
    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters profile) {
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0) {
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0, double arg1, String arg2) {
    }

    @Override
    public void onReceiveMsgCardData(byte flagOfCardData, byte[] cardData) {

        byte flag = (byte) (flagOfCardData & 0x04);

        String strMsrData = null;

        if (flag == 0x00)
            strMsrData = new String(cardData);
        if (flag == 0x04) {
            // You need to decrypt the data here first.
            strMsrData = new String(cardData);
        }

        msrData = new byte[cardData.length];
        System.arraycopy(cardData, 0, msrData, 0, cardData.length);

        try {

            if (_cardData != null) {
                _cardData = null;
            }

//            _magStripeCard = new MagStripeCard(strMsrData);
            _cardData = new CardData(cardData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.post(doHideProcessingCardDlg);

        if (_cardData != null && _cardData.getCreditCardNumber() != null) {
            handler.post(doUpdateCardDataFields);
        } else {
            handler.post(doShowErrorTopDialog);
        }
    }

    @Override
    public void onReceiveMsgCommandResult(int commandID, byte[] cmdReturn) {

//		Log.d("Demo Info >>>>> onReceive commandID="+commandID,",cmdReturn="+ getHexStringFromBytes(cmdReturn));
        isWaitingForCommandResult = false;

        if (cmdReturn.length > 1) {
            if (6 == cmdReturn[0] && (byte) 0x56 == cmdReturn[1]) {
                Toast.makeText(SaleActivity.this, "Failed to send command. Attached reader is in boot loader mode.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (commandID == uniMagReaderMsg.cmdGetNextKSN) {
            if (0 == cmdReturn[0])
                Toast.makeText(SaleActivity.this, "Get Next KSN timeout.", Toast.LENGTH_LONG).show();
            else if (6 == cmdReturn[0])
                try {
                    ksnData = null;
                    ksnData = new byte[10];
                    System.arraycopy(cmdReturn, 2, ksnData, 0, 10);
                    if (ksnData != null) {
                        hexKsnString = new StringBuffer();

                        String fix = null;
                        for (int i = 0; i < ksnData.length; i++) {
                            fix = Integer.toHexString(0xFF & ksnData[i]);
                            if (fix.length() == 1)
                                fix = "0" + fix;
                            hexKsnString.append(fix);
                            if ((i + 1) % 4 == 0 && i != (ksnData.length - 1))
                                hexKsnString.append(' ');
                        }
//                        Toast.makeText(SaleActivity.this, hexKsnString.toString(), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(SaleActivity.this, "Please Try again for KSN.", Toast.LENGTH_LONG).show();

                } catch (Exception ex) {
                    Toast.makeText(SaleActivity.this, "Exception in getting KSN.", Toast.LENGTH_LONG).show();

                }
            else
                Toast.makeText(SaleActivity.this, "Get Next KSN failed.", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onReceiveMsgConnected() {
        isReaderConnected = true;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doHideProcessingCardDlg);
        msrData = null;
        handler.post(doUpdateStatus);
    }

    @Override
    public void onReceiveMsgDisconnected() {
        // percent = 0;
        isReaderConnected = false;
        isWaitingForCommandResult = false;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doHideProcessingCardDlg);
        // handler.post(doUpdateToast);
        handler.post(doUpdateStatus);
    }

    @Override
    public void onReceiveMsgFailureInfo(int arg0, String arg1) {
        errorMsg = arg1;
        handler.post(doErrorToast);
    }

    @Override
    public void onReceiveMsgProcessingCardData() {
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        if (isReaderConnected) {
            popupDialogMsg = "Processing Card Data...";
            handler.post(doShowProcessingCardDlg);
        }
    }

    @Override
    public void onReceiveMsgSDCardDFailed(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgTimeout(String arg0) {

        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        errorMsg = arg0;
        handler.post(doErrorToast);

    }

    @Override
    public void onReceiveMsgToConnect() {
        // beginTime = System.currentTimeMillis();
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        popupDialogMsg = "Powering up Card Reader...";
        handler.post(doShowTopDlg);
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        popupDialogMsg = "Please swipe card.";
        handler.post(doShowSwipeTopDlg);
    }


    private Runnable doHideTopDlg = new Runnable() {
        public void run() {
            hideTopDialog();
        }

    };

    private Runnable doShowSwipeTopDlg = new Runnable() {
        public void run() {
            showSwipeTopDialog("Simply Connected Systems");
        }
    };

    private Runnable doShowTopDlg = new Runnable() {
        public void run() {
            showTopDialog("Simply Connected Systems");
        }
    };

    private Runnable doShowProcessingCardDlg = new Runnable() {
        public void run() {
            showProcessingCardDialog();
        }
    };

    private Runnable doHideProcessingCardDlg = new Runnable() {
        public void run() {
            hideProcessingCardDialog();
        }
    };

    private void showProcessingCardDialog() {
        _cardProgressDialog = ProgressDialog.show(SaleActivity.this, "",
                popupDialogMsg);
    }

    private void hideProcessingCardDialog() {
        if (_cardProgressDialog != null) {
            _cardProgressDialog.dismiss();
        }
    }

    private void showTopDialog(String strTitle) {
        hideTopDialog();
        if (dlgTopShow == null)
            dlgTopShow = new UniMagTopDialog(this);
        try {
            Window win = dlgTopShow.getWindow();
            win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dlgTopShow.setTitle(strTitle);
            dlgTopShow.setContentView(R.layout.dlgtopview);
            TextView myTV = (TextView) dlgTopShow.findViewById(R.id.TView_Info);

            myTV.setText(popupDialogMsg);
            dlgTopShow.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        return false;
                    }
                    return true;
                }
            });
            dlgTopShow.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            dlgTopShow = null;
        }
    }

    private void hideTopDialog() {
        if (dlgTopShow != null) {
            try {
                dlgTopShow.hide();
                dlgTopShow.dismiss();
            } catch (Exception ex) {

                ex.printStackTrace();
            }
            dlgTopShow = null;
        }
    }

    private void hideSwipeTopDialog() {
        try {
            if (dlgSwipeTopShow != null) {
                dlgSwipeTopShow.hide();
                dlgSwipeTopShow.dismiss();
                dlgSwipeTopShow = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void UpdateCardDataFields() {
        _ccnumber.setText(_cardData.getCreditCardNumber());
        _ccexp.setText(_cardData.getExpirationMonth()
                + _cardData.getExpirationYear());
        _ownerName.setText(_cardData.get_CardHolderName());
//        new AlertDialog.Builder(SaleActivity.this).setTitle("Card Data").setMessage(_cardData.getStringData()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        }).create().show();
    }

    private Runnable doHideSwipeTopDlg = new Runnable() {
        public void run() {
            hideSwipeTopDialog();
        }
    };

    private Runnable doUpdateCardDataFields = new Runnable() {
        public void run() {
            UpdateCardDataFields();
        }
    };

    private Runnable doErrorToast = new Runnable() {
        public void run() {

            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, errorMsg, duration).show();

        }
    };

    private Runnable doShowErrorTopDialog = new Runnable() {
        public void run() {

            showErrorTopDialog("Card Read Error",
                    "Card read error or invalid card type");

        }
    };

    // displays result of commands, autoconfig, timeouts, firmware update
    // progress and results.
    private Runnable doUpdateStatus = new Runnable() {
        public void run() {

            if (isReaderConnected) {
                _btnSwipeCardAJR.setEnabled(true);
                _ccnumber.setEnabled(false);
                _ccexp.setEnabled(false);
                _ownerName.setEnabled(false);
                _ccnumber.setText(null);
                _ccexp.setText(null);
                _ownerName.setText(null);
                _myUniMagReader.sendCommandGetNextKSN();

            } else {
                _btnSwipeCardAJR.setEnabled(false);
                _ccnumber.setEnabled(true);
                _ccexp.setEnabled(true);
                _ownerName.setEnabled(true);
                _ccnumber.setText(null);
                _ccexp.setText(null);
                _ownerName.setText(null);
            }
        }
    };

    private void showSwipeTopDialog(String strTitle) {
        hideSwipeTopDialog();
        try {
            if (dlgSwipeTopShow == null) {
                dlgSwipeTopShow = new UniMagTopDialog(this);
            }

            dlgSwipeTopShow.setCancelable(false);

            Window win = dlgSwipeTopShow.getWindow();
            win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dlgSwipeTopShow.setTitle(strTitle);
            dlgSwipeTopShow.setContentView(R.layout.dlgswipetopview);
            TextView myTV = (TextView) dlgSwipeTopShow
                    .findViewById(R.id.TView_Info);
            Button myBtn = (Button) dlgSwipeTopShow
                    .findViewById(R.id.btnCancel);

            myTV.setText(popupDialogMsg);
            myBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    // stop swipe
                    _myUniMagReader.stopSwipeCard();
                    if (dlgSwipeTopShow != null) {
                        Toast.makeText(SaleActivity.this, "Swipe card cancelled.", Toast.LENGTH_LONG).show();
                        msrData = null;
                        handler.post(doUpdateStatus);
                        dlgSwipeTopShow.dismiss();
                    }
                }
            });

            dlgSwipeTopShow.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        return false;
                    }
                    return true;
                }
            });
            dlgSwipeTopShow.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void showErrorTopDialog(String strTitle, String errorMessage) {
        hideSwipeTopDialog();
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
    }


    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.MY_SCAN_REQUEST_CODE:
                if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                    _ccnumber.setText(scanResult.cardNumber);

                    if (scanResult.isExpiryValid()) {
                        _ccexp.setText("0" + scanResult.expiryMonth + String.valueOf(scanResult.expiryYear).substring(2, 4));
                    }

                } else {
                }
                break;
            case Constants.SCAN_CHEQUE:
                if (data != null && resultCode == RESULT_OK) {

                    _aba.setText(data.getStringExtra(Constants.ROUTING_NUMBER));
                    _dda.setText(data.getStringExtra(Constants.ACCOUNT_NUMBER));
                    _chequeNumber.setText(data.getStringExtra(Constants.CHEQUE_NUMBER));

                    if (Boolean.parseBoolean(data.getStringExtra(Constants.OCR_INFO))) {
                        _ownerName.setText(data.getStringExtra(Constants.OWNER_NAME));
//                        _street1.setText(data.getStringExtra(Constants.OWNER_STREET));
//                        _street2.setText(data.getStringExtra(Constants.OWNER_STREET1));
//                        _city.setText(data.getStringExtra(Constants.OWNER_CITY));
                    } else {
                        _ownerName.setText(null);
//                        _street1.setText(null);
//                        _street2.setText(null);
//                        _city.setText(null);
                    }

                } else {
                }
                break;

        }
    }

    @Override
    public void onReceiveMsgToCalibrateReader() {
        // TODO Auto-generated method stub

    }


    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private Runnable doConnectUsingProfile = new Runnable() {
        public void run() {
            if (_myUniMagReader != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _myUniMagReader.connectWithProfile(profile);
            }
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
        switch (checkedId) {
            case R.id.btnSale:
                Toast.makeText(SaleActivity.this, "Sale", Toast.LENGTH_SHORT).show();

                _referenceNumberLbl.setVisibility(View.GONE);
                _referenceNumber.setVisibility(View.GONE);

                _saleAmountLbl.setVisibility(View.VISIBLE);
                _orderIdLbl.setVisibility(View.VISIBLE);
                _saleAmount.setVisibility(View.VISIBLE);
                _orderId.setVisibility(View.VISIBLE);

                if (_saleActivityType.equalsIgnoreCase("credit")) {

                    _ccnumber.setVisibility(View.VISIBLE);
                    _ccexp.setVisibility(View.VISIBLE);
                    _cvv.setVisibility(View.VISIBLE);
                    _ownerName.setVisibility(View.VISIBLE);

                    _ccnumberLbl.setVisibility(View.VISIBLE);
                    _ccexpLbl.setVisibility(View.VISIBLE);
                    _cvvLbl.setVisibility(View.VISIBLE);
                    _ownerNameLbl.setVisibility(View.VISIBLE);

//    			 _btnSwipeCardMSR.setVisibility(View.VISIBLE);
                    _btnScanCard.setVisibility(View.VISIBLE);
                    _btnSwipeCardAJR.setVisibility(View.VISIBLE);

                } else {
                    _aba.setVisibility(View.VISIBLE);
                    _dda.setVisibility(View.VISIBLE);
                    _chequeNumber.setVisibility(View.VISIBLE);
                    _accountType.setVisibility(View.VISIBLE);
/*                    _closeDate.setVisibility(View.VISIBLE);
                    _achName.setVisibility(View.VISIBLE);*/
                    _ownerName.setVisibility(View.VISIBLE);
//                    _street1.setVisibility(View.VISIBLE);
//                    _street2.setVisibility(View.VISIBLE);
/*                    _city.setVisibility(View.VISIBLE);
                    _state.setVisibility(View.VISIBLE);
                    _zip.setVisibility(View.VISIBLE);*/
/*                    _email.setVisibility(View.VISIBLE);
                    _phone.setVisibility(View.VISIBLE);*/

                    _abaLbl.setVisibility(View.VISIBLE);
                    _ddaLbl.setVisibility(View.VISIBLE);
                    _chequeNumberLbl.setVisibility(View.VISIBLE);
                    _accountTypeLbl.setVisibility(View.VISIBLE);
/*                    _closeDateLbl.setVisibility(View.VISIBLE);
                    _achNameLbl.setVisibility(View.VISIBLE);*/
                    _ownerNameLbl.setVisibility(View.VISIBLE);
//                    _street1Lbl.setVisibility(View.VISIBLE);
//                    _street2Lbl.setVisibility(View.VISIBLE);
/*                    _cityLbl.setVisibility(View.VISIBLE);
                    _stateLbl.setVisibility(View.VISIBLE);
                    _zipLbl.setVisibility(View.VISIBLE);*/
/*                    _emailLbl.setVisibility(View.VISIBLE);
                    _phoneLbl.setVisibility(View.VISIBLE);*/
                }

                _recurring.setVisibility(View.GONE);
                _recurringStartDate.setVisibility(View.GONE);
                _recurringType.setVisibility(View.GONE);
                _recurringEndDate.setVisibility(View.GONE);

                _recurring.setText(null);
                _recurringStartDate.setText(null);
//			 _recurringType.setText(null);
                _recurringEndDate.setText(null);

                _recurringLbl.setVisibility(View.GONE);
                _recurringStartDateLbl.setVisibility(View.GONE);
                _recurringTypeLbl.setVisibility(View.GONE);
                _recurringEndDateLbl.setVisibility(View.GONE);

                return;
            case R.id.btnVoid:
                Toast.makeText(SaleActivity.this, "Void", Toast.LENGTH_SHORT).show();

                _referenceNumberLbl.setVisibility(View.VISIBLE);
                _referenceNumber.setVisibility(View.VISIBLE);

                _saleAmountLbl.setVisibility(View.GONE);
                _orderIdLbl.setVisibility(View.GONE);
                _saleAmount.setVisibility(View.GONE);
                _orderId.setVisibility(View.GONE);

                if (_saleActivityType.equalsIgnoreCase("credit")) {

                    _ccnumber.setVisibility(View.GONE);
                    _ccexp.setVisibility(View.GONE);
                    _cvv.setVisibility(View.GONE);

                    _ccnumberLbl.setVisibility(View.GONE);
                    _ccexpLbl.setVisibility(View.GONE);
                    _cvvLbl.setVisibility(View.GONE);

//                    _btnSwipeCardMSR.setVisibility(View.GONE);
                    _btnScanCard.setVisibility(View.GONE);
                    _btnSwipeCardAJR.setVisibility(View.GONE);

                } else {
                    _aba.setVisibility(View.GONE);
                    _dda.setVisibility(View.GONE);
                    _accountType.setVisibility(View.GONE);
/*                    _closeDate.setVisibility(View.GONE);
                    _achName.setVisibility(View.GONE);*/
                    _ownerName.setVisibility(View.GONE);
//                    _street1.setVisibility(View.GONE);
//                    _street2.setVisibility(View.GONE);
/*                    _city.setVisibility(View.GONE);
                    _state.setVisibility(View.GONE);
                    _zip.setVisibility(View.GONE);*/
/*                    _email.setVisibility(View.GONE);
                    _phone.setVisibility(View.GONE);*/

                    _abaLbl.setVisibility(View.GONE);
                    _ddaLbl.setVisibility(View.GONE);
                    _accountTypeLbl.setVisibility(View.GONE);
/*                    _closeDateLbl.setVisibility(View.GONE);
                    _achNameLbl.setVisibility(View.GONE);*/
                    _ownerNameLbl.setVisibility(View.GONE);
//                    _street1Lbl.setVisibility(View.GONE);
//                    _street2Lbl.setVisibility(View.GONE);
/*                    _cityLbl.setVisibility(View.GONE);
                    _stateLbl.setVisibility(View.GONE);
                    _zipLbl.setVisibility(View.GONE);*/
/*                    _emailLbl.setVisibility(View.GONE);
                    _phoneLbl.setVisibility(View.GONE);*/
                }

                _recurring.setVisibility(View.GONE);
                _recurringStartDate.setVisibility(View.GONE);
                _recurringType.setVisibility(View.GONE);
                _recurringEndDate.setVisibility(View.GONE);

                _recurring.setText(null);
                _recurringStartDate.setText(null);
//			 _recurringType.setText(null);
                _recurringEndDate.setText(null);

                _recurringLbl.setVisibility(View.GONE);
                _recurringStartDateLbl.setVisibility(View.GONE);
                _recurringTypeLbl.setVisibility(View.GONE);
                _recurringEndDateLbl.setVisibility(View.GONE);

                return;
            case R.id.btnRecurring:
                Toast.makeText(SaleActivity.this, "Recurring", Toast.LENGTH_SHORT).show();

                _referenceNumberLbl.setVisibility(View.GONE);
                _referenceNumber.setVisibility(View.GONE);

                _saleAmountLbl.setVisibility(View.VISIBLE);
                _orderIdLbl.setVisibility(View.VISIBLE);
                _saleAmount.setVisibility(View.VISIBLE);
                _orderId.setVisibility(View.VISIBLE);

                if (_saleActivityType.equalsIgnoreCase("credit")) {

                    _ccnumber.setVisibility(View.VISIBLE);
                    _ccexp.setVisibility(View.VISIBLE);
                    _cvv.setVisibility(View.VISIBLE);

                    _ccnumberLbl.setVisibility(View.VISIBLE);
                    _ccexpLbl.setVisibility(View.VISIBLE);
                    _cvvLbl.setVisibility(View.VISIBLE);

//    			 _btnSwipeCardMSR.setVisibility(View.VISIBLE);
                    _btnScanCard.setVisibility(View.VISIBLE);
                    _btnSwipeCardAJR.setVisibility(View.VISIBLE);

                } else {
                    _aba.setVisibility(View.VISIBLE);
                    _dda.setVisibility(View.VISIBLE);
                    _accountType.setVisibility(View.VISIBLE);
/*                    _closeDate.setVisibility(View.VISIBLE);
                    _achName.setVisibility(View.VISIBLE);*/
                    _ownerName.setVisibility(View.VISIBLE);
//                    _street1.setVisibility(View.VISIBLE);
//                    _street2.setVisibility(View.VISIBLE);
/*                    _city.setVisibility(View.VISIBLE);
                    _state.setVisibility(View.VISIBLE);
                    _zip.setVisibility(View.VISIBLE);*/
//                    _email.setVisibility(View.VISIBLE);
//                    _phone.setVisibility(View.VISIBLE);

                    _abaLbl.setVisibility(View.VISIBLE);
                    _ddaLbl.setVisibility(View.VISIBLE);
                    _accountTypeLbl.setVisibility(View.VISIBLE);
//                    _closeDateLbl.setVisibility(View.VISIBLE);
//                    _achNameLbl.setVisibility(View.VISIBLE);
                    _ownerNameLbl.setVisibility(View.VISIBLE);
//                    _street1Lbl.setVisibility(View.VISIBLE);
//                    _street2Lbl.setVisibility(View.VISIBLE);
/*                    _cityLbl.setVisibility(View.VISIBLE);
                    _stateLbl.setVisibility(View.VISIBLE);
                    _zipLbl.setVisibility(View.VISIBLE);*/
/*                    _emailLbl.setVisibility(View.VISIBLE);
                    _phoneLbl.setVisibility(View.VISIBLE);*/
                }

                _recurring.setVisibility(View.VISIBLE);
                _recurringStartDate.setVisibility(View.VISIBLE);
                _recurringType.setVisibility(View.VISIBLE);
                _recurringEndDate.setVisibility(View.VISIBLE);

                _recurringLbl.setVisibility(View.VISIBLE);
                _recurringStartDateLbl.setVisibility(View.VISIBLE);
                _recurringTypeLbl.setVisibility(View.VISIBLE);
                _recurringEndDateLbl.setVisibility(View.VISIBLE);

                return;
            case R.id.radio_button_current:
                RadioButton checkedRadioButton = (RadioButton) findViewById(checkedId);
                if (checkedRadioButton != null)
                    accType = checkedRadioButton.getText().toString().substring(0, 1);
                return;

            case R.id.radio_button_saving:
                RadioButton checkedRadioButton1 = (RadioButton) findViewById(checkedId);
                if (checkedRadioButton1 != null)
                    accType = checkedRadioButton1.getText().toString().substring(0, 1);
                return;
        }

    }


    /**
     * callback function for requests
     *
     * @param requestCode  the request code you have sent for requestingpermission
     * @param permissions  which permissions were asked.
     * @param grantResults permissions granted or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case Constants.PermissionRequestCodes.SYSTEM_MICROPHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(SaleActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Granted","DISMISS",Snackbar.LENGTH_LONG);
                } else {
                    Toast.makeText(SaleActivity.this, "Permission Microphone Denied", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Read Phone/Calls Denied","DISMISS",Snackbar.LENGTH_INDEFINITE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            case Constants.PermissionRequestCodes.WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(SaleActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Granted","DISMISS",Snackbar.LENGTH_LONG);
                } else {
                    Toast.makeText(SaleActivity.this, "Permission Write External Storage Denied", Toast.LENGTH_LONG).show();
//               StaticFunctions.createSnackBar(mCoordinatorLayout,"Permission Read Phone/Calls Denied","DISMISS",Snackbar.LENGTH_INDEFINITE);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
