package com.simplyconnectedsystems.cardsupport;

public class PGCard {

	private String cvv;

	public void setCVV(String value) {
		this.cvv = value;
	}

	public String getCVV() {
		return this.cvv;
	}

	/**
	 * Gets the card information as it would be passed using the direct post
	 * api.
	 * 
	 * @param includeCVV
	 *            Whether or not to include the CVV.
	 * @return
	 */
	public String getDirectPostString(boolean includeCVV) {

		if (this.cvv != null && includeCVV) {
			return "cvv=" + this.cvv;
		}
		return null;
	}
}
