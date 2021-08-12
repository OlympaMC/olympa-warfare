package fr.olympa.warfare;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.kits.Kit;
import fr.olympa.warfare.kits.gui.KitListGUI;
import fr.olympa.warfare.xp.XPManagement;

public class PvPKitListener implements Listener {
	
	private static final ItemStack MENU_ITEM = ItemUtils.item(Material.NETHER_STAR, "§bSélecteur de Kit", "§8> §7Clique ici pour ouvrir", "  §7le menu des Kits !");
	
	private DecimalFormat format = new DecimalFormat("0.#");
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player dead = e.getEntity();
		Player killer = dead.getKiller();
		
		boolean legitKill = false;
		
		OlympaPlayerWarfare deadOP = OlympaPlayerWarfare.get(dead);
		if (killer != null) {
			Kit deadKit = deadOP.getUsedKit();
			OlympaPlayerWarfare killerOP = OlympaPlayerWarfare.get(killer);
			Kit killerKit = null;
			if (killerOP != null)
				killerKit = killerOP.getUsedKit();
			if (deadKit != null && killerKit != null) {
				
				double xpGain = 1;
				
				if (killerOP.hasGroup(OlympaGroup.PVPKIT_CHAMPION)) {
					xpGain *= 1.5;
				}else if (killerOP.hasGroup(OlympaGroup.VIP)) {
					xpGain *= 1.25;
				}
				
				Prefix.DEFAULT_GOOD.sendMessage(killer, "§eTu gagnes §6§l%s xp§e !", format.format(xpGain));
				killerOP.setXP(killerOP.getXP() + xpGain);
				killerOP.getKills().increment();
				
				boolean afar = dead.getLastDamageCause().getCause() == DamageCause.PROJECTILE;
				e.setDeathMessage("§c☠ §l" + dead.getName() + "§c (" + deadKit.getId() + ") §7" + (afar ? "🏹" : "⚔") + " §4§l" + killer.getName() + "§4 (" + killerKit.getId() + ") §7~ ks §l" + killerOP.getKillStreak().get());
				legitKill = true;
				
				if (killer.getHealth() < 15) {
					killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10 * 20, (int) (Math.floor(15D - killer.getHealth()) / 5D)));
					killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6 * 20, 1));
				}
			}
			
		}
		if (!legitKill) e.setDeathMessage("§c☠ §l" + dead.getName() + "§c est mort.");
		
		deadOP.setInPvPZone(null);
		
		e.setDroppedExp(0);
		e.getDrops().clear();
		
		Prefix.DEFAULT.sendMessage(dead, "Tu es mort...");
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if (e.getHitBlock() != null && e.getEntity() instanceof AbstractArrow && !(e.getEntity() instanceof Trident)) {
			e.getEntity().remove();
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if (e.getState() == PlayerFishEvent.State.BITE) e.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		OlympaPlayerWarfare player = OlympaPlayerWarfare.get(e.getPlayer());
		e.setFormat(XPManagement.getLevelPrefix(player.getLevel()) + " " + player.getGroupPrefix() + "%s " + player.getGroup().getChatSuffix() + " %s");
	}
	
	@EventHandler
	public void onJoinLocation(PlayerSpawnLocationEvent e) {
		if (!e.getPlayer().hasPlayedBefore()) e.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
		giveMenuItem(e.getPlayer());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPlayedBefore()) {
			if (p.getPersistentDataContainer().has(CustomWorldNBTStorage.PLAYER_KIT, PersistentDataType.STRING)) {
				String kitID = p.getPersistentDataContainer().get(CustomWorldNBTStorage.PLAYER_KIT, PersistentDataType.STRING);
				if (kitID != null) {
					Kit kit = OlympaWarfare.getInstance().kits.getKit(kitID);
					OlympaPlayerWarfare.get(p).setInPvPZone(kit);
					return;
				}
			}
			p.teleport(p.getWorld().getSpawnLocation());
			OlympaWarfare.getInstance().sendMessage("§cLe joueur %s a des données mais pas de kit sauvegardé.", p.getName());
		}
		giveMenuItem(e.getPlayer());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.HAND) {
			if (MENU_ITEM.equals(e.getItem())) {
				new KitListGUI(OlympaPlayerWarfare.get(e.getPlayer())).create(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (MENU_ITEM.equals(e.getCurrentItem())) e.setCancelled(true);
	}
	
	public static void giveMenuItem(Player p) {
		p.getInventory().clear();
		p.getActivePotionEffects().forEach(x -> p.removePotionEffect(x.getType()));
		p.getInventory().setItem(4, MENU_ITEM);
		p.getInventory().setHeldItemSlot(4);
		OlympaPlayerWarfare.get(p).updateXPBar();
	}
	
}
