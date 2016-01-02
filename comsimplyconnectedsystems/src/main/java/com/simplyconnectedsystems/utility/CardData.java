package com.simplyconnectedsystems.utility;

import java.math.BigInteger;

/**
 * Created by skhalid on 11/10/2015.
 */
public class CardData {
    private String stringData;
    private byte[] byteData;
    private String _Track2Data = null;
    private String _PAN = null;
    private String _ExpYearStr = null;
    private String _ExpMonthStr = null;
    private String _CardHolderName = null;



    public CardData(String data){
        if (data.length()%2 == 0) {
            stringData = data;
            byteData = new byte[stringData.length()/2];
            convertStringToByte();
        }
    }
    public CardData(byte[] data) throws MagstripeParseException{
        stringData = "";
        byteData = data;
        convertByteToString();

        parse();
    }
    public String getSTX() {
        return stringData.substring(0,2);
    }
    public int getDataLength() {
        if (!isDataEncrypted())
            return byteData.length;
        return (int)(0xFFFF&(byteData[2]<<8) + 0xFF&byteData[1]);
    }
    public String getCardEncodeType() {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(6, 8);
    }
    public String getTrackStatus() {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(8,10);
    }
    public int getTrack1Length() {
        if (!isDataEncrypted())
            return getT1Data().length()/2;
        return (int) byteData[5];
    }
    public int getTrack2Length() {
        if (!isDataEncrypted())
            return getT2Data().length()/2;
        return (int) byteData[6];
    }
    public int getTrack3Length() {
        if (!isDataEncrypted())
            return 0;
        return (int) byteData[7];
    }
    public String getFieldByte1 () {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(16, 18);
    }
    public String getFieldByte2 () {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(18, 20);
    }
    public String getT1Data() {
        if (!isDataEncrypted()) {
            int endIndex = stringData.indexOf("3f");
            return stringData.substring(0, endIndex + 2);
        }
        if (!isT1DataPresent())
            return null;
        return stringData.substring(20, (getTrack1Length() + 10) * 2);
    }
    public String getT1DataAscii() {
        StringBuffer sf = new StringBuffer();
        if (!isDataEncrypted()) {
            for (int i=0; i<getT1Data().length()/2; i++){
                sf.append((char)byteData[i]);
            }
            return sf.toString();
        }
        if (!isT1DataPresent())
            return null;

        for (int i=10; i<getTrack1Length()+10; i++){
            sf.append((char)byteData[i]);
        }
        return sf.toString();
    }
    public String getT2Data() {
        if (!isDataEncrypted()) {
            int startIndex = stringData.indexOf("3b");
            int endIndex1 = stringData.indexOf("3f");
            int endIndex2 = stringData.lastIndexOf("3f");
            if (endIndex2 > endIndex1 && startIndex > 0)
                return stringData.substring(startIndex, endIndex2+2);
            else
                return null;
        }
        if (!isT1DataPresent())
            return null;
        return stringData.substring((getTrack1Length() + 10) * 2, (getTrack1Length() + 10) * 2 + getTrack2Length() * 2);
    }
    public String getT2DataAscii() {
        StringBuffer sf = new StringBuffer();
        if (!isDataEncrypted()) {
            String strData = getT2Data();
            if(null!=strData)
            {
                if (strData.length()>0) {
                    int startIndex = stringData.indexOf("3b");
                    int endIndex = stringData.lastIndexOf("3f")+2;
                    for (int i=startIndex/2; i<endIndex/2; i++){
                        sf.append((char)byteData[i]);
                    }
                    return sf.toString();
                } else
                    return null;
            }
        }
        if (!isT1DataPresent())
            return null;
        for (int i=getTrack1Length()+10; i<getTrack1Length()+10+getTrack2Length(); i++){
            sf.append((char)byteData[i]);
        }
        return sf.toString();
    }

    public String getEncryptedTrack1() {
        if (!isDataEncrypted())
            return null;
        int startIndex = 20;
        if (isT1DataPresent())
            startIndex += getTrack1Length()*2;
        if (isT2DataPresent())
            startIndex += getTrack2Length()*2;
        return stringData.substring(startIndex, startIndex + lengthOfEncryptedTrack1() * 2);
    }

