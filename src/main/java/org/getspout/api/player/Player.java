package org.getspout.api.player;

import org.getspout.api.entity.Entity;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.protocol.Session;

public interface Player {
	
	/**
	 * Gets the player's name
	 * 
	 * @return the player's name
	 */
	public String getName();
	
	/**
	 * Gets the entity corresponding to the player
	 * 
	 * @return the entity, or null if the player is offline
	 */
	public Entity getEntity();
	
	/**
	 * Gets the player's position.  For offline players, this is where they will appear when they login
	 * 
	 * @return the player's position transform
	 */
	public Transform getTransform();

	/**
	 * Gets the session associated with the Player.
	 * 
	 * @return the session, or null if the player is offline
	 */
	public Session getSession();
	
	/**
	 * Gets if the player is online
	 * 
	 * @return true if online
	 */
	public boolean isOnline();
	
}
