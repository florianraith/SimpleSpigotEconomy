package me.dirantos.moneymaker.spigot.commands;

import me.dirantos.moneymaker.api.managers.AccountManager;
import me.dirantos.moneymaker.api.models.Account;
import me.dirantos.moneymaker.api.service.MoneyMakerAPI;
import me.dirantos.moneymaker.spigot.chat.ChatLevel;
import me.dirantos.moneymaker.spigot.command.CommandInfo;
import me.dirantos.moneymaker.spigot.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandInfo(name = "delete", permission = "moneymaker.cmd.account.delete", usage = "delete [managers-number]", description = "deletes a managers", playerOnly = true)
public class CmdDeleteAccount extends SubCommand {

    @Override
    protected void handle(CommandSender sender, String[] args) {
        if(args.length == 0) {
            getMessanger().send(sender, "You have to give the managers-number from your managers!", ChatLevel.ERROR);
            return;
        }

        int accountNumber;
        try {
           accountNumber = Integer.parseInt(args[0]);
        } catch(NumberFormatException e) {
            getMessanger().send(sender, "Wrong arguments!", ChatLevel.ERROR);
            return;
        }

        AccountManager accountManager = MoneyMakerAPI.getService().getAccountManager();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            Optional<Account> account = accountManager.loadAccount(accountNumber);

            if(!account.isPresent()) {
                getMessanger().send(sender, "The managers could not be found!", ChatLevel.ERROR);
                return;
            }

            if(!account.get().getOwner().equals(((Player) sender).getUniqueId())) {
                getMessanger().send(sender, "The managers does not belong to you!", ChatLevel.ERROR);
                return;
            }

            accountManager.deleteAccount(account.get());
            getMessanger().send(sender, "The managers __" + accountNumber + "__ has successfully been deleted!", ChatLevel.SUCCESS);

        });
    }

}
