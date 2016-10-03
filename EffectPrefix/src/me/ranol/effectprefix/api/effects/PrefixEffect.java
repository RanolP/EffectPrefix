package me.ranol.effectprefix.api.effects;

import java.io.Serializable;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.effects.EffAddHealth;
import me.ranol.effectprefix.effects.EffHoloVisible;
import me.ranol.effectprefix.effects.EffXpGet;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public abstract class PrefixEffect implements Listener, Serializable {
	private static final long serialVersionUID = -741790439302165315L;
	private Prefix target;

	public static void registeration() {
		EffectManager.register(EffXpGet.class);
		EffectManager.register(EffAddHealth.class);
		EffectManager.register(EffHoloVisible.class);
	}

	public abstract Material getMainIcon();

	/**
	 * @param name
	 *            - Effect의 명칭입니다.
	 * @param arguments
	 *            - Effect가 받는 인수들입니다.
	 * @return - 다음 작업을 수행한 PrefixEffect를 반환합니다.
	 */
	public static PrefixEffect createEffect(String name, String arguments) {
		return EffectManager.createEffect(name, arguments);
	}

	public void setTarget(Prefix target) {
		this.target = target;
	}

	public Prefix getTarget() {
		return target;
	}

	public boolean isSelected(OfflinePlayer player) {
		return PrefixManager.getInstance().isSelected(player, target);
	}

	public PrefixEffect() {
		EffectPrefix.getInstance().registerEvents(this);
	}

	public abstract void initialize();

	protected static float parse(float f) {
		return Float.parseFloat(String.format("%G", f));
	}

	protected static double parse(double d) {
		return Double.parseDouble(String.format("%G", d));
	}

	/**
	 * @return command without arguments
	 */
	public abstract String getCommand();

	/**
	 * @param vars
	 *            - replace arguments.
	 * @return replaced description
	 */
	public abstract String getDescription();

	@Override
	public String toString() {
		return getCommand() + "(" + getClass().getSimpleName() + ")";
	}
}
