package com.github.scilldev;

import com.github.scilldev.commands.AutoPickupCommand;
import com.github.scilldev.commands.AutoSellCommand;
import com.github.scilldev.data.database.DataSourceProvider;
import com.github.scilldev.data.database.Database;
import com.github.scilldev.data.database.mysql.MySqlDatabase;
import com.github.scilldev.data.database.mysql.MySqlProvider;
import com.github.scilldev.data.yaml.Messages;
import com.github.scilldev.data.yaml.Settings;
import com.github.scilldev.listeners.PlayerListeners;
import com.github.scilldev.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class AutoSell extends JavaPlugin {

	// holds the preferences for all players
	private PlayerPreferences playerPreferences;

	// yaml data classes
	private Settings settings;

	// sql data classes
	private DataSourceProvider sourceProvider;
	private Database database;

	// vault's economy
	private Economy econ = null;

	// depends on the status of 3rd party applications
	private boolean savePreferences = true;
	private boolean isAutoSellEnabled = true;

	@Override
	public void onEnable() {
		// loads the player and yaml data
		playerPreferences = new PlayerPreferences();
		loadData();

		try {
			// initialize data provider and test connection
			sourceProvider = new MySqlProvider(settings);
			sourceProvider.testConection();
		} catch (SQLException ex) {
			Logger.severe("Could not establish database connection. Preferences will NOT be saved.");
			savePreferences = false;
		}

		if (savePreferences) {
			// set up the database (build needed tables / perform updates)
			Logger.log("Setting up database...");
			timeAction(() -> database = new MySqlDatabase(this, sourceProvider.getSource()), "Database set up in %s ms");

			// loads user data and starts the timers
			Logger.log("Loading in user data...");
			timeAction(() -> database.getUserData().loadUsers(), "Users loaded in %s ms");
			database.startTimers();
		}

		// registers commands and listeners
		registerCommands();
		registerListeners();

		if (!setupEconomy()) {
			turnAutoSellOff("No Vault dependency found. The autosell feature will NOT work.");
		} else if (getServer().getPluginManager().getPlugin("ShopGUIPlus") == null) {
			turnAutoSellOff("No ShopGuiPlus dependency found. The autosell feature will NOT work.");
		}
	}

	@Override
	public void onDisable() {
		if (savePreferences) {
			database.getUserData().saveUsers();
			sourceProvider.close();
		}
	}

	private void loadData() {
		saveDefaultConfig();
		settings = new Settings(this);
		reload();
	}

	public void reload() {
		reloadConfig();
		settings.reload();
		Messages.init(this);
	}

	private void registerCommands() {
		new AutoSellCommand(this);
		new AutoPickupCommand(this);
	}

	private void registerListeners() {
		new PlayerListeners(this);
	}

	/**
	 * @return true if economy was successfully setup
	 */
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEcon() {
		return econ;
	}

	public PlayerPreferences getPlayerPreferences() {
		return playerPreferences;
	}

	public Settings getSettings() {
		return settings;
	}

	public Database getDatabase() {
		return database;
	}

	public boolean isSavePreferencesEnabled() {
		return savePreferences;
	}

	public boolean isAutoSellEnabled() {
		return isAutoSellEnabled;
	}

	/**
	 * Turns auto sell feature off.
	 * @param warningMessage severe message that will be posted to console
	 */
	private void turnAutoSellOff(String warningMessage) {
		Logger.severe(warningMessage);
		isAutoSellEnabled = false;
	}

	/**
	 * Times a method.
	 * @param action any method you want timed
	 * @param complete message sent on complete
	 */
	public void timeAction(Action action, String complete) {
		long time = System.currentTimeMillis();
		action.run();
		Logger.log(String.format(complete, System.currentTimeMillis() - time));
	}

	@FunctionalInterface
	public interface Action {
		void run();
	}
}
