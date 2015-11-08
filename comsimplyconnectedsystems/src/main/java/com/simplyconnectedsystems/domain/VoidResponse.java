package com.simplyconnectedsystems.domain;

import java.io.Serializable;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class VoidResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _status;
	private String _responsetext;
	private String _transactionid;

	private String textBtwTags;

	private boolean status = false;
	private boolean reference_number = false;
	private boolean response_text = false;
	private boolean error;

	public VoidResponse(String response) throws Exception{
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
						if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("status1"))
				        	 status = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("response1"))
					    	  response_text = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("reference_number1"))
					    	  reference_number = true;
					      else if(myParser.getAttributeValue(null,"KEY").equalsIgnoreCase("response1"))
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
		    	  }else if(response_text){
		    		  setResponsetext(textBtwTags);
		    		  response_text = false;
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

	public String getTransactionid() {
		return _transactionid;
	}

	public void setTransactionid(String _transactionid) {
		this._transactionid = _transactionid;
	}


}
