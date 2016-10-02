package me.ranol.effectprefix.designpatterns;

import java.util.List;

public class LinkedListObserverTarget<T> implements ObserverTarget<List<T>> {
	private static final long serialVersionUID = 7598032287111314876L;
	List<T> list;

	public LinkedListObserverTarget(List<T> list) {
		this.list = list;
	}

	@Override
	public void update(List<T> data) {
		this.list.clear();
		this.list.addAll(data);
	}
}
