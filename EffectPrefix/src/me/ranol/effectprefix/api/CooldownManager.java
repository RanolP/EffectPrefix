package me.ranol.effectprefix.api;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class CooldownManager {
	private static HashMap<Cooldown, Long> cooldownMap = new HashMap<>();

	public double getCooldown(Cooldown info) {
		double cooldown = (cooldownMap.get(info).longValue() - System
				.currentTimeMillis()) / 1000;
		if (cooldown > 0)
			return cooldown;
		else {
			cooldownMap.remove(info);
			return 0;
		}
	}

	public double getCooldown(Player player, ActivateableEffect effect,
			Action action) {
		return getCooldown(new Cooldown(player, effect, action));
	}

	public class Cooldown {
		ActivateableEffect effect;
		Action action;
		boolean sneak;
		UUID player;

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Cooldown))
				return false;
			Cooldown other = (Cooldown) obj;
			return other.action == action && other.effect.equals(effect)
					&& other.sneak == sneak && other.player.equals(player);
		}

		public Cooldown(Player player, ActivateableEffect effect, Action action) {
			this.sneak = player.isSneaking();
			this.effect = effect;
			this.action = action;
			this.player = player.getUniqueId();
		}

		public void register() {
			cooldownMap.put(this, System.currentTimeMillis());
		}
	}
}
