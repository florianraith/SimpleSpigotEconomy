package me.dirantos.economy.spigot.models;

import me.dirantos.economy.api.models.Interest;
import me.dirantos.economy.api.models.TransactionType;

import java.util.Date;

public final class InterestImpl extends TransactionImpl implements Interest {

    private final double interestRate;

    public InterestImpl(int id, int recipient, double amount, Date date, double interestRate) {
        super(id, recipient, amount, date, TransactionType.INTEREST);
        this.interestRate = interestRate;
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public String toString() {
        return "InterestImpl{" +
                "interestRate=" + interestRate +
                "} " + super.toString();
    }
}