    public String getEncryptedTrack2()
    {
        if (!isDataEncrypted())
            return null;
        int startIndex = 20;
        if (isT1DataPresent())
            startIndex += getTrack1Length()*2;
        if (isT2DataPresent())
            startIndex += getTrack2Length()*2;
        startIndex += lengthOfEncryptedTrack1() *2;
        return stringData.substring(startIndex, startIndex+lengthOfEncryptedTrack2()*2);
    }

    public int lengthOfEncryptedTrack1(){
        if (!isDataEncrypted())
            return 0;

        int result = 0;

        if (isEncryptedWithTDES()) {
            if (getTrack1Length()%8==0)
                result += getTrack1Length();
            else
                result += (getTrack1Length()/8)*8+8;
        }
        else if (isEncryptedWithAES()) {
            if (getTrack1Length()%16==0)
                result += getTrack1Length();
            else
                result += (getTrack1Length()/16)*16+16;
        }
        else
            return 0;

        return result;
    }

    public int lengthOfEncryptedTrack2()
    {
        if (!isDataEncrypted())
            return 0;

        int result = 0;

        if (isEncryptedWithTDES())
        {
            if (getTrack2Length()%8==0)
                result += getTrack2Length();
            else
                result += (getTrack2Length()/8)*8+8;
        }
        else if (isEncryptedWithAES())
        {
            if (getTrack2Length()%16==0)
                result += getTrack2Length();
            else
                result += (getTrack2Length()/16)*16+16;
        }
        else
            return 0;

        return result;
    }


    public String getEncryptedSection()
    {
        if (!isDataEncrypted())
            return null;
        int startIndex = 20;
        if (isT1DataPresent())
            startIndex += getTrack1Length()*2;
        if (isT2DataPresent())
            startIndex += getTrack2Length()*2;
        return stringData.substring(startIndex, startIndex+lengthOfEncryptedSection()*2);
    }
    public int lengthOfEncryptedSection()
    {
        if (!isDataEncrypted())
            return 0;

        int result = 0;

        if (isEncryptedWithTDES())
        {
            if (getTrack1Length()%8==0)
                result += getTrack1Length();
            else
                result += (getTrack1Length()/8)*8+8;
            if (getTrack2Length()%8==0)
                result += getTrack2Length();
            else
                result += (getTrack2Length()/8)*8+8;
        }
        else if (isEncryptedWithAES())
        {
            if (getTrack1Length()%16==0)
                result += getTrack1Length();
            else
                result += (getTrack1Length()/16)*16+16;
            if (getTrack2Length()%16==0)
                result += getTrack2Length();
            else
                result += (getTrack2Length()/16)*16+16;
        }
        else
            return 0;

        return result;
    }

    public String getSerialNumber() {
        if (!isDataEncrypted())
            return null;
        if (!isSNPresent())
            return null;
        if (isKSNPresent())
            return stringData.substring(stringData.length()-46, stringData.length()-26);
        else
            return stringData.substring(stringData.length()-26, stringData.length()-6);
    }
    public String getKSN() {
        if (!isDataEncrypted())
            return null;
        if (!isKSNPresent())
            return null;
        return stringData.substring(stringData.length()-26, stringData.length()-6);
    }
    public String getLRC() {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(stringData.length()-6, stringData.length()-4);
    }
    public String getCheckSum() {
        if (!isDataEncrypted())
            return null;
        return stringData.substring(stringData.length()-4, stringData.length()-2);
    }
    public String getETX() {
        return stringData.substring(stringData.length()-2);
    }
    public byte calculateCheckSum() {
        if (!isDataEncrypted())
            return 0;
        byte result = 0;
        for (int i=3; i<byteData.length-3; i++)
            result += byteData[i];

        return result;
    }
    public byte calculateLRC() {
        if (!isDataEncrypted())
            return 0;
        byte result = 0;
        for (int i=3; i<byteData.length-3; i++)
            result = (byte) (result ^ byteData[i]);
        return result;
    }

