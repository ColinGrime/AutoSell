package com.github.scilldev.commands;

import com.github.scilldev.AutoSell;
import com.github.scilldev.PlayerPreferences;
import org.bukkit.entity.Player;

public class AutoSellCommand extends AutoCommand {

	public AutoSellCommand(AutoSell plugin) {
		super(plugin, "autosell");
	}

	@Override
	public String getPermission() {
		return "autosell.toggle";
	}

	@Override
	public void onCommand(Player player, String[] args) {
		PlayerPreferences preferences = getPlugin().getPlayerPreferences();

		// manual on/off
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on")) {
				preferences.setAutoSell(player, true);
				return;
			} else if (args[0].equalsIgnoreCase("off")) {
				preferences.setAutoSell(player, false);
				return;
			}
		}

		// toggles it on/off
		preferences.setAutoSell(player, !preferences.isAutoSellOn(player));
	}
}
