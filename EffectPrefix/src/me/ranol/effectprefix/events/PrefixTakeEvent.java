package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixTakeEvent extends DefaultPlayerEvent {
	private Prefix take;

	public PrefixTakeEvent(Player who, Prefix take) {
		super(who);
		this.take = take;
	}

	public Prefix getTakePrefix() {
		return take;
	}

	public void setTakePrefix(Prefix newPrefix) {
		this.take = newPrefix;
	}

}
