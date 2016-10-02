package me.ranol.effectprefix;

import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PrefixListener implements Listener {
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		List<Prefix> prefixes = (List<Prefix>) PrefixManager.getInstance()
				.getSelectedPrefix(e.getPlayer());
		if (prefixes.size() <= 0)
			return;
		String format = "";
		for (Prefix pref : prefixes) {
			format += pref.getPrefix() + " ";
		}
		e.setFormat(format + "§r" + e.getFormat());
	}
}
