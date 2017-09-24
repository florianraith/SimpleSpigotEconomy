package me.dirantos.moneymaker.api.service;

import me.dirantos.moneymaker.api.account.AccountManager;
import me.dirantos.moneymaker.api.fetchers.AccountFetcher;
import me.dirantos.moneymaker.api.fetchers.BankFetcher;
import me.dirantos.moneymaker.api.fetchers.TransactionFetcher;
import me.dirantos.moneymaker.api.transaction.TransactionManager;
import me.dirantos.moneymaker.api.utils.ModelCache;

public interface MoneyMakerAPIService {

    AccountFetcher getAccountFetcher();

    BankFetcher getBankFetcher();

    TransactionFetcher getTransactionFetcher();

    TransactionManager getTransactionManager();

    AccountManager getAccountManager();

    ModelCache getCache();

}