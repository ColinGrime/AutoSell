package com.github.scilldev.data.database.mysql;

import com.github.scilldev.AutoSell;
import com.github.scilldev.data.database.UserData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StandardUserData implements UserData {

	private final String TABLE = "autosell_users";

	private final String GET_UUID = "SELECT * FROM " + TABLE + " WHERE uuid=?;";
	private final String SET_UUID_PREFERENCES = "INSERT INTO " + TABLE + " (uuid, autosell_preference, autopickup_preference) VALUES (?,?,?);";
	private final String UPDATE_UUID_PREFERENCES = "UPDATE " + TABLE + " SET autosell_preference=?, autopickup_preference=? WHERE uuid=?;";
	private final String CHECK_UUID_EXISTS = "SELECT * FROM " + TABLE + " WHERE uuid=?;";

	private final AutoSell plugin;
	private final DataSource source;

	public StandardUserData(AutoSell plugin, DataSource source) {
		this.plugin = plugin;
		this.source = source;
	}

	@Override
	public int loadUsers() {
		int loadedUsers = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (loadUser(player.getUniqueId())) loadedUsers++;
		}
		return loadedUsers;
	}

	@Override
	public boolean loadUser(UUID uuid) {
		// already loaded
		if (plugin.getPlayerPreferences().getAutoSell().containsKey(uuid)) {
			return false;
		}

		// load data if it exists
		if (exists(uuid)) {
			try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(GET_UUID)) {
				stmt.setString(1, uuid.toString());
				ResultSet resultSet = stmt.executeQuery();

				if (resultSet.next()) {
					plugin.getPlayerPreferences().getAutoSell().put(uuid, resultSet.getBoolean("autosell_preference"));
					plugin.getPlayerPreferences().getAutoPickup().put(uuid, resultSet.getBoolean("autopickup_preference"));
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// insert default data
		else {
			try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(SET_UUID_PREFERENCES)) {
				stmt.setString(1, uuid.toString());
				stmt.setBoolean(2, false);
				stmt.setBoolean(3, false);
				stmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public int saveUsers() {
		int savedUsers = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (saveUser(player.getUniqueId())) savedUsers++;
		}
		return savedUsers;
	}

	@Override
	public boolean saveUser(UUID uuid) {
		try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(UPDATE_UUID_PREFERENCES)) {
			stmt.setBoolean(1, plugin.getPlayerPreferences().getAutoSell().get(uuid));
			stmt.setBoolean(2, plugin.getPlayerPreferences().getAutoPickup().get(uuid));
			stmt.setString(3, uuid.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean exists(UUID uuid) {
		try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(CHECK_UUID_EXISTS)) {
			stmt.setString(1, uuid.toString());
			return stmt.executeQuery().next();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
