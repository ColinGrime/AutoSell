package com.github.scilldev.commands;

import com.github.scilldev.AutoSell;
import com.github.scilldev.data.yaml.Messages;
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
	public void onCommand(Player player) {
		if (getPlugin().getPlayerPreferences().toggleAutoSell(player))
			Messages.TOGGLE_AUTOSELL_ON.sendTo(player);
		else
			Messages.TOGGLE_AUTOSELL_OFF.sendTo(player);
	}
}
