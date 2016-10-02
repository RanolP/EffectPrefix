package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultEvent;

public class PrefixCreateEvent extends DefaultEvent {
	private Prefix prefix;

	public PrefixCreateEvent(Prefix prefix) {
		this.prefix = prefix;
	}

	public Prefix getPrefix() {
		return prefix;
	}

}
