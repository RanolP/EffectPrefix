package me.ranol.effectprefix.gui;

import java.util.ArrayList;
import java.util.List;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.designpatterns.ObserverTarget;
import me.ranol.effectprefix.utils.ItemUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UIPrefixUser extends PageUI<Prefix> implements
		ObserverTarget<List<Prefix>> {
	private static final long serialVersionUID = -7502573757932360890L;

	{
		PrefixManager.getInstance().attach(this);
		setStart(0);
		setEnd(26);
	}

	@Override
	public void effect(InventoryClickEvent e) {
		e.setCancelled(true);
		Inventory i = e.getInventory();
		Player p = (Player) e.getWhoClicked();
		switch (e.getRawSlot()) {
		case 30:
			backPage(p);
			calculation(i, p);
			settingSigns(i, p);
			break;
		case 32:
			nextPage(p);
			calculation(i, p);
			settingSigns(i, p);
			break;
		}
	}

	@Override
	public void visible(Player p) {
		open(p);
		Inventory i = Bukkit.createInventory(null, 9 * 6, "칭호 선택");
		setting(i);
		settingSigns(i, p);
		calculation(i, p);
		p.openInventory(i);
	}

	@Override
	protected void setting(Inventory i) {
		ItemStack bg = ItemUtil.createStack(Material.STAINED_GLASS_PANE, "&f");
		for (int j : new int[] { 27, 28, 29, 33, 34, 35, 45, 46, 47, 48, 49,
				50, 51, 52, 53 }) {
			i.setItem(j, bg);
		}
	}

	protected void settingSigns(Inventory i, Player p) {
		ItemStack nextSign = ItemUtil
				.createStack(Material.SIGN, "&a다음 페이지 &e&l→",
						"&6다음 페이지로 이동합니다.", "&e현재 페이지: " + getPage(p));
		ItemStack backSign = ItemUtil
				.createStack(Material.SIGN, "&e&l← &a이전 페이지",
						"&6이전 페이지로 이동합니다.", "&e현재 페이지: " + getPage(p));
		i.setItem(32, nextSign);
		i.setItem(30, backSign);
	}

	@Override
	protected ItemStack getStack(Prefix obj, Player p) {
		if (obj == null)
			return ItemUtil.createStack(Material.AIR, "");
		List<String> effects = new ArrayList<>();
		effects.add("&6접두사: " + obj.getPrefix());
		effects.add("&6설명: " + obj.getDescription());
		obj.getEffects().forEach((e) -> {
			effects.add("&6|    " + e.getDescription());
		});
		ItemStack stack = ItemUtil
				.createStack(
						PrefixManager.getInstance().isSelected(p, obj) ? Material.ENCHANTED_BOOK
								: Material.BOOK,
						"&6칭호 | " + obj.getPrefixName(), effects
								.toArray(new String[0]));
		return stack;
	}

	@Override
	public void update(List<Prefix> data) {
		super.list.clear();
		super.list.addAll(data);
		update();
	}

}
