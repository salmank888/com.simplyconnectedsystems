package com.simplyconnectedsystems.achsupport;

public class ACH {

	String ach_category_text, aba, dda, checkNumber,  ach_account_type, close_date, ach_name, owner_name, owner_street, owner_street2, owner_city, owner_state, owner_zip, owner_country, owner_email, owner_phone;

	
	public ACH() {
		
	}

	
	public ACH(String ach_category_text, String aba, String dda, String check_NumberString, String ach_account_type,
			String close_date, String ach_name, String owner_name,
			String owner_street, String owner_street2, String owner_city,
			String owner_state, String owner_zip, String owner_country,
			String owner_email, String owner_phone) {
		
		this.ach_category_text = ach_category_text;
		this.aba = aba;
		this.dda = dda;
		this.checkNumber = check_NumberString;
		this.ach_account_type = ach_account_type;
		this.close_date = close_date;
		this.ach_name = ach_name;
		this.owner_name = owner_name;
		this.owner_street = owner_street;
		this.owner_street2 = owner_street2;
		this.owner_city = owner_city;
		this.owner_state = owner_state;
		this.owner_zip = owner_zip;
		this.owner_country = owner_country;
		this.owner_email = owner_email;
		this.owner_phone = owner_phone;
	}


	public String getAch_category_text() {
		return ach_category_text;
	}

	public void setAch_category_text(String ach_category_text) {
		this.ach_category_text = ach_category_text;
	}
	
	public String getAba() {
		return aba;
	}

	public void setAba(String aba) {
		this.aba = aba;
	}

	public String getDda() {
		return dda;
	}

	public void setDda(String dda) {
		this.dda = dda;
	}

	public String getAch_account_type() {
		return ach_account_type;
	}

	public void setAch_account_type(String ach_account_type) {
		this.ach_account_type = ach_account_type;
	}

	public String getClose_date() {
		return close_date;
	}

	public void setClose_date(String close_date) {
		this.close_date = close_date;
	}

	public String getAch_name() {
		return ach_name;
	}

	public void setAch_name(String ach_name) {
		this.ach_name = ach_name;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getOwner_street() {
		return owner_street;
	}

	public void setOwner_street(String owner_street) {
		this.owner_street = owner_street;
	}

	public String getOwner_street2() {
		return owner_street2;
	}

	public void setOwner_street2(String owner_street2) {
		this.owner_street2 = owner_street2;
	}

	public String getOwner_city() {
		return owner_city;
	}

	public void setOwner_city(String owner_city) {
		this.owner_city = owner_city;
	}

	public String getOwner_state() {
		return owner_state;
	}

	public void setOwner_state(String owner_state) {
		this.owner_state = owner_state;
	}

	public String getOwner_zip() {
		return owner_zip;
	}

	public void setOwner_zip(String owner_zip) {
		this.owner_zip = owner_zip;
	}

	public String getOwner_country() {
		return owner_country;
	}

	public void setOwner_country(String owner_country) {
		this.owner_country = owner_country;
	}

	public String getOwner_email() {
		return owner_email;
	}

	public void setOwner_email(String owner_email) {
		this.owner_email = owner_email;
	}

	public String getOwner_phone() {
		return owner_phone;
	}

	public void setOwner_phone(String owner_phone) {
		this.owner_phone = owner_phone;
	}

	public String getDirectPostString(){
		String result = "<FIELD KEY=\"ach_category_text\">" + this.ach_category_text;
	    result += "</FIELD><FIELD KEY=\"aba\">" + this.aba;
		result += "</FIELD><FIELD KEY=\"dda\">" + this.dda;
		result += "</FIELD><FIELD KEY=\"total_additional_fields\">1</FIELD><FIELD KEY=\"field_name1\">CheckNumber</FIELD><FIELD KEY=\"field_value1\">" + this.checkNumber;
		result += "</FIELD><FIELD KEY=\"ach_account_type\">" + this.ach_account_type;
		result += "</FIELD><FIELD KEY=\"close_date\">" + this.close_date;
		result += "</FIELD><FIELD KEY=\"ach_name\">" + this.ach_name;
		result += "</FIELD><FIELD KEY=\"owner_name\">" + this.owner_name;
		result += "</FIELD><FIELD KEY=\"owner_street\">" + this.owner_street;
		result += "</FIELD><FIELD KEY=\"owner_street2\">" + this.owner_street2;
		result += "</FIELD><FIELD KEY=\"owner_city\">" + this.owner_country;
		result += "</FIELD><FIELD KEY=\"owner_state\">" + this.owner_state;
		result += "</FIELD><FIELD KEY=\"owner_zip\">" + this.owner_zip;
		result += "</FIELD><FIELD KEY=\"owner_country\">US</FIELD><FIELD KEY=\"owner_email\"></FIELD><FIELD KEY=\"owner_phone\">" + this.owner_phone + "</FIELD>";

		return result;
		
	}
}
