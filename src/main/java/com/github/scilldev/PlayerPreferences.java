package com.github.scilldev;

import com.github.scilldev.data.yaml.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPreferences {

	private Map<UUID, Boolean> autoSell = new HashMap<>();
	private Map<UUID, Boolean> autoPickup = new HashMap<>();

	public boolean isAutoSellOn(Player player) {
		return autoSell.getOrDefault(player.getUniqueId(), false);
	}

	public void setAutoSell(Player player, boolean toggle) {
		autoSell.put(player.getUniqueId(), toggle);

		if (toggle)
			Messages.TOGGLE_AUTOSELL_ON.sendTo(player);
		else
			Messages.TOGGLE_AUTOSELL_OFF.sendTo(player);
	}

	public boolean isAutoPickupOn(Player player) {
		return autoPickup.getOrDefault(player.getUniqueId(), false);
	}

	public void setAutoPickup(Player player, boolean toggle) {
		autoPickup.put(player.getUniqueId(), toggle);

		if (toggle)
			Messages.TOGGLE_AUTOPICKUP_ON.sendTo(player);
		else
			Messages.TOGGLE_AUTOPICKUP_OFF.sendTo(player);
	}

	public Map<UUID, Boolean> getAutoSell() {
		return autoSell;
	}

	public Map<UUID, Boolean> getAutoPickup() {
		return autoPickup;
	}
}
