package com.travall.game.utils;

public class Logger 
{
	private final String tag, original;
	
	public Logger (Class<?> clazz) {
		this(clazz.getSimpleName());
	}
	
	public Logger (String tag) {
		original = tag;
		int len = tag.length()+1;
		char[] str = new char[len+2];
		str[0] = '[';
		for (int i = 1; i < len; i++)
			str[i] = tag.charAt(i-1);
		str[len++] = ']';
		str[len++] = ' ';
		this.tag = new String(str);
	}
	
	public void info(Object obj) {
		System.out.println(tag + obj.toString());
	}
	
	public void error(Object obj) {
		System.err.println(tag + obj.toString());
	}
	
	@Override
	public int hashCode() {
		return original.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj instanceof Logger) {
        	return ((Logger)obj).original.equals(original);
        } if (obj instanceof String) {
        	return original.equals(obj);
        }
		return false;
    }
	
	@Override
	public String toString () {
		return original;
	}
}
