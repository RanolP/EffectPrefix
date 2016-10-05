package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixChangeEvent extends DefaultPlayerEvent {
	private Prefix give;
	private ChangeType type;

	public PrefixChangeEvent(Player who, Prefix give, ChangeType type) {
		super(who);
		this.give = give;
		this.type = type;
	}

	public ChangeType getType() {
		return type;
	}

	public Prefix getChangedPrefix() {
		return give;
	}

	public void setChangedPrefix(Prefix newPrefix) {
		give = newPrefix;
	}

	public enum ChangeType {
		GIVE, GIVED, TAKE, TAKED, SELECT, DESELECT
	}
}
