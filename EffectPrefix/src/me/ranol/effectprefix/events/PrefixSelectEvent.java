package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixSelectEvent extends DefaultPlayerEvent {
	private Prefix selected;

	public PrefixSelectEvent(Player who, Prefix selected) {
		super(who);
		this.selected = selected;
	}

	public Prefix getSelectPrefix() {
		return selected;
	}

}
