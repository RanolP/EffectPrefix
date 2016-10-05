package me.ranol.effectprefix;

import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.api.effects.PrefixEffect;
import me.ranol.effectprefix.events.ServerLoadCompleteEvent;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

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

	@EventHandler
	public void onServerLoaded(ServerLoadCompleteEvent e) {
		PrefixEffect.registeration();
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

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDisabled(PluginDisableEvent e) {
		if (e.getPlugin().getName().equals("EffectPrefix")) {
			Bukkit.getOnlinePlayers().forEach(
					(player) -> {
						PrefixManager
								.getInstance()
								.getSelectedPrefix(player)
								.forEach(
										(prefix) -> PrefixManager.getInstance()
												.deselect(player, prefix));
					});
		}
	}

	@EventHandler
	public void onUsePrefixBook(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
			if (Prefix.isPrefixBook(hand)) {
				Prefix given = Prefix.loadByStack(e.getItem());
				if (given != null)
					if (PrefixManager.getInstance().givePrefix(e.getPlayer(),
							given)) {
						Util.sendMessage(e.getPlayer(),
								"축하합니다! 칭호 " + given.getPrefixName()
										+ "을(를) 획득했습니다!");
						if (hand.getAmount() > 1) {
							hand.setAmount(hand.getAmount() - 1);
						} else
							hand.setType(Material.AIR);
						Util.playSound(e.getPlayer(), Sound.BLOCK_CHEST_OPEN);
						e.getPlayer().getInventory().setItemInHand(hand);
					}
			}
		}
	}
}
