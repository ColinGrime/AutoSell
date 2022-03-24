package com.github.scilldev.commands;

import com.github.scilldev.AutoSell;
import com.github.scilldev.PlayerPreferences;
import org.bukkit.entity.Player;

public class AutoPickupCommand extends AutoCommand {

	public AutoPickupCommand(AutoSell plugin) {
		super(plugin, "autopickup");
	}

	@Override
	public String getPermission() {
		return "autopickup.toggle";
	}

	@Override
	public void onCommand(Player player, String[] args) {
		PlayerPreferences preferences = getPlugin().getPlayerPreferences();

		// manual on/off
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on")) {
				preferences.setAutoPickup(player, true);
				return;
			} else if (args[0].equalsIgnoreCase("off")) {
				preferences.setAutoPickup(player, false);
				return;
			}
		}

		// toggles it on/off
		preferences.setAutoPickup(player, !preferences.isAutoPickupOn(player));
	}
}
