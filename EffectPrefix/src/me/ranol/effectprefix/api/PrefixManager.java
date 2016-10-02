package me.ranol.effectprefix.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.ranol.effectprefix.designpatterns.Observer;
import me.ranol.effectprefix.events.PrefixDeselectEvent;
import me.ranol.effectprefix.events.PrefixGiveEvent;
import me.ranol.effectprefix.events.PrefixSelectEvent;
import me.ranol.effectprefix.events.PrefixTakeEvent;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PrefixManager extends Observer<List<Prefix>> implements
		Serializable {
	private static final long serialVersionUID = 6740473927523193132L;
	private List<Prefix> prefix = new ArrayList<>();
	private HashMap<UUID, List<Prefix>> selected = new HashMap<>();
	private HashMap<UUID, List<Prefix>> has = new HashMap<>();
	private static PrefixManager Instance;

	private PrefixManager() {
	}

	public static PrefixManager getInstance() {
		if (Instance == null)
			synchronized (PrefixManager.class) {
				Instance = new PrefixManager();
			}
		return Instance;
	}

	public boolean register(Prefix prefix) {
		for (Prefix pref : this.prefix) {
			if (pref.getPrefixName().equals(prefix.getPrefixName()))
				return false;
		}
		this.prefix.add(prefix);
		updateAll(this.prefix);
		return true;
	}

	public void replace(Prefix prefix) {
		for (Prefix pref : this.prefix) {
			if (pref.getPrefixName().equals(prefix.getPrefixName())) {
				this.prefix.remove(pref);
				break;
			}
		}
		updateAll(this.prefix);
		this.prefix.add(prefix);
	}

	public Collection<Prefix> getAllPrefix() {
		return new ArrayList<>(prefix);
	}

	public Collection<Prefix> getSelectedPrefix(OfflinePlayer player) {
		if (!selected.containsKey(player.getUniqueId()))
			selected.put(player.getUniqueId(), new ArrayList<Prefix>());
		return new ArrayList<Prefix>(selected.get(player.getUniqueId()));
	}

	public Collection<Prefix> getHasPrefix(OfflinePlayer player) {
		if (player.isOp())
			return getAllPrefix();
		if (!has.containsKey(player.getUniqueId()))
			has.put(player.getUniqueId(), new ArrayList<Prefix>());
		return new ArrayList<Prefix>(has.get(player.getUniqueId()));
	}

	public boolean hasPrefix(OfflinePlayer player, Prefix prefix) {
		return getHasPrefix(player).contains(prefix);
	}

	public boolean isSelected(OfflinePlayer player, Prefix prefix) {
		return getSelectedPrefix(player).contains(prefix);
	}

	public boolean givePrefix(OfflinePlayer player, Prefix prefix) {
		if (hasPrefix(player, prefix))
			return false;
		if (!has.containsKey(player.getUniqueId()))
			has.put(player.getUniqueId(), new ArrayList<Prefix>());
		if (player instanceof Player) {
			PrefixGiveEvent event = new PrefixGiveEvent((Player) player, prefix);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				has.get(player.getUniqueId()).add(event.getGivePrefix());

			}
		} else {
			has.get(player.getUniqueId()).add(prefix);
		}
		return true;
	}

	public boolean takePrefix(OfflinePlayer player, Prefix prefix) {
		if (!hasPrefix(player, prefix))
			return false;
		if (!has.containsKey(player.getUniqueId()))
			has.put(player.getUniqueId(), new ArrayList<Prefix>());
		if (player instanceof Player) {
			PrefixTakeEvent event = new PrefixTakeEvent((Player) player, prefix);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				has.get(player.getUniqueId()).remove(event.getTakePrefix());
				if (isSelected(player, event.getTakePrefix())) {
					deselect(player, event.getTakePrefix());
				}
			}
		} else {
			has.get(player.getUniqueId()).remove(prefix);
			if (isSelected(player, prefix)) {
				deselect(player, prefix);
			}
		}
		return true;
	}

	public boolean select(OfflinePlayer player, Prefix prefix) {
		if (!hasPrefix(player, prefix))
			return false;
		if (isSelected(player, prefix))
			return false;
		if (player instanceof Player) {
			PrefixSelectEvent e = new PrefixSelectEvent((Player) player, prefix);
			Bukkit.getPluginManager().callEvent(e);
			if (!e.isCancelled())
				selected.get(player.getUniqueId()).add(prefix);
		} else
			selected.get(player.getUniqueId()).add(prefix);
		return true;
	}

	public boolean deselect(OfflinePlayer player, Prefix prefix) {
		if (!isSelected(player, prefix))
			return false;
		if (player instanceof Player) {
			PrefixDeselectEvent e = new PrefixDeselectEvent((Player) player,
					prefix);
			Bukkit.getPluginManager().callEvent(e);
			if (!e.isCancelled())
				selected.get(player.getUniqueId()).remove(prefix);
		} else
			selected.get(player.getUniqueId()).remove(prefix);
		return true;
	}

	public Prefix get(String name) {
		Prefix result = null;
		for (Prefix pref : this.prefix) {
			if (pref.getPrefixName().equals(name))
				result = pref;
		}
		return result;
	}
}
