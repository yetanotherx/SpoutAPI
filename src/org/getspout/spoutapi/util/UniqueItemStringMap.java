package org.getspout.spoutapi.util;

/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.getspout.spoutapi.io.FlatFileStore;

public class UniqueItemStringMap {

	private static final ConcurrentHashMap<Integer,String> reverse = new ConcurrentHashMap<Integer,String>();
	private static final ConcurrentHashMap<Integer,String> reverseStable = new ConcurrentHashMap<Integer,String>();
	private static final ConcurrentHashMap<String,Integer> forward = new ConcurrentHashMap<String,Integer>();

	private static final AtomicInteger idCounter = new AtomicInteger(1024);

	
	private static FlatFileStore config;

	public static boolean setConfigFile(FlatFileStore config) {
		UniqueItemStringMap.config = config;
		
		if (!config.load()) {
			return false;
		}
		
		Collection<String> keys = config.getKeys();

		for (String key : keys) {
			Integer id = getIdFromFile(key);
			if (id != null) {
				forward.put(key, id);
				reverse.put(id, key);
				reverseStable.put(id, key);
			}
		}
		
		return true;
	}

	private static Integer getIdFromFile(String key) {

		synchronized(config) {
			if (config.get(key) == null) {
				return null;
			} else {
				int id = config.get(key, -1);
				if (id == -1) {
					config.remove(key);
					return null;
				} else {
					return id;
				}
			}
		}

	}

	private static void setIdInFile(String key, int id) {

		synchronized(config) {
			config.set(key, id);
			config.save();
		}

	}
	
	public static Set<Integer> getIds() {
		return reverse.keySet();
	}

	/**
	 * Associates a unique id for each string
	 * 
	 * These associations persist over reloads and server restarts
	 * 
	 * @param string the string to be associated
	 * @return the id associated with the string.
	 */

	public static int getId(String string) {
		
		Integer id = null;

		boolean success = false;
		int testId = idCounter.incrementAndGet() & 0x0FFFF;

		while (!success || id == null) {

			id = forward.get(string);
			if (id != null) {
				return id;
			}

			if (reverse.containsKey(testId)) { // id already in use
				testId = idCounter.incrementAndGet() & 0x0FFFF;
				if (testId == 65535 || testId < 1024) {
					throw new RuntimeException("[Spout] Out of custom item ids");
				}
				continue;
			}

			String oldString = reverse.putIfAbsent(testId, string);

			if (oldString == null) { // reverse link success
				Integer oldId = forward.putIfAbsent(string, testId);
				if (oldId != null) { // forward link failed
					reverse.remove(testId, string); // remove reverse link
					continue;
				}
				id = testId;
			} else { // reverse link failed
				continue;
			}
			
			reverseStable.put(testId, string);

			setIdInFile(string, id);

			success = true;

		}

		return id;

	}

	/**
	 * Returns the id associated with a string
	 * 
	 * These associations persist over reloads and server restarts
	 * 
	 * Note: . characters are replaced with * characters
	 * 
	 * @param id the id
	 * @return the string associated with the id, or null if no string is associated
	 */

	public static String getString(int id) {
		return reverseStable.get(id);
	}

}