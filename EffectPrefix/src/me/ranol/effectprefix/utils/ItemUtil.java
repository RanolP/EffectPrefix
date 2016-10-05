package me.ranol.effectprefix.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {
	public static ItemStack createStack(Material material, String name,
			String... lores) {
		ItemStack stack = new ItemStack(material);
		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return stack;
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (name)));
		if (lores.length > 0) {
			for (int n = 0; n < lores.length; n++) {
				lores[n] = ChatColor
						.translateAlternateColorCodes('&', lores[n]);
			}
			meta.setLore(Arrays.asList(lores));
		}
		stack.setItemMeta(meta);
		return stack;
	}

	public static int hasItem(Player p, ItemStack item) {
		int founded = 0;
		ItemStack[] stacks = p.getInventory().getContents();
		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i];
			if (stack == null)
				continue;
			if (matchItem(item, stack))
				founded += stack.getAmount();
		}
		return founded;
	}

	public static boolean matchItem(ItemStack a, ItemStack b) {
		return a.getType() == b.getType()
				&& a.getDurability() == b.getDurability()
				&& a.getEnchantments().equals(b.getEnchantments())
				&& a.getItemMeta().equals(b.getItemMeta());
	}

	public static boolean canGive(Player p, ItemStack i, int count) {
		int cg = hasItem(p, i) % 64;
		for (ItemStack stack : p.getInventory().getContents())
			if (stack == null || stack.getType() == Material.AIR)
				cg += 64;
		return cg >= count;
	}

	public static boolean takeItem(Player p, ItemStack item, int cnt) {
		int removed = 0;
		boolean result = false;
		ItemStack[] stacks = p.getInventory().getContents();
		int t = stacks.length;
		for (int match = 0; match < t; match++) {
			ItemStack stack = stacks[match];
			if (stack == null)
				continue;
			if (matchItem(stack, item)) {
				int a = stack.getAmount() - (cnt - removed);
				if (a > 0) {
					stack.setAmount(a);
					removed += cnt - removed;
					break;
				}
				removed += stack.getAmount();
				stack.setAmount(0);
			}
		}
		if (removed == cnt) {
			p.getInventory().setContents(stacks);
			result = true;
		}
		return result;
	}

	public static boolean giveItem(Player p, ItemStack item) {
		if (item.getAmount() > 64) {
			giveItem(p, item, 64);
			System.out.println(item.getAmount() - 64);
			return giveItem(p, item, item.getAmount() - 64);
		}
		if (p.getInventory().firstEmpty() != -1) {
			p.getInventory().addItem(new ItemStack[] { item });
			return false;
		}
		p.getWorld().dropItem(p.getLocation(), item);
		return true;
	}

	public static boolean giveItem(Player p, ItemStack item, int count) {
		ItemStack i = item.clone();
		i.setAmount(count);
		return giveItem(p, i);
	}

	public static void addLore(ItemStack stack, String lore) {
		if (!stack.hasItemMeta())
			return;
		List<String> lores = new ArrayList<>();
		ItemMeta m = stack.getItemMeta();
		if (m.hasLore())
			lores.addAll(m.getLore());
		lores.add(ChatColor.translateAlternateColorCodes('&', lore));
		m.setLore(lores);
		stack.setItemMeta(m);
	}

	public static void clearLore(ItemStack stack) {
		if (!stack.hasItemMeta())
			return;
		List<String> lores = new ArrayList<>();
		ItemMeta m = stack.getItemMeta();
		m.setLore(lores);
		stack.setItemMeta(m);
	}
}