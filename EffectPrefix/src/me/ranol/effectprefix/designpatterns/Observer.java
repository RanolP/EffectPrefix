package me.ranol.effectprefix.designpatterns;

import java.util.ArrayList;
import java.util.List;

public abstract class Observer<T> {
	protected List<ObserverTarget<T>> targets = new ArrayList<>();

	public void attach(ObserverTarget<T> target) {
		targets.add(target);
	}

	public void detach(ObserverTarget<T> target) {
		targets.remove(target);
	}

	public void updateAll(T data) {
		targets.forEach((a) -> a.update(data));
	}
}
