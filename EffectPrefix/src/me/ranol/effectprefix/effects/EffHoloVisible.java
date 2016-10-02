package me.ranol.effectprefix.effects;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import me.ranol.effectprefix.api.PrefixEffect;

public class EffHoloVisible extends PrefixEffect {
	private static final long serialVersionUID = -1867329429319893668L;

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
		if (e.getFrom().distance(e.getTo()) < 0.0001)
			return;

	}

	@Override
	public Material getMainIcon() {
		return Material.GOLDEN_CARROT;
	}
}
