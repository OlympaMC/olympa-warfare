package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class GameState implements Listener {
	
	protected TDM tdm;
	
	public GameState(TDM tdm) {
		this.tdm = tdm;
	}
	
	public TDM getTDM() {
		return tdm;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public abstract void onJoin(PlayerJoinEvent e);
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public abstract void onQuit(PlayerQuitEvent e);
	
}
