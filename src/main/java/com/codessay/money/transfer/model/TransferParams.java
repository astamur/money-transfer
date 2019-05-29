package com.codessay.money.transfer.model;

import io.javalin.validation.TypedValidator;

import java.math.BigDecimal;
import java.util.StringJoiner;

public class TransferParams {
    public static final String PROPERTY_FROM = "from";
    public static final String PROPERTY_TO = "to";
    public static final String PROPERTY_AMOUNT = "amount";

    private String from;
    private String to;
    private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferParams setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransferParams)) return false;

        TransferParams that = (TransferParams) o;

        if (getFrom() != null ? !getFrom().equals(that.getFrom()) : that.getFrom() != null) return false;
        if (getTo() != null ? !getTo().equals(that.getTo()) : that.getTo() != null) return false;
        return getAmount() != null ? getAmount().equals(that.getAmount()) : that.getAmount() == null;

    }

    @Override
    public int hashCode() {
        int result = getFrom() != null ? getFrom().hashCode() : 0;
        result = 31 * result + (getTo() != null ? getTo().hashCode() : 0);
        result = 31 * result + (getAmount() != null ? getAmount().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransferParams.class.getSimpleName() + "[", "]")
                .add("from='" + from + "'")
                .add("to='" + to + "'")
                .add("amount=" + amount)
                .toString();
    }

    public static TransferParams validate(TypedValidator<TransferParams> validator) {
        return validator
                .check(obj -> obj.getFrom() != null && obj.getFrom().matches(Account.ACCOUNT_ID_REGEX),
                        "Invalid id pattern for 'from' account")
                .check(obj -> obj.getTo() != null && obj.getTo().matches(Account.ACCOUNT_ID_REGEX),
                        "Invalid id pattern for 'to' account")
                .check(obj -> obj.getAmount() != null && obj.getAmount().scale() > -1 && obj.getAmount().scale() < 3,
                        "Amount can not be null and should have 2 digits after decimal point")
                .get();
    }
}