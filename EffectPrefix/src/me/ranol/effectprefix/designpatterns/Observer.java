package me.ranol.effectprefix.designpatterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Observer<T> implements Serializable {
	private static final long serialVersionUID = 6912996901141427595L;
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
