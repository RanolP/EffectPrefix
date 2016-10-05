package me.ranol.effectprefix.effects;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.api.effects.CompatiblePlugins;
import me.ranol.effectprefix.api.effects.EffectManager;
import me.ranol.effectprefix.api.effects.PrefixEffect;
import me.ranol.effectprefix.api.effects.RequirePlugins;
import me.ranol.effectprefix.api.effects.ResultTo;
import me.ranol.effectprefix.events.PrefixChangeEvent;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

@RequirePlugins({ "HolographicDisplays|filoghost|ProtocolLib이 없다면 살짝 오류가 날 수 있습니다." })
@CompatiblePlugins({ "ProtocolLib|[dmulloy2, comphenix]|완벽한 홀로그램을 지원합니다." })
public class EffHoloVisible extends PrefixEffect {
	private static final long serialVersionUID = -1867329429319893668L;
	@ResultTo("HolographicDisplays")
	private static boolean holographic;
	@ResultTo("ProtocolLib")
	private static boolean protocol;
	private HashMap<UUID, HolographicDisplaysHologram> holo = new HashMap<>();

	@Override
	public void initialize() {
	}

	@Override
	public String getCommand() {
		return "holovisible";
	}

	@Override
	public String getDescription() {
		return "칭호가 홀로그램으로 뜹니다.";
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		if (e.getFrom().distance(e.getTo()) == 0.0D || e.isAsynchronous()
				|| !holographic || !isSelected(e.getPlayer()))
			return;
		refresh(e.getPlayer());
	}

	@EventHandler
	public void onDeselect(PrefixChangeEvent e) {
		switch (e.getType()) {
		case DESELECT:
			if (e.getChangedPrefix().equals(getTarget())) {
				holo.get(e.getPlayer().getUniqueId()).dispose();
				holo.remove(e.getPlayer().getUniqueId());
			}
			break;
		case SELECT:
			if (e.isAsynchronous() || !holographic
					|| !e.getChangedPrefix().equals(getTarget()))
				refresh(e.getPlayer());
			break;
		default:
			return;
		}
	}

	public void refresh(Player player) {
		List<Prefix> sel = EffectManager.hasOptionSet(
				EffHoloVisible.class,
				(List<Prefix>) PrefixManager.getInstance().getSelectedPrefix(
						player));
		int count = sel.size() - sel.indexOf(getTarget());
		if (!holo.containsKey(player.getUniqueId())) {
			if (holographic) {
				holo.put(player.getUniqueId(), new HolographicDisplaysHologram(
						player, count));
			}
		} else {
			holo.get(player.getUniqueId()).update(player, count);
		}
	}

	public void dispose() {
		holo.values().forEach((h) -> h.dispose());
	}

	@Override
	public Material getMainIcon() {
		return Material.GOLDEN_CARROT;
	}

	class HolographicDisplaysHologram {
		Hologram hologram;
		private static final double playerHeight = 2.5;
		private static final double height = 0.25;

		public HolographicDisplaysHologram(Player player, int count) {
			hologram = HologramsAPI.createHologram(
					EffectPrefix.getInstance(),
					player.getLocation().add(0, playerHeight + count * height,
							0));
			hologram.appendTextLine(getTarget().getPrefix());
		}

		public void update(Player p, int count) {
			hologram.teleport(p.getLocation().add(0,
					playerHeight + count * height, 0));
		}

		public void dispose() {
			hologram.delete();
		}
	}

}
