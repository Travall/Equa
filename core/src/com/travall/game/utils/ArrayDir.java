package com.travall.game.utils;

import com.badlogic.gdx.utils.reflect.ArrayReflection;

/** Array direct. For simpler IntMap and faster. */
public class ArrayDir<T> {
	public final T[] objs;
	
	@SuppressWarnings("unchecked")
	public ArrayDir(Class<T> clazz, int size) {
		objs = (T[]) ArrayReflection.newInstance(clazz, size);
	}
	
	public T get(int index) {
		return objs[index];
	}
	
	public ArrayDir<T> put(int index, T obj) {
		objs[index] = obj;
		return this;
	}
}
