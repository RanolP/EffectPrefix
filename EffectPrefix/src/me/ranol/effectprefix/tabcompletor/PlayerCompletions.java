package me.ranol.effectprefix.tabcompletor;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PlayerCompletions extends ListCompletions {

	public PlayerCompletions(Completions parent) {
		super(parent);
	}

	@Override
	public Collection<String> complete(String[] args, int index,
			CommandSender completor) {
		super.completions.clear();
		Bukkit.getOnlinePlayers().forEach((action) -> {
			super.completions.add(action.getName());
		});
		return super.complete(args, index, completor);
	}

	@Override
	public Completions clone() {
		return new PlayerCompletions(getParents());
	}
}
