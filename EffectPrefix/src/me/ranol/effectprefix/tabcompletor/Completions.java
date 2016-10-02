package me.ranol.effectprefix.tabcompletor;

import java.util.Collection;

import org.bukkit.command.CommandSender;

public abstract class Completions implements Cloneable {
	protected Completions parent;

	public Completions(Completions parent) {
		this.parent = parent;
	}

	public Completions getParents() {
		return parent;
	}

	public void setParents(Completions newParents) {
		this.parent = newParents;
	}

	public abstract Collection<String> complete(String[] args, int index,
			CommandSender completor);
}
