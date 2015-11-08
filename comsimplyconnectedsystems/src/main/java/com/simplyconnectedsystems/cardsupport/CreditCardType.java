package com.simplyconnectedsystems.cardsupport;

public final class CreditCardType {

	private CreditCardType(int cardType) {
		this.cardType = cardType;
	}

	public String getShortName() {
		return cardTypeShortNames[cardType];
	}

	public String getFullName() {
		return cardTypeFullNames[cardType];
	}

	public static CreditCardType determineCreditCardType(String cardNumber) {
		if (isVisa(cardNumber))
			return VISA;
		if (isMasterCard(cardNumber))
			return MASTERCARD;
		if (isAmericanExpress(cardNumber))
			return AMERICAN_EXPRESS;
		if (isDiscover(cardNumber))
			return DISCOVER;
		if (isDinersClub(cardNumber))
			return DINERS_CLUB;
		if (isJCB(cardNumber))
			return JCB;
		if (isMJM(cardNumber))
			return MJM;
		if (isPathFinder(cardNumber))
			return PATHFINDER;
		else
			return UNKWN;
	}

	private static boolean isVisa(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return (cardNumber.length() == 13 || cardNumber.length() == 16) && temp.compareTo("4000") >= 0 && temp.compareTo("4999") <= 0;
	}

	private static boolean isMasterCard(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return cardNumber.length() == 16 && temp.compareTo("5100") >= 0 && temp.compareTo("5599") <= 0 || cardNumber.length() == 14 && temp.compareTo("3600") >= 0 && temp.compareTo("3699") <= 0;
	}

	private static boolean isAmericanExpress(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return cardNumber.length() == 15 && temp.compareTo("3400") >= 0 && temp.compareTo("3499") <= 0 || temp.compareTo("3700") >= 0 && temp.compareTo("3799") <= 0;
	}

	private static boolean isDiscover(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return cardNumber.length() == 16 && temp.compareTo("6011") >= 0 && temp.compareTo("6011") <= 0 || cardNumber.length() == 16 && temp.compareTo("6500") >= 0 && temp.compareTo("6599") <= 0;
	}

	private static boolean isDinersClub(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return cardNumber.length() == 14 && temp.compareTo("3000") >= 0 && temp.compareTo("3099") <= 0 || temp.compareTo("3800") >= 0 && temp.compareTo("3899") <= 0;
	}

	private static boolean isJCB(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return (cardNumber.length() == 15 || cardNumber.length() == 16) && temp.compareTo("3528") >= 0 && temp.compareTo("3589") <= 0;
	}

	private static boolean isMJM(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return (cardNumber.length() == 15 || cardNumber.length() == 16) && temp.compareTo("3950") >= 0 && temp.compareTo("3950") <= 0;
	}

	private static boolean isPathFinder(String cardNumber) {
		String temp = cardNumber.substring(0, 4);
		return (cardNumber.length() == 15 || cardNumber.length() == 16) && temp.compareTo("7000") >= 0 && temp.compareTo("7000") <= 0;
	}

	private static final int _VISA = 0;
	private static final int _MASTERCARD = 1;
	private static final int _AMERICAN_EXPRESS = 2;
	private static final int _DISCOVER = 3;
	private static final int _DINERS_CLUB = 4;
	private static final int _JCB = 5;
	private static final int _MJM = 6;
	private static final int _PATHFINDER = 7;
	private static final int _UNKWN = 8;
	private static final String cardTypeShortNames[] = {"VI", "MC", "AX", "DI", "DC", "JC", "MJ", "PF", "UN"};
	private static final String cardTypeFullNames[] = {"VISA", "MasterCard", "AmericanExpress", "Discover", "DinersClub", "JCB", "MJM", "PathFinder", "Unknown"};
	public static final CreditCardType VISA = new CreditCardType(_VISA);
	public static final CreditCardType MASTERCARD = new CreditCardType(_MASTERCARD);
	public static final CreditCardType AMERICAN_EXPRESS = new CreditCardType(_AMERICAN_EXPRESS);
	public static final CreditCardType DISCOVER = new CreditCardType(_DISCOVER);
	public static final CreditCardType DINERS_CLUB = new CreditCardType(_DINERS_CLUB);
	public static final CreditCardType JCB = new CreditCardType(_JCB);
	public static final CreditCardType MJM = new CreditCardType(_MJM);
	public static final CreditCardType PATHFINDER = new CreditCardType(_PATHFINDER);
	public static final CreditCardType UNKWN = new CreditCardType(_UNKWN);
	private final int cardType;

}
