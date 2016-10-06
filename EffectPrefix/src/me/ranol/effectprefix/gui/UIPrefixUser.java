package me.ranol.effectprefix.gui;

import java.util.ArrayList;
import java.util.List;

import me.ranol.effectprefix.EffectPrefix;
import me.ranol.effectprefix.api.Prefix;
import me.ranol.effectprefix.api.PrefixManager;
import me.ranol.effectprefix.designpatterns.ObserverTarget;
import me.ranol.effectprefix.events.PrefixChangeEvent;
import me.ranol.effectprefix.utils.ItemUtil;
import me.ranol.effectprefix.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UIPrefixUser extends PageUI<Prefix> implements
		ObserverTarget<List<Prefix>> {
	private static final ItemStack prevent = ItemUtil.createStack(
			Material.THIN_GLASS, "&c[ &4&l잠긴 &e슬롯 &c]",
			"&6사용이 &4&l불가능&6한 &e슬롯&6입니다!");
	private static final ItemStack canuse = ItemUtil.createStack(
			Material.BOOK_AND_QUILL, "&c[ &a&l장착 가능 &e슬롯 &c]",
			"&6사용이 &a&l가능&6한 &e슬롯&6입니다!");
	private static final int selectableSlots = 36;
	private static final int size = 9;
	private static final long serialVersionUID = -7502573757932360890L;

	{
		PrefixManager.getInstance().attach(this);
		setStart(0);
		setEnd(26);
		addModify((player, stack) -> {
			if (stack == null || !stack.hasItemMeta()
					|| !stack.getItemMeta().hasDisplayName())
				return;
			Prefix prefix = PrefixManager.getInstance().get(
					stack.getItemMeta().getDisplayName().substring(7));
			if (!PrefixManager.getInstance().hasPrefix(player, prefix)) {
				stack.setType(Material.BEDROCK);
				ItemUtil.clearLore(stack);
				ItemUtil.addLore(stack, "&c" + Util.X_MARK + " 사용할 수 없습니다!");
			}
		});
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
		if (e.getRawSlot() >= getStart() && e.getRawSlot() <= getEnd()) {
			if (e.getCurrentItem().getType() == Material.BEDROCK
					|| e.getCursor().getType() == Material.BEDROCK) {
				return;
			}
			Prefix prefix = getByIndex(e.getRawSlot(), p);
			if (prefix == null) {
				return;
			}
			if (e.getClick().isShiftClick()) {
				UIPrefixBookCreation ui = AbstractUI
						.getInstance(UIPrefixBookCreation.class);
				ui.setPrefix(prefix);
				Bukkit.getScheduler().scheduleSyncDelayedTask(
						EffectPrefix.getInstance(), () -> {
							safeClose(p);
							ui.visible(p);
						}, 1L);
				return;
			}
			if (PrefixManager.getInstance().isSelected(p, prefix)) {
				PrefixManager.getInstance().deselect(p, prefix);
				Util.playSound(p, Sound.BLOCK_DISPENSER_FAIL);
			} else {
				if (PrefixManager.getInstance().select(p, prefix)) {
					Util.playSound(p, Sound.ITEM_ARMOR_EQUIP_DIAMOND);
				} else {
					Util.playSound(p, Sound.BLOCK_ANVIL_LAND);
					Util.sendWarning(p, "장착 중인 칭호가 너무 많습니다!");
					return;
				}
			}
			calculation(i, p);
			prevents(i);
			selects(p, i);
			selected(p, i);
		} else if (e.getRawSlot() >= selectableSlots
				&& e.getRawSlot() <= selectableSlots + size) {
			List<Prefix> selected = PrefixManager.getInstance()
					.getSelectedPrefix(p);
			int s = e.getRawSlot() - 36;
			int cs = selected.size();
			if (cs >= size)
				cs = size;
			if (e.getCurrentItem().getType() == Material.BOOK_AND_QUILL
					|| e.getCursor().getType() == Material.BOOK_AND_QUILL)
				return;
			if (s <= selected.size()) {
				if (e.getClick().isLeftClick()) {
					if (selectableSlots == e.getRawSlot())
						return;
					PrefixManager.getInstance().deselect(p, selected.get(s));
					PrefixManager.getInstance().select(p, selected.get(s),
							s - 1);
				} else if (e.getClick().isRightClick()) {
					if (selectableSlots + cs == e.getRawSlot())
						return;
					if (selected.size() == 1)
						return;
					PrefixManager.getInstance().deselect(p, selected.get(s));
					PrefixManager.getInstance().select(p, selected.get(s),
							s + 1);
				}
			}
		}
	}

	@Override
	public void visible(Player p) {
		open(p);
		Inventory i = Bukkit.createInventory(null, 9 * 6, "칭호 선택");
		setting(i);
		prevents(i);
		selects(p, i);
		selected(p, i);
		settingSigns(i, p);
		calculation(i, p);
		p.openInventory(i);
	}

	protected void prevents(Inventory i) {
		for (int j = selectableSlots; j < selectableSlots + size; j++) {
			i.setItem(j, prevent);
		}
	}

	protected void selects(Player p, Inventory i) {
		int count = PrefixManager.getInstance().getCanSelectPrefixCount(p);
		for (int j = selectableSlots; j < selectableSlots + size; j++) {
			if (count <= 0)
				break;
			i.setItem(j, canuse);
			count--;
		}
	}

	protected void selected(Player p, Inventory i) {
		List<Prefix> list = PrefixManager.getInstance().getSelectedPrefix(p);
		int count = 0;
		for (int j = selectableSlots; j < selectableSlots + size; j++) {
			if (count >= list.size())
				break;
			ItemStack temp = getStack(list.get(count), p);
			if (count > 0)
				ItemUtil.addLore(temp, "&e◀ 좌클릭으로 순서를 옮깁니다.");
			if (count < list.size() - 1)
				ItemUtil.addLore(temp, "&e우클릭으로 순서를 옮깁니다. ▶");
			i.setItem(j, temp);
			count++;
		}
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
		ItemStack stack = prefix(obj, p);
		ItemUtil.addLore(stack, "&e쉬프트 클릭으로 아이템화합니다.");
		return stack;
	}

	private ItemStack prefix(Prefix obj, Player p) {
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPrefixGive(PrefixChangeEvent e) {
		switch (e.getType()) {
		case GIVED:
		case SELECT:
		case DESELECT:
			if (!e.isCancelled() && isJoined(e.getPlayer()))
				Bukkit.getScheduler().scheduleSyncDelayedTask(
						EffectPrefix.getInstance(),
						() -> {
							Player p = e.getPlayer();
							Inventory i = e.getPlayer().getOpenInventory()
									.getTopInventory();
							calculation(i, p);
							selects(p, i);
							selected(p, i);
						});
			break;
		default:
			break;
		}
	}
}
