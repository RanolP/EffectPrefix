package me.ranol.effectprefix.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.ranol.effectprefix.designpatterns.Observer;
import me.ranol.effectprefix.events.PrefixChangeEvent;
import me.ranol.effectprefix.events.PrefixChangeEvent.ChangeType;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PrefixManager extends Observer<List<Prefix>> implements
		Serializable {
	private static final long serialVersionUID = 6740473927523193132L;
	private List<Prefix> prefix = new ArrayList<>();
	private HashMap<UUID, List<Prefix>> selected = new HashMap<>();
	private HashMap<UUID, List<Prefix>> has = new HashMap<>();
	private HashMap<UUID, Integer> canSelect = new HashMap<>();
	private static PrefixManager Instance;

	private PrefixManager() {
	}

	public int getCanSelectPrefixCount(OfflinePlayer player) {
		if (!canSelect.containsKey(player.getUniqueId()))
			canSelect.put(player.getUniqueId(), 3);
		return canSelect.get(player.getUniqueId());
	}

	public void setCanSelectPrefixCount(Player player, int count) {
		canSelect.put(player.getUniqueId(), 3);
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

	public boolean remove(Prefix prefix) {
		boolean result = true;
		for (Prefix pref : this.prefix) {
			if (pref.getPrefixName().equals(prefix.getPrefixName()))
				result = false;
		}
		if (result)
			return false;
		this.prefix.remove(prefix);
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

	@SuppressWarnings("unchecked")
	public <T extends Collection<Prefix>> T getSelectedPrefix(
			OfflinePlayer player) {
		if (!selected.containsKey(player.getUniqueId()))
			selected.put(player.getUniqueId(), new ArrayList<Prefix>());
		return (T) new ArrayList<Prefix>(selected.get(player.getUniqueId()));
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
			PrefixChangeEvent event = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.GIVE);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				has.get(player.getUniqueId()).add(event.getChangedPrefix());
			}
			PrefixChangeEvent event2 = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.GIVED);
			Bukkit.getPluginManager().callEvent(event2);
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
			PrefixChangeEvent event = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.TAKE);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				has.get(player.getUniqueId()).remove(event.getChangedPrefix());
				if (isSelected(player, event.getChangedPrefix())) {
					deselect(player, event.getChangedPrefix());
				}
			}
			PrefixChangeEvent event2 = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.TAKED);
			Bukkit.getPluginManager().callEvent(event2);
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
		if (getCanSelectPrefixCount(player) <= getSelectedPrefix(player).size())
			return false;
		if (player instanceof Player) {
			PrefixChangeEvent e = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.SELECT);
			Bukkit.getPluginManager().callEvent(e);
			if (!e.isCancelled())
				selected.get(player.getUniqueId()).add(prefix);
			else
				return false;
		} else
			selected.get(player.getUniqueId()).add(prefix);
		return true;
	}

	public boolean select(OfflinePlayer player, Prefix prefix, int index) {
		if (!hasPrefix(player, prefix))
			return false;
		if (isSelected(player, prefix))
			return false;
		if (getCanSelectPrefixCount(player) <= getSelectedPrefix(player).size())
			return false;
		if (player instanceof Player) {
			PrefixChangeEvent e = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.SELECT);
			Bukkit.getPluginManager().callEvent(e);
			if (!e.isCancelled())
				selected.get(player.getUniqueId()).add(index, prefix);
			else
				return false;
		} else
			selected.get(player.getUniqueId()).add(index, prefix);
		return true;
	}

	public boolean deselect(OfflinePlayer player, Prefix prefix) {
		if (!isSelected(player, prefix))
			return false;
		if (player instanceof Player) {
			PrefixChangeEvent e = new PrefixChangeEvent((Player) player,
					prefix, ChangeType.DESELECT);
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
