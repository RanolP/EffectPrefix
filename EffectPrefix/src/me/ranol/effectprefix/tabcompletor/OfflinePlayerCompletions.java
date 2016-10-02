package me.ranol.effectprefix.tabcompletor;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class OfflinePlayerCompletions extends ListCompletions {

	public OfflinePlayerCompletions(Completions parent) {
		super(parent);
	}

	@Override
	public Collection<String> complete(String[] args, int index,
			CommandSender completor) {
		super.completions.clear();
		Arrays.asList(Bukkit.getOfflinePlayers()).forEach((action) -> {
			super.completions.add(action.getName());
		});
		return super.complete(args, index, completor);
	}

	@Override
	public Completions clone() {
		return new OfflinePlayerCompletions(getParents());
	}
}
