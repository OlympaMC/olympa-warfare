package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayingGameState extends GameState {
	
	public PlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {}
	
	@Override
	public void onQuit(PlayerQuitEvent e) {}
	
}
