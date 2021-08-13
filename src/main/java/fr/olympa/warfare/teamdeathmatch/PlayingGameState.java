package fr.olympa.warfare.teamdeathmatch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.kits.Kits;

public class PlayingGameState extends GameState {
	
	private Kits defaultKit = Kits.SOLDAT;

	public PlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start() {
		super.start();
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		for (Player p : players) {
			OlympaPlayerWarfare player = OlympaPlayerWarfare.get(p);
			if (player.usedKit == null) {
				player.usedKit = defaultKit;
				Prefix.DEFAULT.sendMessage(p, "Le kit %s t'as été donné par défaut.", defaultKit.getName());
			}
		}
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {}
	
	@Override
	public void onQuit(PlayerQuitEvent e) {}
	
}
