package me.ranol.effectprefix.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class PageUI<T> extends AbstractUI {
	private int start, end;
	private HashMap<UUID, Integer> page = new HashMap<>();
	protected List<T> list = new ArrayList<T>();
	private List<ItemModify> modify = new ArrayList<ItemModify>();
	private int maxPage;

	public T getByIndex(int index, Player p) {
		int i = index + (getPage(p) - 1) * (end - start);
		if (list.size() - 1 < i)
			return null;
		return list.get(i);
	}

	public int addModify(ItemModify modify) {
		this.modify.add(modify);
		return this.modify.size();
	}

	public void removeModify(ItemModify modify) {
		this.modify.remove(modify);
	}

	protected abstract ItemStack getStack(T obj, Player p);

	protected void update() {
		maxPage = list.size() % end - start == 0 ? list.size() / end - start
				: list.size() / end - start + 1;
	}

	protected void calculation(Inventory i, Player p) {
		int index = start;
		for (int j = (getPage(p) - 1) * (end - start); j < list.size() + end; j++) {
			if (index > end)
				break;
			ItemStack temp = getStack(list.size() > j ? list.get(j) : null, p);
			modify.forEach(modify -> modify.modify(p, temp));
			i.setItem(index++, temp);
		}
	}

	public int getStart() {
		return start;
	}

	protected void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	protected void setEnd(int end) {
		this.end = end;
	}

	public int getPage(Player p) {
		return page.containsKey(p.getUniqueId()) ? page.get(p.getUniqueId())
				: 1;
	}

	public void setPage(Player p, int var) {
		if (var > maxPage)
			var = maxPage;
		if (var < 1)
			var = 1;
		page.put(p.getUniqueId(), var);
	}

	public void nextPage(Player p) {
		setPage(p, getPage(p) + 1);
	}

	public void backPage(Player p) {
		setPage(p, getPage(p) - 1);
	}

	@FunctionalInterface
	public interface ItemModify {
		public void modify(Player player, ItemStack stack);
	}

}
