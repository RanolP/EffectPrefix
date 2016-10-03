package me.ranol.effectprefix.effects;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.api.effects.EffectManager;
import me.ranol.effectprefix.api.effects.PrefixEffect;
import me.ranol.effectprefix.api.effects.RequireOnePlugins;
import me.ranol.effectprefix.api.effects.ResultTo;
import me.ranol.effectprefix.events.PrefixDeselectEvent;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

@RequireOnePlugins({ "HolographicDisplays|filoghost|추천하지 않는 연동입니다." })
public class EffHoloVisible extends PrefixEffect {
	private static final long serialVersionUID = -1867329429319893668L;
	@ResultTo("HolographicDisplays")
	private static boolean holographic;
	@ResultTo("ProtocolLib")
	private static boolean protocol;
	private HashMap<UUID, HologramHooker> holo = new HashMap<>();

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
				|| !(protocol || holographic) || !isSelected(e.getPlayer()))
			return;
		refresh(e.getPlayer());
	}

	@EventHandler
	public void onDeselect(PrefixDeselectEvent e) {
		if (e.getDeselectPrefix().equals(getTarget())) {
			holo.get(e.getPlayer().getUniqueId()).dispose();
			holo.remove(e.getPlayer().getUniqueId());
		}
	}

	public void refresh(Player player) {
		List<Prefix> sel = EffectManager.hasOptionSet(
				EffHoloVisible.class,
				(List<Prefix>) PrefixManager.getInstance().getSelectedPrefix(
						player));
		int count = sel.size() - sel.indexOf(getTarget());
		if (!holo.containsKey(player.getUniqueId())) {
			holo.put(player.getUniqueId(), new PrefixHologram(player, count));
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

	abstract class HologramHooker {
		protected static final double playerHeight = 2.5;
		protected static final double height = 0.25;

		public HologramHooker(Player player, int count) {

		}

		public abstract void update(Player player, int count);

		public abstract void dispose();
	}

	class PrefixHologram extends HologramHooker {
		Hologram hologram;
		private static final double playerHeight = 2.5;
		private static final double height = 0.25;

		public PrefixHologram(Player player, int count) {
			super(player, count);
			hologram = HologramsAPI.createHologram(
					EffectPrefix.getInstance(),
					player.getLocation().add(0, playerHeight + count * height,
							0));
			TextLine line = hologram.appendTextLine(getTarget().getPrefix());
			line.setTouchHandler((p) -> {
				Util.sendMessage(p, "&e이름&f: " + getTarget().getPrefixName());
				Util.sendMessage(p, "&b접두사&f: " + getTarget().getPrefix());
				Util.sendMessage(p, "&6설명&f: " + getTarget().getDescription());
				getTarget().getEffects().forEach((eff) -> {
					Util.sendMessage(p, "&6|    " + eff.getDescription());
				});
			});
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
