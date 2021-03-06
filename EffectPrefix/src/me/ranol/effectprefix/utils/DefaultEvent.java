package me.ranol.effectprefix.utils;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class DefaultEvent extends Event implements Cancellable {
	boolean toCancel = false;
	private static final HandlerList handlers = new HandlerList();

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

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
