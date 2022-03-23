package com.github.scilldev;

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

	public boolean toggleAutoSell(Player player) {
		autoSell.put(player.getUniqueId(), !isAutoSellOn(player));
		return isAutoSellOn(player);
	}

	public boolean isAutoPickOn(Player player) {
		return autoPickup.getOrDefault(player.getUniqueId(), false);
	}

	public boolean toggleAutoPickup(Player player) {
		autoPickup.put(player.getUniqueId(), !isAutoPickOn(player));
		return isAutoPickOn(player);
	}

	public Map<UUID, Boolean> getAutoSell() {
		return autoSell;
	}

	public Map<UUID, Boolean> getAutoPickup() {
		return autoPickup;
	}
}
