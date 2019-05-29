package com.codessay.money.transfer.model;

public class TransferParams {
    private String from;
    private String to;
    private double amount;

    public String getFrom() {
        return from;
    }

    public TransferParams setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public TransferParams setTo(String to) {
        this.to = to;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public TransferParams setAmount(double amount) {
        this.amount = amount;
        return this;
    }
}