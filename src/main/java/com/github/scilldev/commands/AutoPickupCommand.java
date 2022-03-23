package com.github.scilldev.commands;

import com.github.scilldev.AutoSell;
import com.github.scilldev.data.yaml.Messages;
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
	public void onCommand(Player player) {
		if (getPlugin().getPlayerPreferences().toggleAutoPickup(player))
			Messages.TOGGLE_AUTOPICKUP_ON.sendTo(player);
		else
			Messages.TOGGLE_AUTOPICKUP_OFF.sendTo(player);
	}
}
