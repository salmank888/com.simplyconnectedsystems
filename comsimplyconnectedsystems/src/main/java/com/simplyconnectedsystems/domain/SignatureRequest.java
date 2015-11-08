package com.simplyconnectedsystems.domain;

import com.simplyconnectedsystems.utility.Constants;

public class SignatureRequest {

    private String _merchant, _gateway_id, _operation_type, _reference_number, _processor, _signature;

    
	public SignatureRequest() {

	}

	public String get_merchant() {
		return _merchant;
	}

	public void set_merchant(String _merchant) {
		this._merchant = _merchant;
	}

	public String get_gateway_id() {
		return _gateway_id;
	}

	public void set_gateway_id(String _gateway_id) {
		this._gateway_id = _gateway_id;
	}

	public String get_operation_type() {
		return _operation_type;
	}

	public void set_operation_type(String _operation_type) {
		this._operation_type = _operation_type;
	}

	public String get_reference_number() {
		return _reference_number;
	}

	public void set_reference_number(String _reference_number) {
		this._reference_number = _reference_number;
	}

	public String get_processor() {
		return _processor;
	}

	public void set_processor(String _processor) {
		this._processor = _processor;
	}

	public String get_signature() {
		return _signature;
	}

	public void set_signature(String _signature) {
		this._signature = _signature;
	}
    
	public String getAddSignatureUrl(){
		if (Constants._1stPAY_URL != null && get_operation_type() != null) {
			StringBuilder transUrl = new StringBuilder();
			transUrl.append("<TRANSACTION><FIELDS><FIELD KEY=\"merchant\">");
			transUrl.append(get_merchant());
			transUrl.append("</FIELD><FIELD KEY=\"gateway_id\">");
			transUrl.append(get_gateway_id());
			transUrl.append("</FIELD><FIELD KEY=\"operation_type\">");
			transUrl.append(get_operation_type());
			transUrl.append("</FIELD><FIELD KEY=\"reference_number\">");
			transUrl.append(get_reference_number());
			transUrl.append("</FIELD><FIELD KEY=\"processor\">");
			transUrl.append(get_processor());
			transUrl.append("</FIELD><FIELD KEY=\"signature\">");
			transUrl.append(get_signature());
			transUrl.append("</FIELD>");
			transUrl.append("</FIELDS></TRANSACTION>");


			return transUrl.toString();
		} else {
			return null;
		}	
	}
    
}
