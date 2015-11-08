package com.simplyconnectedsystems.cardsupport;

public class Void {

	private String referenceNumber;

	public Void(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getVoidDirectPostString() {
		return "<FIELD KEY=\"total_number_transactions\">1</FIELD><FIELD KEY=\"reference_number1\">" + this.referenceNumber + "</FIELD>";
	}
	
}
