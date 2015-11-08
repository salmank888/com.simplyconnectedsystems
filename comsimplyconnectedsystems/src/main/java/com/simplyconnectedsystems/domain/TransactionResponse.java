package com.simplyconnectedsystems.domain;

import java.io.Serializable;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class TransactionResponse implements Serializable {

	private static final long serialVersionUID = 5797952708003951103L;

	private String _status;
	private String _responsetext;
	private String _authcode;
	private String _transactionid;
	private String _avsresponse;
	private String _cvvresponse;
	private String _orderid;
	private String _type;
	private String _response_code;

	private String textBtwTags;

	private boolean status = false;
	private boolean auth_code = false;
	private boolean order_id = false;
	private boolean reference_number = false;

	private boolean error;

	public TransactionResponse(String response) throws Exception{
		XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
		XmlPullParser myParser = xmlFactoryObject.newPullParser();
		
		myParser.setInput(new StringReader(response));
		
		int event = myParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) 
		{
		   String name=myParser.getName();
		   switch (event){
		      case XmlPullParser.START_TAG:
			      if(name.equals("FIELD")){
						if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("status"))
				        	 status = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("auth_code"))
					    	 auth_code = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("order_id"))
					    	  order_id = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("reference_number"))
					    	  reference_number = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("error"))
					    	  error = true;
				      }
		      break;
				case XmlPullParser.TEXT :
					textBtwTags = myParser.getText();
					break;
		      case XmlPullParser.END_TAG:
		    	  if(status){
		    		  setStatus(textBtwTags);
		    		  status = false;
		    	  }else if(auth_code){
		    		  setAuthcode(textBtwTags);
		    		  auth_code = false;
		    	  }else if(order_id){
		    		  setOrderid(textBtwTags);
		    		  order_id = false;
		    	  }else if(reference_number){
		    		  setTransactionid(textBtwTags);
		    		  reference_number = false;
		    	  }else if(error){
		    		  setResponsetext(textBtwTags);
		    		  error = false;
		    	  }
		      break;
		   }		 
		   event = myParser.next(); 					
		}

	}

	public String getStatus() {
		return _status;
	}

	public void setStatus(String _status) {
		this._status = _status;
	}

	public String getResponsetext() {
		return _responsetext;
	}

	public void setResponsetext(String _responsetext) {
		this._responsetext = _responsetext;
	}

	public String getAuthcode() {
		return _authcode;
	}

	public void setAuthcode(String _authcode) {
		this._authcode = _authcode;
	}

	public String getTransactionid() {
		return _transactionid;
	}

	public void setTransactionid(String _transactionid) {
		this._transactionid = _transactionid;
	}

	public String getAvsresponse() {
		return _avsresponse;
	}

	public void setAvsresponse(String _avsresponse) {
		this._avsresponse = _avsresponse;
	}

	public String getCvvresponse() {
		return _cvvresponse;
	}

	public void setCvvresponse(String _cvvresponse) {
		this._cvvresponse = _cvvresponse;
	}

	public String getOrderid() {
		return _orderid;
	}

	public void setOrderid(String _orderid) {
		this._orderid = _orderid;
	}

	public String getType() {
		return _type;
	}

	public void setType(String _type) {
		this._type = _type;
	}

	public String getResponse_code() {
		return _response_code;
	}

	public void setResponse_code(String _response_code) {
		this._response_code = _response_code;
	}

}
