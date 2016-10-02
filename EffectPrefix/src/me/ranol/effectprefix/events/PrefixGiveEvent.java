package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixGiveEvent extends DefaultPlayerEvent {
	private Prefix give;

	public PrefixGiveEvent(Player who, Prefix give) {
		super(who);
		this.give = give;
	}

	public Prefix getGivePrefix() {
		return give;
	}

	public void setGivePrefix(Prefix newPrefix) {
		give = newPrefix;
	}
}
