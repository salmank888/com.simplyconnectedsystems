package com.simplyconnectedsystems.cardsupport;

public class PGKeyedCard extends PGCard {

	private String cardNumber;
	private String expirationDate;

	// maestro variables
	private String cardStartDate;
	private String cardIssueNumber;

	public PGKeyedCard(String ccnumber, String exp, String cvv) {
		this.cardNumber = ccnumber;
		this.expirationDate = exp;
		this.setCVV(cvv);
		this.cardStartDate = null;
		this.cardIssueNumber = null;
	}

	public PGKeyedCard(String ccnumber, String exp, String cvv,
			String startDate, String issueNum) {
		this.cardNumber = ccnumber;
		this.expirationDate = exp;
		this.setCVV(cvv);
		this.cardStartDate = startDate;
		this.cardIssueNumber = issueNum;
	}

	public void setCardNumber(String value) {
		this.cardNumber = value;
	}

	public void setExpirationDate(String value) {
		this.expirationDate = value;
	}

	public void setCardStartDate(String value) {
		this.cardStartDate = value;
	}

	public void setCardIssueNumber(String value) {
		this.cardIssueNumber = value;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public String getExpirationDate() {
		return this.expirationDate;
	}

	public String getCardStartDate() {
		return this.cardStartDate;
	}

	public String getCardIssueNumber() {
		return this.cardIssueNumber;
	}
	
	/*
	 * Returns card data formated properly to be passed to gateway or
	 * encrypt(non-Javadoc)
	 * 
	 * @see
	 * com.SafeWebServices.PaymentGateway.PGCard#getDirectPostString(boolean)
	 */
	
	public String getDirectPostString(boolean includeCVV) {

		String result = "<FIELD KEY=\"card_number\">" + this.cardNumber;
		result += "</FIELD><FIELD KEY=\"card_name\">" + CreditCardType.determineCreditCardType(this.cardNumber).getFullName();
		result += "</FIELD><FIELD KEY=\"card_exp\">" + this.expirationDate + "</FIELD>";
		if (this.getCVV() != null && includeCVV) {
			result += "<FIELD KEY=\"cvv2\">" + this.getCVV() + "</FIELD>";
		}

		return result;
	}
}
