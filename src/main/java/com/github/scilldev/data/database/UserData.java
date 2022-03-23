package com.github.scilldev.data.database;

import java.util.UUID;

public interface UserData {

	/**
	 * Loads user data.
	 * @return amount of users loaded
	 */
	int loadUsers();

	/**
	 * Loads a single user's data.
	 * @param uuid uuid of player
	 * @return true if player data was loaded for the first time
	 */
	boolean loadUser(UUID uuid);

	/**
	 * Saves user data.
	 * @return amount of users saved
	 */
	int saveUsers();

	/**
	 * Saves a single user's data.
	 * @param uuid uuid of player
	 * @return true if player data was saved successfully
	 */
	boolean saveUser(UUID uuid);

	/**
	 * @param uuid uuid of player
	 * @return true if player's uuid exists within the database
	 */
	boolean exists(UUID uuid);
}
