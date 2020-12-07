package com.travall.game.utils;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;

public class Properties extends OrderedMap<String, Object> {
	
	private static final Properties DUMMY = new DummyProps(); 
	
	public final boolean isDummy;
	
	public Properties() {
		isDummy = false;
	}

	private Properties(boolean bool) {
		super(1, 0.99f);
		isDummy = true;
	}
	
	@SuppressWarnings("unchecked")
	public <V> V got(String key, V defaultValue) {
		return (V) get(key, defaultValue);
	}

	@Override
	public Object put(String key, Object value) {
		if (containsKey(key)) {
			throw new IllegalStateException("Duplicated key: " + key);
		}
		return super.put(key, value);
	}

	public Properties newProps(String key) {
		final Properties props = new Properties();
		put(key, props);
		return props;
	}
	
	public Properties getProps(String key) {
		return (Properties) get(key, DUMMY);
	}
	
	private static class DummyProps extends Properties {
		private static final String MESSAGE = "This is a dummy properties.";
		
		private DummyProps() {
			super(true);
		}
		
		@Override
		public Object put(String key, Object value) {
			throw new UnsupportedOperationException(MESSAGE);
		}
		
		@Override
		public void putAll(ObjectMap<? extends String, ? extends Object> map) {
			throw new UnsupportedOperationException(MESSAGE);
		}
		
		@Override
		public <T extends String> void putAll(OrderedMap<T, ? extends Object> map) {
			throw new UnsupportedOperationException(MESSAGE);
		}
		
		@Override
		public Properties newProps(String key) {
			throw new UnsupportedOperationException(MESSAGE);
		}
		
		@Override
		public Object get(String key) {
			return null;
		}
		
		@Override
		public Object get(String key, Object defaultValue) {
			return defaultValue;
		}
		
		@Override
		public Properties getProps(String key) {
			return this;
		}
		
		@Override
		public boolean containsKey(String key) {
			return false;
		}
		
		@Override
		public boolean containsValue(Object value, boolean identity) {
			return false;
		}
	}
}
