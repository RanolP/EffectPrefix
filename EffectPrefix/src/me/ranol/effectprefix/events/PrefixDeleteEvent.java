package me.ranol.effectprefix.events;

import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixDeleteEvent extends DefaultPlayerEvent {

	public PrefixDeleteEvent(Player who) {
		super(who);
	}

}
