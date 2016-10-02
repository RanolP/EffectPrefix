package me.ranol.effectprefix.designpatterns;

import java.io.Serializable;

@FunctionalInterface
public interface ObserverTarget<T> extends Serializable {
	public abstract void update(T data);
}
