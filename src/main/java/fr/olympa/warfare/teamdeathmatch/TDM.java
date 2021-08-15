package fr.olympa.warfare.teamdeathmatch;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.spigotmc.SpigotConfig;

import fr.olympa.api.common.player.OlympaPlayer;
import fr.olympa.api.spigot.scoreboard.tab.INametagApi.NametagHandler;
import fr.olympa.api.spigot.scoreboard.tab.Nametag;
import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;

public class TDM {
	
	private final OlympaWarfare plugin;
	
	private GameState state;
	private int minPlayers;
	
	public TDM(OlympaWarfare plugin, int minPlayers) {
		this.plugin = plugin;
		this.minPlayers = minPlayers;
		setState(WaitingGameState::new);
		SpigotConfig.disablePlayerDataSaving = true;
		
		OlympaCore.getInstance().getNameTagApi().addNametagHandler(EventPriority.HIGHEST, new NametagHandler() {
			@Override
			public void updateNameTag(Nametag nametag, OlympaPlayer player, OlympaPlayer to) {
				Team team = Team.getPlayerTeam((Player) player.getPlayer());
				nametag.appendPrefix(team != null ? team.getColor().toString() : "§7");
			}
			
			@Override
			public Integer getPriority(OlympaPlayer player) {
				Team team = Team.getPlayerTeam((Player) player.getPlayer());
				return team == null ? null : team.ordinal();
			}
		});
	}
	
	public OlympaWarfare getPlugin() {
		return plugin;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(Function<TDM, GameState> stateProvider) {
		GameState old = state;
		if (old != null) old.stop();
		if (stateProvider != null) {
			state = stateProvider.apply(this);
			if (state != null) {
				try {
					state.start(old);
					return;
				}catch (Throwable ex) {
					SpigotUtils.broadcastMessage("§4Une erreur est survenue.");
					ex.printStackTrace();
				}
			}
		}
		SpigotUtils.broadcastMessage(Prefix.BROADCAST.formatMessage("Arrêt de ce serveur de jeu..."));
		Bukkit.shutdown();
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public void teamChanged(Player p) {
		OlympaCore.getInstance().getNameTagApi().callNametagUpdate(OlympaPlayerWarfare.get(p));
	}
	
}
