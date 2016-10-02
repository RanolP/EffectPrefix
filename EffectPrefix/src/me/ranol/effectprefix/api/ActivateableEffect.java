package me.ranol.effectprefix.api;

import org.bukkit.event.block.Action;

public abstract class ActivateableEffect extends PrefixEffect {

	public abstract void activate(Action type, boolean force);
}
