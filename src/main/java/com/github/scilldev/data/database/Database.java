package com.github.scilldev.data.database;

public interface Database {

	/**
	 * Sets up the table to keep track of versions / patches.
	 */
	void setupVersionTable();

	/**
	 * Checks for updates from the version table.
	 * @return true if an update is available
	 */
	boolean checkForUpdates();

	/**
	 * Performs the necessary updates.
	 */
	void performUpdates();

	/**
	 * Initializes the database.
	 */
	void init();

	/**
	 * @return user data stored on the database
	 */
	UserData getUserData();

	/**
	 * Starts the timers necessary for saving data regularly, etc.
	 */
	void startTimers();
}
