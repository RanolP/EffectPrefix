package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixChangeEvent extends DefaultPlayerEvent {
	private Prefix before;
	private Prefix after;

	public PrefixChangeEvent(Player who, Prefix before, Prefix after) {
		super(who);
		this.before = before;
		this.after = after;
	}

	public Prefix getOldPrefix() {
		return before;
	}

	public Prefix getNewPrefix() {
		return after;
	}

	public void setNewPrefix(Prefix newPrefix) {
		this.after = newPrefix;
	}

}
