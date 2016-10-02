package me.ranol.effectprefix.designpatterns;

import java.util.ArrayList;
import java.util.List;

public class ExtendedListObserverTarget<T, E> implements
		ObserverTarget<List<E>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3116654699351481935L;
	List<T> list;
	List<ListWorker<T, E>> workers = new ArrayList<>();

	public ExtendedListObserverTarget(List<T> list) {
		this.list = list;
	}

	@SuppressWarnings("unchecked")
	public void addWorker(ListWorker<? extends T, ? extends E> worker) {
		workers.add((ListWorker<T, E>) worker);
	}

	@Override
	public void update(List<E> data) {
		this.list.clear();
		workers.forEach((worker) -> this.list.addAll(worker.work(data)));
	}

	@FunctionalInterface
	public interface ListWorker<T, E> {
		public List<T> work(List<E> data);
	}
}
