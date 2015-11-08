package com.simplyconnectedsystems.cardsupport;

public class PGSwipedCard extends PGCard {
    
    private String track1;
    private String track2;
    private String track3;
    
    //The following fields are not passed to the gateway.
    private String maskedCardNumber;
    private String cardholderName;
    private String expirationDate;
    private String ksnData;


    public PGSwipedCard(String track1, String track2, String track3, String cvv, String ksn) {
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        this.ksnData = ksn;
        this.setCVV(cvv);
    }
    public PGSwipedCard() {
        this.track1 = null;
        this.track2 = null;
        this.track3 = null;
        this.setCVV(null);
    }

    public void setTrack1(String value) {
        this.track1 = value;
    }
    public void setTrack2(String value) {
        this.track2 = value;
    }
    public void setTrack3(String value) {
        this.track3 = value;
    }
    public void setMaskedCardNumber(String value) {
        this.maskedCardNumber = value;
    }
    public void setCardholderName(String value) {
        this.cardholderName = value;
    }
    public void setExpirationDate(String value) {
        this.expirationDate = value;
    }
    public String getTrack1() {
        return this.track1;
    }
    public String getTrack2() {
        return this.track2;
    }
    public String getTrack3() {
        return this.track3;
    }
    public String getMaskedCardNumber() {
        return this.maskedCardNumber;
    }
    public String getCardholderName() {
        return this.cardholderName;
    }
    public String getExpirationDate() {
        return this.expirationDate;
    }

    public String getKsnData() {
        return ksnData;
    }

    public void setKsnData(String ksnData) {
        this.ksnData = ksnData;
    }

    /*
         * Returns card data formated properly to be passed to gateway or encrypt(non-Javadoc)
         * @see com.SafeWebServices.PaymentGateway.PGCard#getDirectPostString(boolean)
         */
    public String getDirectPostString(boolean includeCVV) {

    	return "<FIELD KEY=\"mag_data\">" + getTrack1() + getTrack2() + "</FIELD>";

    }

    public String getEncryptedDirectPostString() {

        return "<FIELD KEY=\"enctrack1\">" + getTrack1() + "</FIELD><FIELD KEY=\"ksn\">" + getKsnData() +"</FIELD><FIELD KEY=\"swiper\">simplyconnectedsystems</FIELD>";

    }
}
