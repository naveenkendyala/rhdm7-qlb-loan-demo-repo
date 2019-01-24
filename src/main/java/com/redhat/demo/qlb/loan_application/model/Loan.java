package com.redhat.demo.qlb.loan_application.model;

import java.math.BigInteger;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Loan implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	@org.kie.api.definition.type.Label("Amount")
	private long amount;
	@org.kie.api.definition.type.Label("Duration")
	private int duration;
	@org.kie.api.definition.type.Label("Interest Rate")
	private double interestRate;
	@org.kie.api.definition.type.Label("Approved")
	private boolean approved;

	@org.kie.api.definition.type.Label("Comment")
	private java.lang.String comment;

	@org.kie.api.definition.type.Label("Monthly Repayment")
	private double monthlyRepayment;

	public Loan() {
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isApproved() {
		return this.approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public java.lang.String getComment() {
		return this.comment;
	}

	public void setComment(java.lang.String comment) {
		this.comment = comment;
	}

	public long getAmount() {
		return this.amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public double getInterestRate() {
		return this.interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public double getMonthlyRepayment() {
		return this.monthlyRepayment;
	}

	public void setMonthlyRepayment(double monthlyRepayment) {
		this.monthlyRepayment = monthlyRepayment;
	}

	public Loan(long amount, int duration, double interestRate,
			boolean approved, java.lang.String comment, double monthlyRepayment) {
		this.amount = amount;
		this.duration = duration;
		this.interestRate = interestRate;
		this.approved = approved;
		this.comment = comment;
		this.monthlyRepayment = monthlyRepayment;
	}
	
	@Override
    public String toString() {
        return "Loan{" +
                "amount=" + amount +
                ", duration=" + duration +
                ", interestRate=" + interestRate +
                ", approved=" + approved +
                ", comment='" + comment + '\'' +
                ", monthlyRepayment=" + monthlyRepayment +
                '}';
    }
}