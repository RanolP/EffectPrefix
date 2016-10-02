package me.ranol.effectprefix.tabcompletor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ListCompletions extends Completions {
	List<String> completions = new ArrayList<>();

	public ListCompletions(Completions parent, List<String> completions) {
		this(parent);
		if (completions != null)
			this.completions = completions;
	}

	public ListCompletions(Completions parent) {
		super(parent);
	}

	@Override
	public Collection<String> complete(String[] args, int index,
			CommandSender completor) {
		List<String> result = new ArrayList<>();
		if (getParents() == null
				|| getParents().complete(args, index - 1, completor).size() > 0) {
			for (String s : completions) {
				if (s.startsWith(args[index - 1])) {
					result.add(s);
				}
			}
		}
		return result;
	}

	@Override
	public Completions clone() {
		return new ListCompletions(getParents(), new ArrayList<>(completions));
	}
}
