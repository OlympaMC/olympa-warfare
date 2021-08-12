package fr.olympa.warfare.teamdeathmatch;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.spigotmc.SpigotConfig;

import fr.olympa.warfare.OlympaWarfare;

public class TDM {
	
	private final OlympaWarfare plugin;
	
	private GameState state;
	
	public TDM(OlympaWarfare plugin) {
		this.plugin = plugin;
		setState(WaitingGameState::new);
		SpigotConfig.disablePlayerDataSaving = true;
	}
	
	public OlympaWarfare getPlugin() {
		return plugin;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(Function<TDM, GameState> stateProvider) {
		if (state != null) HandlerList.unregisterAll(state);
		state = stateProvider.apply(this);
		Bukkit.getPluginManager().registerEvents(state, plugin);
	}
	
	public int getMinPlayers() {
		return 6;
	}
	
}
