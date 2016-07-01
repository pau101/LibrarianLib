package com.teamwizardry.librarianlib.api.gui;

import com.teamwizardry.librarianlib.api.util.math.Vec2;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.Set;

/**
 * A component of a gui, such as a button, image, piece of text, list, etc.
 * 
 * @param <T> The class of this component. Used for setup()
 */
public abstract class GuiComponent<T extends GuiComponent<?>> implements IGuiDrawable {
	
	public int zIndex = 0;
	protected Vec2 pos, size;
	protected GuiComponent<?> parent;
	
	public GuiComponent(int posX, int posY) {
		this(posX, posY, 0, 0);
	}
	
	public GuiComponent(int posX, int posY, int width, int height) {
		this.pos = new Vec2(posX, posY);
		this.size = new Vec2(width, height);
	}
	
	/**
	 * Setup the component without making temporary variables
	 */
	@SuppressWarnings("unchecked")
	public T setup(IComponentSetup<T> setup) {
		setup.setup((T)this);
		return (T)this;
	}
	
	/**
	 * Get the position of the component relative to it's parent
	 */
	public Vec2 getPos() {
		return pos;
	}

	/**
	 * Set the position of the component relative to it's parent
	 */
	public void setPos(Vec2 pos) {
		this.pos = pos;
	}

	/**
	 * Get the size of the component
	 */
	public Vec2 getSize() {
		return size;
	}

	/**
	 * Set the size of the component
	 */
	public void setSize(Vec2 size) {
		this.size = size;
	}
	
	/**
	 * Set the parent component
	 */
	public void setParent(GuiComponent<?> parent) {
		this.parent = parent;
	}
	
	/**
	 * Get the parent component
	 */
	public GuiComponent<?> getParent() {
		return this.parent;
	}
	
	/**
	 * Transforms the position passed to be relative to this component's position.
	 */
	public Vec2 relativePos(Vec2 pos) {
		return pos.sub(this.pos);
	}
	
	/**
	 * Transforms the position passed to be relative to the root component's position.
	 */
	public Vec2 rootPos(Vec2 pos) {
		if(parent == null)
			return pos.add(this.pos);
		else
			return parent.rootPos(pos.add(this.pos));
	}
	
	/**
	 * Test if the mouse is over this component. mousePos is relative to the position of the element.
	 */
	public boolean isMouseOver(Vec2 mousePos) {
		return mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y;
	}
	
	/**
	 * Called when the mouse is pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is released. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDrag(Vec2 mousePos, EnumMouseButton button) {}
	
	/**
	 * Called when the mouse wheel is moved.
	 * @param mousePos
	 * @param button
	 */
	public void mouseWheel(Vec2 mousePos, int direction) {}
	
	/**
	 * Called when a key is pressed in the parent component.
	 * @param key The actual character that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyPressed(char key, int keyCode) {}
	
	/**
	 * Called when a key is released in the parent component.
	 * @param key The actual key that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyReleased(char key, int keyCode) {}
	
	@FunctionalInterface
	public static interface IComponentSetup<T extends GuiComponent> {
		public void setup(T component);
	}
}
