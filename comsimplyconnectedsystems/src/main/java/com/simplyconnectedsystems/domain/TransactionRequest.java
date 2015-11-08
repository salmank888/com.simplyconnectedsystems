package com.simplyconnectedsystems.domain;

import com.simplyconnectedsystems.utility.Constants;

public class TransactionRequest {
	
	private String _MID;

	private String _TID;

	private String _processor;
	
	private String _gateway_id;
	
	private String _transaction_center_id;

	private String _type;

	private String _amount;

	private String _orderId;

//	private String _encrypted_payment;
	
	private String _unencrypted_payment;

	public TransactionRequest() {
	}

	public String get_MID() {
		return _MID;
	}

	public void set_MID(String _MID) {
		this._MID = _MID;
	}

	public String get_TID() {
		return _TID;
	}

	public void set_TID(String _TID) {
		this._TID = _TID;
	}

	public String get_processor() {
		return _processor;
	}

	public void set_processor(String _processor) {
		this._processor = _processor;
	}

	public String get_gateway_id() {
		return _gateway_id;
	}

	public void set_gateway_id(String _gateway_id) {
		this._gateway_id = _gateway_id;
	}

	public String get_transaction_center_id() {
		return _transaction_center_id;
	}

	public void set_transaction_center_id(String _transaction_center_id) {
		this._transaction_center_id = _transaction_center_id;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public String get_amount() {
		return _amount;
	}

	public void set_amount(String _amount) {
		this._amount = _amount;
	}

	public String get_orderId() {
		return _orderId;
	}

	public void set_orderId(String _orderId) {
		this._orderId = _orderId;
	}

	public String getTransactionUrl(boolean _isTIDpresent, boolean _isVoidRequest) {
		if ((Constants._1stPAY_URL != null && get_type() != null && get_amount() != null) || (Constants._1stPAY_URL != null && _isVoidRequest)) {
			StringBuilder transUrl = new StringBuilder();
			transUrl.append("<TRANSACTION><FIELDS><FIELD KEY=\"transaction_center_id\">");
			transUrl.append(get_transaction_center_id());
			transUrl.append("</FIELD><FIELD KEY=\"Processor\">");
			transUrl.append(get_processor());
			transUrl.append("</FIELD><FIELD KEY=\"MID\">");
			transUrl.append(get_MID());
			if(_isTIDpresent){
			transUrl.append("</FIELD><FIELD KEY=\"TID\">");
			transUrl.append(get_TID());
			}
			transUrl.append("</FIELD><FIELD KEY=\"gateway_id\">");
			transUrl.append(get_gateway_id());
			transUrl.append("</FIELD><FIELD KEY=\"operation_type\">");
			transUrl.append(get_type());
			if(!_isVoidRequest){
			transUrl.append("</FIELD><FIELD KEY=\"order_id\">");
			transUrl.append(get_orderId());
			transUrl.append("</FIELD><FIELD KEY=\"total\">");
			transUrl.append(get_amount());
			}
			transUrl.append("</FIELD>");
			transUrl.append(getUnencrypted_payment());
			transUrl.append("</FIELDS></TRANSACTION>");

	
//			if (getUnencrypted_payment() != null)
//			{
//				transUrl.append("&");
//				transUrl.append(getUnencrypted_payment());
//			}
//			else
//			{
//				transUrl.append("&");
//				transUrl.append("encrypted_payment=");
//				transUrl.append(getEncrypted_payment());
//			}

			return transUrl.toString();
		} else {
			return null;
		}

	}

	public String getUnencrypted_payment() {
		return _unencrypted_payment;
	}

	public void setUnencrypted_payment(String _unencrypted_payment) {
		this._unencrypted_payment = _unencrypted_payment;
	}

}
