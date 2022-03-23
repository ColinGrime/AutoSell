package com.github.scilldev.commands;

import com.github.scilldev.AutoSell;
import com.github.scilldev.data.yaml.Messages;
import com.github.scilldev.utils.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public abstract class AutoCommand implements CommandExecutor {

	private final AutoSell plugin;

	public AutoCommand(AutoSell plugin, String name) {
		this.plugin = plugin;
		PluginCommand command = plugin.getCommand(name);

		// disable plugin if a command is invalid
		if (command == null) {
			Logger.severe("Commands have failed to load. Plugin has been disabled.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}

		command.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// has to be a player
		if (!(sender instanceof Player)) {
			Messages.INVALID_SENDER.sendTo(sender);
		}

		// no perms
		else if (!sender.hasPermission(getPermission())) {
			Messages.INVALID_PERMISSION.sendTo(sender);
		}

		// reload command
		else if (args.length != 0 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("autosell.reload")) {
			Messages.RELOADED.sendTo(sender);
			plugin.reload();
		}

		// specific auto sub-command
		else {
			onCommand((Player) sender);
		}

		return true;
	}

	/**
	 * @return permission of the command
	 */
	public abstract String getPermission();

	/**
	 * Runs additional code for each auto command.
	 * @param player any player
	 */
	public abstract void onCommand(Player player);

	public AutoSell getPlugin() {
		return plugin;
	}
}
