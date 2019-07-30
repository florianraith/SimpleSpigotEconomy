package me.dirantos.economy.api.account;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event gets called when an account gets deleted
 */
public class AsyncAccountDeleteEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Account account;

    public AsyncAccountDeleteEvent(Account account) {
        super(true);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}