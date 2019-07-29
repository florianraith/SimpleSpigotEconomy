package me.dirantos.economy.spigot.fetchers;

import me.dirantos.economy.api.cache.ModelCache;
import me.dirantos.economy.api.fetchers.AccountFetcher;
import me.dirantos.economy.api.models.Account;
import me.dirantos.economy.spigot.EconomyPlugin;
import me.dirantos.economy.spigot.models.AccountImpl;
import me.dirantos.economy.spigot.mysql.MySQLConnectionPool;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public final class AccountFetcherImpl extends DataFetcherImpl<Account, Integer> implements AccountFetcher {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `accounts` ( `id` INT UNSIGNED NOT NULL AUTO_INCREMENT , `owner` VARCHAR(36) NOT NULL , `balance` DECIMAL(15,2) NOT NULL , `transactions` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) AUTO_INCREMENT = 100000";
    private static final String INSERT_DATA = "INSERT INTO `accounts` (`id`, `owner`, `balance`, `transactions`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `owner` = VALUES(`owner`), `balance` = VALUES(`balance`), `transactions` = VALUES(`transactions`)";
    private static final String INSERT_MULTIPLE_DATA = "INSERT INTO `accounts` (`id`, `owner`, `balance`, `transactions`) VALUES $values$ ON DUPLICATE KEY UPDATE `owner` = VALUES(`owner`), `balance` = VALUES(`balance`), `transactions` = VALUES(`transactions`)";
    private static final String FETCH_DATA = "SELECT * FROM `accounts` WHERE `id` = ?";
    private static final String FETCH_MULTIPLE_DATA = "SELECT * FROM `accounts` WHERE `id` IN $values$";
    private static final String DELETE_DATA = "DELETE FROM `accounts` WHERE `id` = ?";

    public AccountFetcherImpl(MySQLConnectionPool mySQL, EconomyPlugin plugin, ModelCache cache) {
        super(mySQL, plugin, cache);
    }

    @Override
    public void createTableIfNotExists() {
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Account fetchData(Integer id) {
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(FETCH_DATA)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if(result.next()) {

                    UUID owner = UUID.fromString(result.getString("owner"));
                    double balance = Double.parseDouble(result.getString("balance"));
                    String[] arr = result.getString("transactions").split(",");
                    Set<Integer> transactions = new HashSet<>();
                    for (String s : arr) {
                        int x = -1;
                        try {
                            x = Integer.parseInt(s);
                        } catch (NumberFormatException e) {}
                        if(x != -1) transactions.add(x);

                    }
                    Account account = new AccountImpl(id, owner, balance, transactions);
                    getCache().getAccountCache().add(id, account);
                    return account;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<Account> fetchMultipleData(Set<Integer> ids) {
        String query = multipleFetchBuilder(FETCH_MULTIPLE_DATA, ids.size());
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            for (int id : ids) {
                statement.setInt(i, id);
                i++;
            }
            try(ResultSet result = statement.executeQuery()) {
                Set<Account> accountSet = new HashSet<>();
                while(result.next()) {
                    int id = result.getInt("id");
                    UUID owner = UUID.fromString(result.getString("owner"));
                    double balance = Double.parseDouble(result.getString("balance"));
                    String[] arr = result.getString("transactions").split(",");
                    Set<Integer> transactions = new HashSet<>();
                    for (String s : arr) {
                        int x = -1;
                        try {
                            x = Integer.parseInt(s);
                        } catch (NumberFormatException e) {}
                        if(x != -1) transactions.add(x);
                    }
                    Account account = new AccountImpl(id, owner, balance, transactions);
                    getCache().getAccountCache().add(id, account);
                    accountSet.add(account);
                }
                return accountSet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account saveData(Account data) {
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_DATA,Statement.RETURN_GENERATED_KEYS)) {
            if(data.getAccountNumber() != -1) statement.setInt(1, data.getAccountNumber());
            else statement.setNull(1, Types.INTEGER);
            statement.setString(2, data.getOwner().toString());
            statement.setString(3, String.format(Locale.ENGLISH, "%.2f", data.getBalance()));
            statement.setString(4, data.getTransactionIDs().stream().map(Object::toString).collect(Collectors.joining(",")));
            int affectedRows = statement.executeUpdate();
            if(affectedRows != 0) {
                try(ResultSet result = statement.getGeneratedKeys()) {
                    if(result.next()) {
                        ((AccountImpl) data).setAccountNumber(result.getInt(1));
                        return data;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveMultipleData(Set<Account> dataSet) {
        String query = multipleInsertBuilder(INSERT_MULTIPLE_DATA, "(?,?,?,?)", dataSet.size());
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            int i = 1;
            for (Account data : dataSet) {
                statement.setInt(i, data.getAccountNumber());
                statement.setString(i+1, data.getOwner().toString());
                statement.setString(i+2, String.format(Locale.ENGLISH, "%.2f", data.getBalance()));
                statement.setString(i+3, data.getTransactionIDs().stream().map(Object::toString).collect(Collectors.joining(",")));
                i += 4;
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteData(Integer id) {
        getCache().getAccountCache().remove(id);
        try(Connection connection = getMySQL().getConnection(); PreparedStatement statement = connection.prepareStatement(DELETE_DATA)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}