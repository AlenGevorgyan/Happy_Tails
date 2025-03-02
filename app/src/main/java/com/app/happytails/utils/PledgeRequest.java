package com.app.happytails.utils;

public class PledgeRequest {
    private String patronId;
    private double amount;
    private String currency;

    public PledgeRequest(String patronId, double amount, String currency) {
        this.patronId = patronId;
        this.amount = amount;
        this.currency = currency;
    }

    public String getPatronId() {
        return patronId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
