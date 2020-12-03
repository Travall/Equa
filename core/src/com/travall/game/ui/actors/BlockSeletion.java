package com.travall.game.ui.actors;

import static com.travall.game.Main.main;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.travall.game.blocks.Block;
import com.travall.game.handles.Assets;
import com.travall.game.items.BlockItem;

public class BlockSeletion extends Actor {
	private static final Color DARK = new Color(0f, 0f, 0f, 0.4f);
	private static final Array<BlockItem> ITEMS = new Array<>();
	
	public int index;
	
	private final InputAdapter input = new InputAdapter() {
		public boolean scrolled(float amountX, float amountY) {
			index = MathUtils.clamp(index+MathUtils.round(amountY), 0, ITEMS.size-1);
			return false;
		}
	};
	
	public BlockSeletion() {
		setSize(310, 60);
	}
	
	public void addInput() {
		main.inputs.addProcessor(input);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		setSize(310, 60);
		batch.setColor(DARK);
		batch.draw(Assets.BLANK, getX() - (getWidth()*0.5f) + 16, getY() - (getHeight()*0.5f) + 16, getWidth(), getHeight());
		batch.draw(Assets.BLANK, getX() - 8, getY() - 12, 48, 58);
		batch.setColor(Color.WHITE);
		for (int i = -3; i < 4; i++) {
			int offset = index + i;
			if (offset >= 0 && offset < ITEMS.size) {
				batch.draw(ITEMS.get(offset).getTexture(), getX()+(i*40), getY(), 32, 32);
			}
		}
	}
	
	public BlockItem getBlockItem() {
		return ITEMS.get(index);
	}
	
	public static void add(Block block) {
		ITEMS.add(new BlockItem(block));
	}
	
	public static void add(Block block, int type) {
		ITEMS.add(new BlockItem(block, type));
	}
}
