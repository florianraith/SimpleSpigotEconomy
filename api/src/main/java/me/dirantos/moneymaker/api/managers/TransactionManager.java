package me.dirantos.moneymaker.api.managers;

import me.dirantos.moneymaker.api.models.Account;
import me.dirantos.moneymaker.api.models.Interest;
import me.dirantos.moneymaker.api.models.Transaction;
import me.dirantos.moneymaker.api.models.Transfer;

import java.util.Optional;
import java.util.Set;

public interface TransactionManager {

    Transfer makeTransfer(Account recipient, Account sender, double amount);

    Interest makeInterest(Account recipient, double interestRate);

    Transaction makeWithdrawal(Account recipient, double amount);

    Transaction makeDeposit(Account recipient, double amount);

    Optional<Transaction> loadTransaction(int id);

    Set<Transaction> loadTransactions(Set<Integer> ids);

    void deleteTransaction(Transaction transaction);

    void deleteTransaction(int id);

}