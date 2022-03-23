package com.github.scilldev.listeners;

import com.github.scilldev.AutoSell;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
import java.util.Iterator;

public class PlayerListeners implements Listener {

	private final AutoSell plugin;

	public PlayerListeners(AutoSell plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Collection<ItemStack> items = event.getBlock().getDrops(player.getInventory().getItemInMainHand(), player);

		// ignore, nothing interesting is happening...
		if (items.isEmpty() || (!plugin.isAutoSellEnabled() && !plugin.getPlayerPreferences().isAutoPickOn(player))) {
			return;
		}

		// auto sell blocks
		if (plugin.isAutoSellEnabled() && plugin.getPlayerPreferences().isAutoSellOn(player)) {
			double amountSold = 0;
			Iterator<ItemStack> itemIterator = items.iterator();

			// get sell prices of items
			while (itemIterator.hasNext()) {
				ItemStack item = itemIterator.next();
				double sellPrice = ShopGuiPlusApi.getItemStackPriceSell(player, item);

				// item is sellable, remove it from the item collection
				if (sellPrice != -1) {
					amountSold += sellPrice;
					itemIterator.remove();
				}
			}

			// MONEY!!!
			plugin.getEcon().depositPlayer(player, amountSold);
		}

		tryAutoPickup(player, event.getBlock().getLocation(), items);

		// auto give xp
		if (plugin.getSettings().isAutoBlockXpEnabled()) {
			player.giveExp(event.getExpToDrop());
			event.setExpToDrop(0);
		} else if (event.getExpToDrop() > 0) {
			ExperienceOrb orb = player.getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class);
			orb.setExperience(event.getExpToDrop());
		}

		// cancel event
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity killed = event.getEntity();
		Player killer = killed.getKiller();

		if (killed instanceof Player || killer == null || !plugin.getSettings().getEnabledMobs().contains(killed.getType())) {
			return;
		}

		tryAutoPickup(killer, killed.getLocation(), event.getDrops());
		event.getDrops().clear();

		// auto give xp
		if (plugin.getSettings().isAutoMobXpEnabled()) {
			killer.giveExp(event.getDroppedExp());
			event.setDroppedExp(0);
		}
	}

	/**
	 * Attempts to automatically pick up items.
	 * Items that aren't picked up will be dropped.
	 *
	 * @param player any player
	 * @param dropLocation location that items should be dropped
	 * @param items collection of items
	 */
	private void tryAutoPickup(Player player, Location dropLocation, Collection<ItemStack> items) {
		// auto pickup blocks
		if (plugin.getPlayerPreferences().isAutoPickOn(player)) {
			items = player.getInventory().addItem(items.toArray(new ItemStack[0])).values();
		}

		// drop remaining items
		for (ItemStack item : items) {
			player.getWorld().dropItemNaturally(dropLocation, item);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.isSavePreferencesEnabled()) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDatabase().getUserData().saveUser(event.getPlayer().getUniqueId()));
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.isSavePreferencesEnabled()) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDatabase().getUserData().loadUser(event.getPlayer().getUniqueId()));
		}
	}
}
