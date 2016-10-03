package me.ranol.effectprefix.tabcompletor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.command.CommandSender;

public class ExtendedListCompletions extends Completions {
	List<String> completions = new ArrayList<>();
	List<CompletionWorker> workers = new ArrayList<>();

	public ExtendedListCompletions(Completions parent, List<String> completions) {
		this(parent);
		if (completions != null)
			this.completions = completions;
	}

	public ExtendedListCompletions(Completions parent) {
		super(parent);
	}

	public void clearWorkers() {
		workers.clear();
	}

	public int addWorker(CompletionWorker worker) {
		workers.add(worker);
		return workers.size() - 1;
	}

	public void removeWorker(int key) {
		workers.remove(key);
	}

	public void removeWorker(CompletionWorker worker) {
		workers.remove(worker);
	}

	@Override
	public Collection<String> complete(String[] args, int index,
			CommandSender completor) {
		List<String> result = new ArrayList<>();
		workers.forEach((a) -> a.run(completions, completor));
		if (getParents() == null
				|| getParents().complete(args, index - 1, completor).size() > 0) {
			for (String s : completions) {
				if (s.toLowerCase().startsWith(args[index - 1].toLowerCase())) {
					result.add(s);
				}
			}
		}
		return result;
	}

	@Override
	public Completions clone() {
		ExtendedListCompletions compl = new ExtendedListCompletions(
				getParents(), completions);
		compl.workers = new ArrayList<>(workers);
		return compl;
	}

	@FunctionalInterface
	public interface CompletionWorker {
		public void run(List<String> completions, CommandSender completor);
	}
}
