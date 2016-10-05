package me.ranol.effectprefix.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SelectUI extends AbstractUI {
	private List<ItemStack> items = new ArrayList<>();

	public void setStack(ItemStack stack, int index) {
		while (items.size() <= index) {
			addStack(null);
		}
		items.set(index, stack);
	}

	public void addStack(ItemStack stack) {
		items.add(stack);
	}

	public void initialize(Inventory i) {
		for (int j = 0; j < items.size(); j++) {
			i.setItem(j, items.get(j));
		}
	}

	public List<ItemStack> getStacks(Player p) {
		return new ArrayList<>(items);
	}
}
