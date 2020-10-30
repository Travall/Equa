package com.travall.game.blocks.data;

import com.badlogic.gdx.utils.ObjectMap;
import com.travall.game.utils.BlockUtils;

public final class DataManager {
	private final ObjectMap<String, DataComponent> components = new ObjectMap<>();
	
	private int bitSize;
	
	public void addCompoment(DataComponent component) {
		addCompoment(component.getKey(), component);
	}
	
	public void addCompoment(String key, DataComponent component) {
		if (bitSize+component.size >= 16) throw new IllegalStateException("Block data has reached the bit limit!");
		if (components.containsKey(key)) throw new IllegalStateException("Duplicated key - use the different key.");
		component.genData(BlockUtils.DATA_SHIFT+bitSize);
		bitSize += component.size;
		components.put(key, component);
	}

	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	public int getCompomentsSize() {
		return components.size;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DataComponent> T getComponent(String key) {
		return (T)components.get(key);
	}
}