    public boolean isStartingWithSTX() {
        if (getSTX().equals("02") || getSTX().equals("25"))
            return true;
        else
            return false;
    }
    public boolean isEndingWithETX() {
        if (getETX().equalsIgnoreCase("03") || getETX().equals("0d"))
            return true;
        else
            return false;
    }
    public boolean isLRCCorrect() {
        if (!isDataEncrypted())
            return false;
        String lrc = Integer.toHexString(0xFF&calculateLRC());
        if (lrc.length()==1)
            lrc = "0"+lrc;
        if (lrc.equalsIgnoreCase(getLRC()))
            return true;
        else
            return false;
    }
    public boolean isCheckSumCorrect() {
        if (!isDataEncrypted())
            return false;
        String checkSum = Integer.toHexString(0xFF & calculateCheckSum());
        if (checkSum.length()==1)
            checkSum = "0"+checkSum;

        if (checkSum.equalsIgnoreCase(getCheckSum()))
            return true;
        else
            return false;
    }
    public boolean isSNPresent() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte1Binary();
        if (binaryString.length()!=8)
            return false;
        if (binaryString.charAt(0)=='1')
            return true;
        return false;
    }
    public boolean isKSNPresent() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte2Binary();
        if (binaryString.length()!=8)
            return false;
        if (binaryString.charAt(0)=='1')
            return true;
        return false;
    }
    public boolean isT1DataPresent() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte1Binary();
        if (binaryString.length()!=8)
            return false;
        if (binaryString.charAt(7)=='1')
            return true;
        return false;
    }
    public boolean isT2DataPresent() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte1Binary();
        if (binaryString.length()!=8)
            return false;
        if (binaryString.charAt(6)=='1')
            return true;
        return false;
    }

    public boolean isEncryptedWithTDES() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte1Binary();
        while (binaryString.length()<8)
            binaryString = "00"+binaryString;
        if (binaryString.substring(2,4).equals("00"))
            return true;
        return false;
    }
    public boolean isEncryptedWithAES() {
        if (!isDataEncrypted())
            return false;
        String binaryString = getFieldByte1Binary();
        if (binaryString.length()!=8)
            return false;
        if (binaryString.substring(2,4).equals("01"))
            return true;
        return false;
    }
    public boolean isDataEncrypted(){
        if (byteData[0]==0x25 && byteData[byteData.length-1]==0x0d)
            return false;
        if (byteData[0]==0x02 && byteData[byteData.length-1]==0x03)
            return true;
        return false;
    }
    public String getFieldByte1Binary() {
        String binaryString =  hexToBin(getFieldByte1());
        while (binaryString.length()<8)
            binaryString = "00"+binaryString;
        return binaryString;
    }
    public String getFieldByte2Binary() {
        String binaryString =  hexToBin(getFieldByte2());
        while (binaryString.length()<8)
            binaryString = "00"+binaryString;
        return binaryString;
    }
    private void convertByteToString() {
        String str = null;
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<byteData.length; i++){
            str = Integer.toHexString(0xFF & byteData[i]);
            if (str.length()==1)
                str="0"+str;
            hexString.append(str);
        }
        stringData = hexString.toString();

    }
    public String byteToString(byte[] value){
        String str = null;
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<value.length; i++){
            str = Integer.toHexString(0xFF & value[i]);
            if (str.length()==1)
                str="0"+str;
            hexString.append(str);
        }
        return hexString.toString();
    }
    public String byteToString (byte value) {
        return Integer.toHexString(0xFF & value);
    }

    private void convertStringToByte() {
        for (int i=0; i<stringData.length()/2; i++) {
            byteData[i]= (byte) ((Character.digit(stringData.charAt(i*2), 16) << 4) + Character.digit(stringData.charAt(i*2+1), 16));
        }

    }
    public String getStringData() {
        return stringData;
    }

    public byte[] getByteData() {
        return byteData;
    }
    public String toString() {
        String encryptionType = "No Encryption";

        if (isEncryptedWithTDES())
            encryptionType = "TDES";
        if (isEncryptedWithAES())
            encryptionType = "AES";

        if (isDataEncrypted()) {
            return  "Encryption Type: "+encryptionType + "\n" +
                    "STX: "+getSTX() + "\n" +
                    "Data Length: "+getDataLength()+"\n" +
                    "Card Encode Type: "+getCardEncodeType()+"\n" +
                    "Track 1-3 Status: "+getTrackStatus()+"\n" +
                    "Track 1 Data Length: " + getTrack1Length()+"\n"+
                    "Track 2 Data Length: " + getTrack2Length()+"\n"+
                    "Track 3 Data Length: " + getTrack3Length()+"\n"+
                    "Field Byte 1: " + getFieldByte1()+"\n"+
                    "Field Byte 2: " + getFieldByte2()+"\n\n"+
                    "Track 1 Data: " + getT1Data()+"\n\n"+
                    "Track 1 Data (Ascii): "+getT1DataAscii()+"\n\n"+
                    "Track 2 Data: " +getT2Data()+"\n\n"+
                    "Track 2 Data (Ascii): "+getT2DataAscii()+"\n\n"+
                    "Encrypted Track1: " +getEncryptedTrack1()+"\n\n"+
                    "Encrypted Track2: " +getEncryptedTrack2()+"\n\n"+
                    "Encrypted Section: " +getEncryptedSection()+"\n\n"+
                    "Serial Number: " + getSerialNumber()+"\n"+
                    "KSN: "+ getKSN()+"\n"+
                    "LRC: "+ getLRC()+"\n"+
                    "Check Sum: "+getCheckSum()+"\n"+
                    "ETX: "+getETX()+"\n\n"+
                    "Strting with STX: "+isStartingWithSTX()+"\n"+
                    "Ending with ETX: "+isEndingWithETX()+"\n"+
                    "Checking LRC: " +isLRCCorrect()+"\n"+
                    "Checking Check Sum: " +isCheckSumCorrect()+"\n";
        } else {
            return  "Encryption Type: "+encryptionType + "\n" +
                    "Track 1 Data: " + getT1Data()+"\n\n"+
                    "Track 1 Data (Ascii): "+getT1DataAscii()+"\n\n"+
                    "Track 2 Data: " +getT2Data()+"\n\n"+
                    "Track 2 Data (Ascii): "+getT2DataAscii()+"\n\n";

        }

    }
    public String hexToBin (String hex){
        return new BigInteger(hex, 16).toString(2);
    }

    private void parse() throws MagstripeParseException{
        try {

            // ------------------------------------------
            // --- Track 2 only cards
            // --- Ex: 1234123412341234=0305101193010877?
            // ------------------------------------------
            if(isT2DataPresent()) {
                _Track2Data = getT2DataAscii();
                // -- add sentinels if not there
                if (_Track2Data.charAt(0) != ';')
                    _Track2Data = ";" + _Track2Data;
                if (_Track2Data.charAt(_Track2Data.length() - 1) != '?')
                    _Track2Data += "?";

                int iSep = _Track2Data.indexOf("=");
                if (iSep == -1)
                    throw new MagstripeParseException("Invalid track 2 data string");
                _PAN = _Track2Data.substring(0, iSep);
                if (_PAN.charAt(0) == ';')
                    _PAN = _PAN.substring(1);

                _ExpYearStr = _Track2Data.substring(iSep + 1, iSep + 3);

                _ExpMonthStr = _Track2Data.substring(iSep + 3, iSep + 5);
            }

        } catch (ArrayIndexOutOfBoundsException oob) {
            throw new MagstripeParseException(oob.getMessage());
        } catch (NumberFormatException nfe) {
            throw new MagstripeParseException(nfe.getMessage());
        } catch (Exception exc) {
            throw new MagstripeParseException(exc.getMessage());
        }
    }
    public String getCreditCardNumber() {
        return _PAN;
    }

    public String getExpirationYear() {
        return _ExpYearStr;
    }

    public String getExpirationMonth() {
        return _ExpMonthStr;
    }

    public String get_CardHolderName() {
        if(isT1DataPresent())
         _CardHolderName = getT1DataAscii().split("\\^")[1].trim();
        return _CardHolderName;
    }


}
