package me.ranol.effectprefix.tabcompletor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.command.CommandSender;

public class StringCompletions extends Completions {
	private String complete;

	public StringCompletions(String comp, Completions parent) {
		super(parent);
		this.complete = comp;
	}

	public StringCompletions(String comp) {
		this(comp, null);
	}

	public String getArgument() {
		return complete;
	}

	public boolean completeFully(String[] args, int argument,
			CommandSender completor) {
		if (complete.equalsIgnoreCase(args[argument - 1])) {
			if (getParents() == null)
				return true;
			if (getParents().complete(args, argument - 1, completor).size() > 0)
				return false;
			return true;
		}
		return false;
	}

	public Collection<String> complete(String[] args, int argument,
			CommandSender completor) {
		if (complete.toLowerCase().startsWith(args[argument - 1].toLowerCase())) {
			if (getParents() == null)
				return new ArrayList<>(Arrays.asList(complete));
			if (getParents().complete(args, argument - 1, completor).size() > 0)
				return Collections.emptyList();
			return new ArrayList<>(Arrays.asList(complete));
		}
		return Collections.emptyList();
	}

	@Override
	public Completions clone() {
		return new StringCompletions(complete, getParents());
	}
}
