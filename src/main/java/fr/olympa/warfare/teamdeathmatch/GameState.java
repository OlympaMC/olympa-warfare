package fr.olympa.warfare.teamdeathmatch;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import fr.olympa.api.spigot.customevents.ScoreboardCreateEvent;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;

public abstract class GameState implements Listener {
	
	private static final Set<Player> TEAMCHAT_HELP = new HashSet<>();
	
	protected TDM tdm;
	
	public GameState(TDM tdm) {
		this.tdm = tdm;
	}
	
	public TDM getTDM() {
		return tdm;
	}
	
	public void start(GameState from) {
		Bukkit.getPluginManager().registerEvents(this, tdm.getPlugin());
		tdm.getPlugin().sendMessage("Début de la phase %s", getClass().getSimpleName());
		if (from != null && updateScoreboard(from)) {
			Bukkit.getOnlinePlayers().forEach(x -> tdm.getPlugin().scoreboards.refresh(OlympaPlayerWarfare.get(x)));
		}
	}
	
	public void stop() {
		HandlerList.unregisterAll(this);
	}
	
	protected boolean updateScoreboard(GameState from) {
		return true;
	}
	
	protected abstract void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard);
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e.getEntity() instanceof Player p) {
			e.setCancelled(cancelDamage(p, e));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		Team team = Team.getPlayerTeam(e.getPlayer());
		boolean global = team == null || e.getMessage().startsWith("!");
		if (global) {
			if (team != null) e.setMessage(e.getMessage().substring(1));
			e.setFormat((team == null ? "§7" : team.getColor()) + "%s " + OlympaPlayerWarfare.get(e.getPlayer()).getGroup().getChatSuffix() + " %s");
		}else {
			if (TEAMCHAT_HELP.add(e.getPlayer())) Prefix.INFO.sendMessage(e.getPlayer(), "Met un ! devant ton message pour écrire à tout le monde ;-)");
			for (Team other : Team.values()) {
				if (other != team) e.getRecipients().removeAll(other.getPlayers());
			}
			e.setFormat("§7[ÉQUIPE] " + team.getColor() + "%s : §7%s");
		}
	}
	
	@EventHandler
	public void onScoreboardCreate(ScoreboardCreateEvent<OlympaPlayerWarfare> e) {
		handleScoreboard(e.getScoreboard());
	}
	
	@EventHandler
	public void onJoinLocation(PlayerSpawnLocationEvent e) {
		e.setSpawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		if (tdm.isInGame()) {
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			e.setKickMessage("La partie a déjà commencé.");
		}
	}
	
	protected boolean cancelDamage(Player p, EntityDamageEvent e) {
		return true;
	}
	
}
