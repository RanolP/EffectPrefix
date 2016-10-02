package me.ranol.effectprefix.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class DefaultPlayerEvent extends PlayerEvent implements
		Cancellable {
	boolean toCancel = false;
	private static final HandlerList handlers = new HandlerList();

	public DefaultPlayerEvent(Player who) {
		super(who);
	}

	@Override
	public boolean isCancelled() {
		return toCancel;
	}

	@Override
	public void setCancelled(boolean newValue) {
		toCancel = newValue;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public HandlerList getHandlerList() {
		return handlers;
	}

}
