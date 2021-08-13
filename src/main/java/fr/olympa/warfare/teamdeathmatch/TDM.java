package fr.olympa.warfare.teamdeathmatch;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.spigotmc.SpigotConfig;

import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;

public class TDM {
	
	private final OlympaWarfare plugin;
	
	private GameState state;
	
	public TDM(OlympaWarfare plugin) {
		this.plugin = plugin;
		setState(WaitingGameState::new);
		SpigotConfig.disablePlayerDataSaving = true;
		
		OlympaCore.getInstance().getNameTagApi().addNametagHandler(EventPriority.HIGH, (nametag, player, to) -> {
			Team team = Team.getPlayerTeam((Player) player.getPlayer());
			if (team != null) nametag.appendPrefix(team.getColor().toString());
		});
	}
	
	public OlympaWarfare getPlugin() {
		return plugin;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(Function<TDM, GameState> stateProvider) {
		if (state != null) state.stop();
		state = stateProvider.apply(this);
		state.start();
	}
	
	public int getMinPlayers() {
		return 6;
	}
	
	public void teamChanged(Player p) {
		OlympaCore.getInstance().getNameTagApi().callNametagUpdate(OlympaPlayerWarfare.get(p));
	}
	
}
