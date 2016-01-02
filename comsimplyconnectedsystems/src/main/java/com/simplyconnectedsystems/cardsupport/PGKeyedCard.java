package com.simplyconnectedsystems.cardsupport;

public class PGKeyedCard extends PGCard {

	private String cardNumber;
	private String expirationDate;
	private String cardIssueNumber;

	public PGKeyedCard(String ccnumber, String exp, String cvv) {
		this.cardNumber = ccnumber;
		this.expirationDate = exp;
		this.setCVV(cvv);
		this.cardIssueNumber = null;
	}

	public PGKeyedCard(String ccnumber, String exp, String cvv, String issueNum) {
		this.cardNumber = ccnumber;
		this.expirationDate = exp;
		this.setCVV(cvv);
		this.cardIssueNumber = issueNum;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String value) {
		this.cardNumber = value;
	}

	public String getExpirationDate() {
		return this.expirationDate;
	}

	public void setExpirationDate(String value) {
		this.expirationDate = value;
	}

	public String getCardIssueNumber() {
		return this.cardIssueNumber;
	}

	public void setCardIssueNumber(String value) {
		this.cardIssueNumber = value;
	}
	
	public String getDirectPostString(boolean includeCVV) {

		String result = "<FIELD KEY=\"card_number\">" + this.cardNumber;
		result += "</FIELD><FIELD KEY=\"card_name\">" + CreditCardType.determineCreditCardType(this.cardNumber).getFullName();
		result += "</FIELD><FIELD KEY=\"card_exp\">" + this.expirationDate;
		result += "</FIELD><FIELD KEY=\"owner_name\">" + this.cardIssueNumber + "</FIELD>";
		if (this.getCVV() != null && includeCVV) {
			result += "<FIELD KEY=\"cvv2\">" + this.getCVV() + "</FIELD>";
		}

		return result;
	}
}
