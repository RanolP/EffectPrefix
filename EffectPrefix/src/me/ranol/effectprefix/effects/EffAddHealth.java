package me.ranol.effectprefix.effects;

import me.ranol.effectprefix.api.Argument;
import me.ranol.effectprefix.api.effects.PrefixEffect;
import me.ranol.effectprefix.events.PrefixDeselectEvent;
import me.ranol.effectprefix.events.PrefixSelectEvent;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;

public class EffAddHealth extends PrefixEffect {
	private static final long serialVersionUID = -8257382118481638877L;
	@Argument(1)
	double addHealth = 0;

	@Override
	public Material getMainIcon() {
		return Material.GOLDEN_APPLE;
	}

	@Override
	public void initialize() {
		addHealth = parse(addHealth);
	}

	@Override
	public String getCommand() {
		return "addhealth";
	}

	@Override
	public String getDescription() {
		return "체력 " + addHealth + (addHealth > 0 ? " 증가" : " 감소");
	}

	@EventHandler
	public void onSelect(PrefixSelectEvent e) {
		if (e.getSelectPrefix().equals(getTarget())) {
			Damageable dam = e.getPlayer();
			dam.setMaxHealth(dam.getMaxHealth() + addHealth);
		}
	}

	@EventHandler
	public void onDeselect(PrefixDeselectEvent e) {
		if (e.getDeselectPrefix().equals(getTarget())) {
			Damageable dam = e.getPlayer();
			dam.setMaxHealth(dam.getMaxHealth() - addHealth);
		}
	}
}
