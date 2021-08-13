package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitPlayingGameState extends GameState {
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		e.setKickMessage("La partie a déjà commencé.");
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {}
	
	@Override
	public void onQuit(PlayerQuitEvent e) {}
	
}
