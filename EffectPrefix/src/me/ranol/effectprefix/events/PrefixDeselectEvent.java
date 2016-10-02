package me.ranol.effectprefix.events;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.utils.DefaultPlayerEvent;

import org.bukkit.entity.Player;

public class PrefixDeselectEvent extends DefaultPlayerEvent {
	private Prefix deselected;

	public PrefixDeselectEvent(Player who, Prefix deselected) {
		super(who);
		this.deselected = deselected;
	}

	public Prefix getDeselectPrefix() {
		return deselected;
	}

}
