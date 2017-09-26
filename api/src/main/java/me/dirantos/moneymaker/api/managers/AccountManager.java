package me.dirantos.moneymaker.api.managers;

import me.dirantos.moneymaker.api.models.Account;
import me.dirantos.moneymaker.api.models.Transaction;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AccountManager {

    Account createNewAccount(UUID owner, double startBalance);

    Account createNewAccount(UUID owner);

    Optional<Account> loadAccount(Integer accountNumber);

    Set<Account> loadAccounts(Set<Integer> accountNumbers);

    Set<Transaction> loadAllTransactions(Account account);

    void deleteAccount(Account account);

    void deleteAccount(int accountNumber);

}
