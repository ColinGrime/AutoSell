package com.github.scilldev.data.yaml;

import com.github.scilldev.utils.Logger;
import com.github.scilldev.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Messages {

	// toggle messages
	TOGGLE_AUTOSELL_ON("toggle.autosell-on", "&aAuto Sell has been toggled on!"),
	TOGGLE_AUTOSELL_OFF("toggle.autosell-off", "&cAuto Sell has been toggled off."),
	TOGGLE_AUTOPICKUP_ON("toggle.autopickup-on", "&aAuto Pickup has been toggled on!"),
	TOGGLE_AUTOPICKUP_OFF("toggle.autopickup-off", "&cAuto Pickup has been toggled off."),

	// invalid messages
	INVALID_PERMISSION("invalid.permission", "&cYou do not have permission to perform this command."),
	INVALID_SENDER("invalid.sender", "&cYou must be a player to perform this command."),

	// admin messages
	RELOADED("admin.reloaded", "&aAutoSell has been reloaded!"),
	;

	private static FileConfiguration config;
	private final String path;
	private List<String> messages;

	Messages(String path, String...messages) {
		this.path = "messages." + path;
		this.messages = Arrays.asList(messages);
	}

	public static void init(JavaPlugin plugin) {
		config = plugin.getConfig();
		Arrays.stream(Messages.values()).forEach(Messages::update);
	}

	private void update() {
		if (!config.getStringList(path).isEmpty()) {
			messages = Utils.color(config.getStringList(path));
		} else if (config.getString(path) != null) {
			messages = Collections.singletonList(Utils.color(config.getString(path)));
		} else {
			Logger.log("Config path \"" + path + "\" has failed to load (using default value).");
			messages = Utils.color(messages);
		}
	}

	public void sendTo(CommandSender sender) {
		if (messages.isEmpty()) {
			return;
		}

		messages.forEach(sender::sendMessage);
	}
}
