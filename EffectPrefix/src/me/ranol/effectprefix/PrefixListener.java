package me.ranol.effectprefix;

import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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

	@EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
	public void onWorldChanged(PlayerTeleportEvent e) {
		if (e.getFrom().getWorld().equals(e.getTo().getWorld()))
			return;
		List<Prefix> before = (List<Prefix>) PrefixManager.getInstance()
				.getSelectedPrefix(e.getPlayer());
		before.forEach((prefix) -> {
			PrefixManager.getInstance().deselect(e.getPlayer(), prefix);
		});
		Bukkit.getScheduler().scheduleSyncDelayedTask(
				EffectPrefix.getInstance(),
				() -> {
					before.forEach((prefix) -> PrefixManager.getInstance()
							.select(e.getPlayer(), prefix));
				});
	}
}
