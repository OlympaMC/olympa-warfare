package fr.olympa.warfare.teamdeathmatch;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.Kit;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.weapons.WeaponsListener;

public class PlayingGameState extends GameState {

	public PlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start() {
		super.start();
		Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "Début de la partie ! Tuez tous les joueurs adverses jusqu'à ce qu'ils perdent toutes leurs vies !");
		Bukkit.getPluginManager().registerEvents(new WeaponsListener(), tdm.getPlugin());
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {}
	
	@Override
	public void onQuit(PlayerQuitEvent e) {}
	
	@Override
	protected boolean cancelDamage(Player p, EntityDamageEvent e) {
		return false;
	}
	
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
		
		e.setDroppedExp(0);
		e.getDrops().clear();
		e.setKeepInventory(true);
		
		Prefix.DEFAULT.sendMessage(dead, "Tu es mort...");
	}
	
}
