package me.ranol.effectprefix.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ranol.effectprefix.api.effects.PrefixEffect;
import me.ranol.effectprefix.utils.ItemUtil;

public class Prefix implements Serializable {
	private static final long serialVersionUID = -538246772988031160L;
	private List<PrefixEffect> effects = new ArrayList<>();
	private String prefixName;
	private String prefix;
	private String description = "설명 없음";

	public Prefix(String prefixName) {
		this.prefixName = prefixName;
		this.prefix = prefixName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrefixName() {
		return prefixName;
	}

	public List<PrefixEffect> getEffects() {
		return effects;
	}

	public String getPrefix() {
		return prefix.startsWith("§") ? prefix : "§r" + prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Prefix))
			return false;
		return ((Prefix) obj).prefixName.equals(prefixName);
	}

	@Override
	public String toString() {
		return "[Prefix: name=" + prefixName + ",visible=" + prefix + "]";
	}

	public ItemStack getPrefixBook() {
		List<String> lores = new ArrayList<>();
		lores.add("&6접두사: " + getPrefix());
		lores.add("&6설명: " + getDescription());
		getEffects().forEach((e) -> {
			lores.add("&6|    " + e.getDescription());
		});
		ItemStack stack = ItemUtil
				.createStack(Material.ENCHANTED_BOOK, "&6칭호: ["
						+ getPrefixName() + "]", lores.toArray(new String[0]));
		return stack;
	}

	public static boolean isPrefixBook(ItemStack stack) {
		if (stack == null)
			return false;
		if (!stack.hasItemMeta())
			return false;
		ItemMeta m = stack.getItemMeta();
		return (m.hasDisplayName() && m.hasLore())
				&& (m.getDisplayName().contains("칭호: [")
						&& m.getLore().get(0).contains("접두사: ") && m.getLore()
						.get(1).contains("설명: "));
	}

	public static Prefix loadByStack(ItemStack stack) {
		if (!isPrefixBook(stack))
			return null;
		String prefixName = stack.getItemMeta().getDisplayName();
		prefixName = prefixName.substring(prefixName.indexOf("[") + 1);
		prefixName = prefixName.substring(0, prefixName.indexOf("]"));
		return PrefixManager.getInstance().get(prefixName);
	}
}
