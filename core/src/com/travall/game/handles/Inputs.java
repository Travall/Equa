package com.travall.game.handles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.IntArray;

/** Like <code>Gdx.input</code> but faster and can be override by Scene2D. */
public final class Inputs extends InputAdapter 
{
	public static final Inputs input = new Inputs();
	private Inputs() {}
	
	private static final IntArray keysPressed = new IntArray(false, 16);
	private static final IntArray keysJustPre = new IntArray(false, 16);
	private static final IntArray buttPressed = new IntArray(false, 8);
	private static final IntArray buttJustPre = new IntArray(false, 8);
	
	private static final GridPoint2 
	lastPos = new GridPoint2(), movePos = new GridPoint2(), tmp = new GridPoint2();
	
	public static final GridPoint2 mousePos = new GridPoint2();
	
	@Override
	public boolean keyDown (int keycode) {
		if (!keysPressed.contains(keycode)) {
			keysPressed.add(keycode);
		}
		if (!keysJustPre.contains(keycode)) {
			keysJustPre.add(keycode);
		}
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		keysPressed.removeValue(keycode);
		return false;
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (!buttPressed.contains(button)) {
			buttPressed.add(button);
		}
		if (!buttJustPre.contains(button)) {
			buttJustPre.add(button);
		}
		return false;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		buttPressed.removeValue(button);
		return false;
	}
	
	@Override
	public boolean mouseMoved (int screenX, int screenY) {
		move(screenX, screenY);
		mousePos.set(screenX, screenY);
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		move(screenX, screenY);
		mousePos.set(screenX, screenY);
		return false;
	}
	
	private void move(int screenX, int screenY) {
		movePos.add(lastPos.x - screenX, lastPos.y - screenY);
		lastPos.set(screenX, screenY);
	}
	
	public static GridPoint2 getMouseDelta() {
		tmp.set(movePos);
		Gdx.input.setCursorPosition(0, 0);
		resetMouse();
		return tmp;
	}
	
	/** Resets the mouse's delta. */
	public static void resetMouse() {
		lastPos.set(0, 0);
		movePos.set(0, 0);
	}
	
	/** Must be called when windows has been resized. */
	public static void clear() {
		keysPressed.clear();
		buttPressed.clear();
		reset();
		resetMouse();
	}
	
	/** Must be called on the end of <code>render()</code> */
	public static void reset() {
		keysJustPre.clear();
		buttJustPre.clear();
	}
	
	/** Returns whether the key has just been pressed.
	 *  If pressed, than remove the pressed key (will return false on next call with same key).
	 * @param key The key code as found in {@link Input.Keys}.
	 * @return true if key just pressed, else false. */
	public static boolean isKeyJustPressed(int key) {
		return keysJustPre.contains(key);
	}
	
	/** Returns whether the button has just been pressed.
	 * @param button The button button as found in button
	 * @return true if button pressed, else false. */
	public static boolean isButtonPressed(int button) {
		return buttPressed.contains(button);
	}
	
	/** Returns whether the button has just been pressed.
	 *  If pressed, than remove the pressed button (will return false on next call with same button).
	 * 
	 * @param button The button code as found in {@link Input.Buttons}.
	 * @return true if button just pressed, else false. */
	public static boolean isButtonJustPressed(int button) {
		return buttJustPre.contains(button);
	}
	
	/** Returns whether the key is pressed.
	 * @param key The key code as found in {@link Input.Keys}.
	 * @return true or false. */
	public static boolean isKeyPressed(int key) {
		return keysPressed.contains(key);
	}

	public static boolean justTouched() {
		return buttJustPre.notEmpty();
	}
}
