package me.dirantos.moneymaker.spigot.transaction;

import me.dirantos.moneymaker.api.models.Interest;
import me.dirantos.moneymaker.api.models.Transaction;
import me.dirantos.moneymaker.api.models.TransactionType;
import me.dirantos.moneymaker.api.models.Transfer;
import me.dirantos.moneymaker.spigot.models.InterestImpl;
import me.dirantos.moneymaker.spigot.models.TransactionImpl;
import me.dirantos.moneymaker.spigot.models.TransferImpl;

import java.util.Date;

public final class TransactionFactory {

    public Transaction createTransaction(TransactionType type, int recipient, int sender, double amount, double interestRate) {
        switch(type) {
            case TRANSFER:
                return new TransferImpl(-1, recipient, amount, new Date(), sender);
            case INTEREST:
                return new InterestImpl(-1, recipient, amount, new Date(), interestRate);
            default:
                return new TransactionImpl(-1, recipient, amount, new Date(), type);
        }
    }

    public Transfer createTransfer(int recipient, int sender, double amount) {
        return (Transfer) createTransaction(TransactionType.TRANSFER, recipient, sender, amount, 0);
    }

    public Interest createInterest(int recipient, double amount, double interestRate) {
        return (Interest) createTransaction(TransactionType.INTEREST, recipient, 0, amount, interestRate);
    }

    public Transaction createWithdrawal(int account, double amount) {
        return createTransaction(TransactionType.WITHDRAWAL, account, 0, amount, 0);
    }

    public Transaction createDeposit(int account, double amount) {
        return createTransaction(TransactionType.DEPOSIT, account, 0, amount, 0);
    }

}
