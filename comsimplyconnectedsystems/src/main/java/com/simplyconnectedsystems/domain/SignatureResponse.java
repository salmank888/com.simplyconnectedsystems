package com.simplyconnectedsystems.domain;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class SignatureResponse {

	private String _status;
	private String _responsetext;


	private String textBtwTags;

	private boolean status = false;

	private boolean error = false;

	public SignatureResponse(String response) throws Exception{
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
	
}
