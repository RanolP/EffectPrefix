package me.ranol.effectprefix.tabcompletor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class DefaultCommandExecutor implements CommandExecutor,
		TabCompleter {
	HashMap<Integer, List<Completions>> completions = new HashMap<>();

	public void addCompletion(Completions complete, int index) {
		if (!completions.containsKey(index))
			completions.put(index, new ArrayList<Completions>());
		completions.get(index).add(complete);
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command c, String l,
			String[] a) {
		List<String> results = new ArrayList<String>();
		int index = a.length == 0 ? 1 : a.length;
		if (completions.containsKey(index)) {
			for (Completions comp : completions.get(index)) {
				results.addAll(comp.complete(a, index, s));
			}
		}
		return results;
	}
}
