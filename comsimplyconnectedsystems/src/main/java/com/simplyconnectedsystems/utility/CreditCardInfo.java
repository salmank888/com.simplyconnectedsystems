package com.simplyconnectedsystems.utility;

/**
 * 
 * Validate credit card checksum and determine type of credit card. <br />
 * The following credit card numbers can be used for testing:
 * 
 * <ul type="circle">
 * <li>Test Visa Number = 4007000000027</li>
 * <li>Test MasterCard Number = 5424 0000 0000 0015</li>
 * <li>Test American Express Number = 370000000000002</li>
 * <li>Test Discover/Novus Number = 6011000000000012</li>
 * <li>Test JCB Number = 3088000000000017</li>
 * <li>Test Diners/Carte Blanche Number = 38000000000006</li>
 * 
 * </ul>
 * 
 * @author <a href="http://www.acmetech.com/">Wayne K. Walrath, Acme
 *         Technologies</a>
 * 
 * <br />
 * <br />
 * 
 *         <font size="-1"> <u>Version History</u> <br />
 *         <font size="-1"><b>0.1 - 2005-03-23</b></font> - First release
 *         </font>
 * 
 * <br />
 * <br />
 *         <font size="-1">Copyright 2005 &copy; Acme Technologies. All rights
 *         reserved. Permission granted to incorporate these classes into
 *         non-commercial (shareware applications ARE allowed) derivative works.
 *         You may not post these classes on the internet or otherwise
 *         distribute them EXCEPT as part of a derivative work which includes
 *         the Acme Technologies copyright and the address of our website
 *         (www.acmetech.com). <br />
 * <br />
 *         For an easy flat-fee redistribution license for commercial use,
 *         please contact Acme Technologies through one of the means listed at
 *         <a href="http://www.acmetech.com/">www.acmetech.com</a>. </font>
 */

public final class CreditCardInfo {

	/**
	 * cannot be instantiated. All member methods are static.
	 */
	private CreditCardInfo() {
	}

	/**
	 * Determine if card account number passes checksum verification. (NOTE:
	 * this does not mean it is a valid account number, only that the checksum
	 * matches the check digit.) <br />
	 * <br />
	 * <font size="-1"><i>Calculated using the Luhn formula for computing
	 * modulus 10 "double-add-double" check digit: Double the value of alternate
	 * (odd) digits starting by the least significant (first right hand) digit.
	 * Then add the individual digits of doubled (odd) numbers and even digits
	 * of the original number. If the value ends in 0 then the check digit is 0.
	 * Otherwise subtract the value from the next higher number ending in 0
	 * (tens complement of the unit digit). The result is the check digit.
	 * Example: If the account number without check digit is 1234 5678 9012 344
	 * then (8) + 4 + (6) + 2 + (2) + 0 + (1 + 8) + 8 + (1 + 4) + 6 + (1 + 0) +
	 * 4 + (6) + 2 + (2) = 65, therefore the check digit is 70 - 65 = 5 and so
	 * the complete account number is 1234 5678 9012 3445. </i></font>
	 * 
	 * @param cardAccountNumber
	 *            the credit card account number (non-digit characters are
	 *            ignored)
	 * @return true if the checksum matches the check digit.
	 * @throws java.lang.NullPointerException
	 * 
	 */
	public static boolean validateChecksum(String cardAccountNumber)
			throws NullPointerException {
		cardAccountNumber = stripNonDigits(cardAccountNumber);
		if (cardAccountNumber == null)
			throw new NullPointerException("Card number is null");

		char[] inNumber = cardAccountNumber.toCharArray();
		int len = inNumber.length;
		int checksum, digit, temp, i;
		char lastChar = inNumber[len - 1];
		checksum = 0;
		for (i = 1; i < len; i++) {
			// don't include last digit
			digit = inNumber[len - i - 1] - '0';

			temp = digit * (1 + (i % 2));
			if (temp < 10)
				checksum += temp;
			else
				checksum += temp - 9;
		}
		checksum = (10 - (checksum % 10)) % 10;

		if ((lastChar - '0') == checksum)
			return true;
		else
			return false;
	}

	/**
	 * Get the card type for a given credit card account number.
	 * 
	 * @param cardAccountNumber
	 *            credit card number
	 * @return type of credit card or &quot;Unknown&qout; if type can't be
	 *         determined.
	 * @throws java.lang.NumberFormatException
	 */
	public static String cardType(String cardAccountNumber)
			throws java.lang.NumberFormatException {
		String c = stripNonDigits(cardAccountNumber);
		if (c == null)
			throw new java.lang.NumberFormatException("Null credit card number");

		if (c.length() < 13)
			throw new java.lang.NumberFormatException(
					"Invalid credit card account number length (<13): " + c);

		char firstdigit = c.charAt(0);
		if (firstdigit == '3' && c.charAt(1) == '7')
			return "American Express";
		else if (firstdigit == '4')
			return "Visa";
		else if (firstdigit == '5' && c.charAt(1) == '6')
			return "BankCard";
		else if (firstdigit == '5')
			return "MasterCard";
		else if (firstdigit == '3')
			return "Diner's Club";
		else if (firstdigit == '6')
			return "Discover";
		else
			return "Unknown";

	}

	/**
	 * Strip out non-digit characters returning the raw credit card account
	 * number.
	 * 
	 * @param s
	 *            credit card account number
	 * @return cleaned account number (with nothing but digits)
	 */
	public static String stripNonDigits(String s) {
		if (s == null)
			return s;
		int len = s.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			if (Character.isDigit(s.charAt(i)))
				sb.append(s.charAt(i));
		}
		return sb.toString();
	}

}
