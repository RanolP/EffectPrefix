package me.ranol.effectprefix.gui;

import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.utils.ItemUtil;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UIPrefixBookCreation extends SelectUI {
	private Prefix pref;

	public void setPrefix(Prefix pref) {
		this.pref = pref;
	}

	@Override
	public void effect(InventoryClickEvent e) {
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		switch (e.getRawSlot()) {
		case 0:
			if (PrefixManager.getInstance().takePrefix(p, pref)) {
				ItemUtil.giveItem(p, pref.getPrefixBook());
				Util.sendMessage(p, "칭호 " + pref.getPrefixName()
						+ "을(를) 칭호 책으로 변경했습니다.");
				safeClose(p);
			}
		case 8:
			safeClose(p);
			AbstractUI.getInstance(UIPrefixUser.class).visible(p);
		}
	}

	@Override
	public void visible(Player p) {
		open(p);
		Inventory i = Bukkit.createInventory(null, 9, "칭호 책 생성");
		ItemStack create = ItemUtil.createStack(Material.BOOK_AND_QUILL,
				"&7[ &e&l칭호 책 생성 &7]", "&6대상: " + pref.getPrefixName());
		ItemStack cancel = ItemUtil.createStack(Material.ANVIL,
				"&7[ &c&l취소 &7]", "&6대상: " + pref.getPrefixName());
		setStack(create, 0);
		setStack(cancel, 8);
		initialize(i);
		p.openInventory(i);
	}

	@Override
	protected void setting(Inventory i) {
	}

}
