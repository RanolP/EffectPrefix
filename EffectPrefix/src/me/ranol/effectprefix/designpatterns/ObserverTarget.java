package me.ranol.effectprefix.designpatterns;

import java.io.Serializable;

public interface ObserverTarget<T> extends Serializable{
	public abstract void update(T data);
}
