package me.ranol.effectprefix.tabcompletor;

import java.util.List;

public class LinkedListCompletions extends ExtendedListCompletions {

	public LinkedListCompletions(List<String> list) {
		super(null, list);
	}

	public LinkedListCompletions() {
		super(null);
	}

	public LinkedListCompletions link(Completions parent) {
		LinkedListCompletions completions = new LinkedListCompletions(
				super.completions);
		completions.setParents(parent);
		completions.workers = workers;
		return completions;
	}
}
