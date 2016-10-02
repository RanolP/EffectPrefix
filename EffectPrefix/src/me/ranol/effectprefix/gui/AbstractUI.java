package me.ranol.effectprefix.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.ranol.effectprefix.EffectPrefix;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class AbstractUI implements Listener {
	private boolean safeClose = false;
	private List<String> openers = new ArrayList<>();
	private static HashMap<Class<?>, AbstractUI> instanceMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T extends AbstractUI> T getInstance(Class<T> clazz) {
		if (!instanceMap.containsKey(clazz)) {
			try {
				instanceMap.put(clazz, clazz.newInstance());
			} catch (Exception e) {
			}
		}
		return (T) instanceMap.get(clazz);
	}

	public void dispose() {
		openers.clear();
		openers = null;
	}

	public static void disposeAll() {
		for (AbstractUI ui : instanceMap.values())
			ui.dispose();
		instanceMap.clear();
		instanceMap = null;
	}

	protected void setSafeClose(boolean newVal) {
		safeClose = newVal;
	}

	public boolean isSafeClose() {
		return safeClose;
	}

	public AbstractUI() {
		initialize();
	}

	private final void initialize() {
		EffectPrefix.getInstance().registerEvents(this);
	}

	/**
	 * @param p
	 *            - 인벤토리를 연 플레이어입니다.
	 */
	protected final void open(Player p) {
		if (!isJoined(p))
			openers.add(p.getName());
	}

	/**
	 * @param p
	 *            - 인벤토리를 닫은 플레이어입니다.
	 */
	protected final void close(Player p) {
		if (isJoined(p))
			openers.remove(p.getName());
	}

	/**
	 * @param p
	 *            - 인벤토리를 연 상태인지 확인할 플레이어입니다.
	 * @return 플레이어가 이 인벤토리를 열었는지 반환합니다.
	 */
	public final boolean isJoined(Player p) {
		return openers.contains(p.getName());
	}

	public final void safeClose(Player p) {
		close(p);
		p.closeInventory();
	}

	@EventHandler
	public final void onInventoryClose(final InventoryCloseEvent e) {
		if (safeClose && isJoined((Player) e.getPlayer())) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(
					EffectPrefix.getInstance(), () -> {
						e.getPlayer().openInventory(e.getInventory());
					}, 1L);
			return;
		}
		close((Player) e.getPlayer());
	}

	@EventHandler
	public final void onInventoryClick(InventoryClickEvent e) {
		if (isJoined((Player) e.getWhoClicked()))
			effect(e);
	}

	public abstract void effect(InventoryClickEvent e);

	/**
	 * @param p
	 *            - 인벤토리를 열 플레이어입니다.
	 */
	public abstract void visible(Player p);

	protected abstract void setting(Inventory i);
}
