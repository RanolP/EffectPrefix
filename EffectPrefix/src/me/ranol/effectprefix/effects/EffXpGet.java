package me.ranol.effectprefix.effects;

import me.ranol.effectprefix.api.Argument;
import me.ranol.effectprefix.api.effects.PrefixEffect;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class EffXpGet extends PrefixEffect {
	private static final long serialVersionUID = 4994628596228856156L;
	@Argument(1)
	double mul = 0;

	@Override
	public void initialize() {
		mul = parse(mul);
	}

	@Override
	public String getCommand() {
		return "xpget";
	}

	@Override
	public String getDescription() {
		return "경험치의 획득량이 " + (mul > 0 ? mul + "% 증가합니다." : mul + "% 감소합니다.");
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
	public void onXpChanged(PlayerExpChangeEvent e) {
		if (e.getAmount() < 0)
			return;
		if (!isSelected(e.getPlayer()))
			return;
		e.setAmount((int) (e.getAmount() * (mul / 100)));
		e.getPlayer().sendMessage("야");
	}

	@Override
	public Material getMainIcon() {
		return Material.EXP_BOTTLE;
	}
}
