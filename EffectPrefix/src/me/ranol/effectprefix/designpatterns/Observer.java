package me.ranol.effectprefix.designpatterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Observer<T> implements Serializable {
	private static final long serialVersionUID = 6912996901141427595L;
	protected List<ObserverTarget<T>> targets = new ArrayList<>();

	public <E extends ObserverTarget<T>> void attach(E target) {
		targets.add(target);
	}

	public <E extends ObserverTarget<T>> void detach(E target) {
		targets.remove(target);
	}

	public void updateAll(T data) {
		targets.forEach((a) -> a.update(data));
	}
}
