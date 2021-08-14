package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.olympa.warfare.OlympaWarfare;

public abstract class GameState implements Listener {
	
	protected TDM tdm;
	
	public GameState(TDM tdm) {
		this.tdm = tdm;
	}
	
	public TDM getTDM() {
		return tdm;
	}
	
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, tdm.getPlugin());
		OlympaWarfare.getInstance().sendMessage("Début de la phase %s", getClass().getSimpleName());
	}
	
	public void stop() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public abstract void onJoin(PlayerJoinEvent e);
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public abstract void onQuit(PlayerQuitEvent e);
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e instanceof Player p) {
			e.setCancelled(cancelDamage(p, e));
		}
	}
	
	protected boolean cancelDamage(Player p, EntityDamageEvent e) {
		return true;
	}
	
}
