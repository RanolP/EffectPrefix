package me.ranol.effectprefix.designpatterns;

public interface ObserverTarget<T> {
	public abstract void update(T data);
}
