package com.simplyconnectedsystems.cardsupport;

public class Recurring {

	private String recurringNumber;
	private String recurringStartDate;
	private String recurringType;
	private String recurringEndDate;
	
	
	public Recurring(String recurringNumber, String recurringStartDate,
			String recurringType, String recurringEndDate) {
		super();
		this.recurringNumber = recurringNumber;
		this.recurringStartDate = recurringStartDate;
		this.recurringType = recurringType;
		this.recurringEndDate = recurringEndDate;
	}
	
	
	public String getRecurringNumber() {
		return recurringNumber;
	}
	public void setRecurringNumber(String recurringNumber) {
		this.recurringNumber = recurringNumber;
	}
	public String getRecurringStartDate() {
		return recurringStartDate;
	}
	public void setRecurringStartDate(String recurringStartDate) {
		this.recurringStartDate = recurringStartDate;
	}
	public String getRecurringType() {
		return recurringType;
	}
	public void setRecurringType(String recurringType) {
		this.recurringType = recurringType;
	}
	public String getRecurringEndDate() {
		return recurringEndDate;
	}
	public void setRecurringEndDate(String recurringEndDate) {
		this.recurringEndDate = recurringEndDate;
	}
	
	
	public String getRecurringDirectPostString() {
		return "<FIELD KEY=\"recurring\">" + this.recurringNumber + "</FIELD><FIELD KEY=\"recurring_start_date\">" + this.recurringStartDate  + "</FIELD><FIELD KEY=\"recurring_type\">" + this.recurringType  +  "</FIELD><FIELD KEY=\"recurring_end_date\">" +  this.recurringEndDate  + "</FIELD>";
	}
	
}
