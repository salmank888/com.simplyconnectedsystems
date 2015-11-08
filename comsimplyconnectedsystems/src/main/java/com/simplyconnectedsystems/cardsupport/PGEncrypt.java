package com.simplyconnectedsystems.cardsupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class PGEncrypt {

	private RSAPublicKey pubKey;
	private String keyId;

	private static int AES_BITS = 256;
	private static String FORMAT_ID = "GWSC";
	private static String VERSION = "1";

	/**
	 * Sets the public key that will be used for encryption.
	 * 
	 * @param key
	 *            Found in the merchant control panel in your gateway.
	 */
	public void setKey(String key) throws IllegalArgumentException {
		if (!key.startsWith("***") || !key.endsWith("***")) {
			throw new IllegalArgumentException(
					"Key is not valid. Should start and end with '***'");
		}
		String[] keys = key.split("\\|");
		this.keyId = keys[0].substring(3);
		try {
			KeyFactory keyFact = KeyFactory.getInstance("RSA");

			byte[] decodedKey = Base64.decode(
					keys[1].substring(0, keys[1].length() - 3),
					Base64.NO_OPTIONS);

			ByteArrayInputStream inStream = new ByteArrayInputStream(decodedKey);

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf
					.generateCertificate(inStream);

			X509EncodedKeySpec spec = new X509EncodedKeySpec(cert
					.getPublicKey().getEncoded());
			this.pubKey = (RSAPublicKey) keyFact.generatePublic(spec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypts the given string using the following method. First creates a
	 * random AES key and IV. Uses the newly created AES key to encrypt the
	 * credit card information. Then uses the RSA public key to encrypt the AES
	 * key. Finally formats the data and returns the encrypted string.
	 * 
	 * @param plaintext
	 * @return
	 */
	public String encrypt(String plaintext) {
		Cipher rsaCipher, aesCipher;
		try {
			// Create AES key
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(AES_BITS);
			Key aesKey = keyGen.generateKey();

			// Create Random IV
			byte[] iv = SecureRandom.getSeed(16);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			// Encrypt data using AES
			aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
			byte[] data = aesCipher.doFinal(plaintext.getBytes());

			// Encrypt AES key using RSA public key
			rsaCipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
			rsaCipher.init(Cipher.ENCRYPT_MODE, this.pubKey);
			byte[] encKey = rsaCipher.doFinal(aesKey.getEncoded());

			// Create output
			String keyResult = new String(Base64.encodeBytes(encKey, 0));
			String ivResult = new String(Base64.encodeBytes(iv, 0));
			String dataResult = new String(Base64.encodeBytes(data, 0));
			String result = FORMAT_ID + "|" + VERSION + "|" + this.keyId + "|"
					+ keyResult + "|" + ivResult + "|" + dataResult;
			return Base64.encodeBytes(result.getBytes(), Base64.URL_SAFE);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Encryption_Failed";
	}

	/**
	 * Gets the direct post string from the passed PaymentGatewayCard object.
	 * Returns the encrypted string.
	 * 
	 * @param card
	 *            Card that needs to be encrypted.
	 * @param includeCVV
	 *            Whether or not to encrypt the cvv.
	 * @return
	 */
	public String encrypt(PGCard card, boolean includeCVV) {
		return this.encrypt(card.getDirectPostString(includeCVV));
	}
}
