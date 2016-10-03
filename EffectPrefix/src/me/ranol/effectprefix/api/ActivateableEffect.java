package me.ranol.effectprefix.api;

import me.ranol.effectprefix.api.effects.PrefixEffect;

import org.bukkit.event.block.Action;

public abstract class ActivateableEffect extends PrefixEffect {

	private static final long serialVersionUID = 8233586399311719079L;

	public abstract void activate(Action type, boolean force);
}
