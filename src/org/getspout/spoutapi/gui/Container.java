package org.getspout.spoutapi.gui;

import org.getspout.spoutapi.gui.Widget;

public interface Container extends Widget {

	/**
	 * Adds a single widget to a container
	 * @param child The widget to add
	 * @return Widget
	 */
	public Container addChild(Widget child);

	/**
	 * Adds a list of children to a container.
	 * @param children The widgets to add
	 * @return 
	 */
	public Container addChildren(Widget... children);

	/**
	 * Removes a single widget from this container
	 * @param child The widget to add
	 * @return
	 */
	public Container removeChild(Widget child);
	
	/**
	 * Get a list of widgets inside this container.
	 * @return 
	 */
	public Widget[] getChildren();
	
	public int getOffsetX();
	
	public int getOffsetY();
}