package me.dirantos.moneymaker.spigot.managers;

import me.dirantos.moneymaker.api.fetchers.AccountFetcher;
import me.dirantos.moneymaker.api.fetchers.TransactionFetcher;
import me.dirantos.moneymaker.api.models.*;
import me.dirantos.moneymaker.api.managers.TransactionManager;
import me.dirantos.moneymaker.api.cache.ModelCache;
import me.dirantos.moneymaker.spigot.models.AccountImpl;
import org.apache.commons.lang.Validate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class TransactionManagerImpl implements TransactionManager {

    private final TransactionFactory factory = new TransactionFactory();
    private final TransactionFetcher transactionFetcher;
    private final AccountFetcher accountFetcher;
    private final ModelCache cache;

    public TransactionManagerImpl(TransactionFetcher transactionFetcher, AccountFetcher accountFetcher, ModelCache cache) {
        this.transactionFetcher = transactionFetcher;
        this.accountFetcher = accountFetcher;
        this.cache = cache;
    }

    @Override
    public Transfer makeTransfer(Account recipient, Account sender, double amount) {
        Transfer transfer = factory.createTransfer(recipient.getAccountNumber(), sender.getAccountNumber(), amount);
        Validate.isTrue(recipient.getAccountNumber() == transfer.getRecipientAccountNumber(), "The given recipient managers does not correspond with the transaction recipient managers-number");
        Validate.isTrue(sender.getAccountNumber() == transfer.getSenderAccountNumber(), "The given sender managers does not correspond with the transaction sender managers-number");

        if(sender.getBalance() - transfer.getAmount() < 0) throw new IllegalStateException("The sender has not enough money");
        sender.setBalance(sender.getBalance() - transfer.getAmount());
        recipient.setBalance(recipient.getBalance() + transfer.getAmount());

        Transaction validated = transactionFetcher.saveData(transfer);

        recipient.addTransaction(validated);
        sender.addTransaction(validated);
        accountFetcher.saveData(recipient);
        accountFetcher.saveData(sender);

        return (Transfer) validated;
    }

    @Override
    public Interest makeInterest(Account recipient, double interestRate) {
        Interest interest = factory.createInterest(recipient.getAccountNumber(), 0, interestRate);
        Validate.isTrue(recipient.getAccountNumber() == interest.getRecipientAccountNumber(), "The given recipient managers does not correspond with the transaction recipient managers-number");

        double averageBalances = ((AccountImpl) recipient).getBalanceChanges().stream().reduce(0.0, Double::sum) / 2.0;
        recipient.setBalance(recipient.getBalance() + (averageBalances * interest.getInterestRate()));
        recipient.addTransaction(interest);

        Transaction validated = transactionFetcher.saveData(interest);

        recipient.addTransaction(validated);
        accountFetcher.saveData(recipient);

        return (Interest) validated;
    }

    @Override
    public Transaction makeWithdrawal(Account recipient, double amount) {
        Transaction transaction = factory.createWithdrawal(recipient.getAccountNumber(), amount);
        Validate.isTrue(recipient.getAccountNumber() == transaction.getRecipientAccountNumber(), "The given recipient managers does not correspond with the transaction recipient managers-number");

        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        Transaction validated = transactionFetcher.saveData(transaction);

        recipient.addTransaction(validated);
        accountFetcher.saveData(recipient);

        return validated;
    }

    @Override
    public Transaction makeDeposit(Account recipient, double amount) {
        Transaction transaction = factory.createDeposit(recipient.getAccountNumber(), amount);
        Validate.isTrue(recipient.getAccountNumber() == transaction.getRecipientAccountNumber(), "The given recipient managers does not correspond with the transaction recipient managers-number");

        if(recipient.getBalance() - transaction.getAmount() < 0) recipient.setBalance(0);
        else recipient.setBalance(recipient.getBalance() - transaction.getAmount());

        Transaction validated = transactionFetcher.saveData(transaction);

        recipient.addTransaction(validated);
        accountFetcher.saveData(recipient);

        return validated;
    }

    @Override
    public Optional<Transaction> loadTransaction(int id) {
        Transaction transaction = cache.getTransactionCache().get(id);
        if(transaction == null) transaction = transactionFetcher.fetchData(id);
        return Optional.of(transaction);
    }

    @Override
    public Set<Transaction> loadTransactions(Set<Integer> ids) {
        Set<Transaction> transactions = new HashSet<>();
        Set<Integer> toFetch = new HashSet<>();

        for (int id : ids) {
            Transaction account = cache.getTransactionCache().get(id);
            if(account == null) {
                toFetch.add(id);
            } else {
                transactions.add(account);
            }
        }

        transactions.addAll(transactionFetcher.fetchMultipleData(toFetch));

        return transactions;
    }

    @Override
    public void deleteTransaction(int id) {
        cache.getTransactionCache().remove(id);
        transactionFetcher.deleteData(id);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        deleteTransaction(transaction.getID());
    }

}