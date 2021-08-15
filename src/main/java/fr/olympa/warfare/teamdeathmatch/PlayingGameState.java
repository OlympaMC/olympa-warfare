package fr.olympa.warfare.teamdeathmatch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.kits.Kits;
import fr.olympa.warfare.weapons.WeaponsListener;

public class PlayingGameState extends GameState {

	private List<Player> living = new ArrayList<>();
	private List<Team> going = new ArrayList<>();
	
	public PlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start(GameState from) {
		super.start(from);
		Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "Début de la partie ! Tuez tous les joueurs adverses jusqu'à ce qu'ils perdent toutes leurs vies !");
		Bukkit.getPluginManager().registerEvents(new WeaponsListener(), tdm.getPlugin());
		living.addAll(Bukkit.getOnlinePlayers());
		going.addAll(Arrays.asList(Team.values()));
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		e.setKickMessage("La partie a déjà commencé.");
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {}
	
	@Override
	public void onQuit(PlayerQuitEvent e) {}
	
	@Override
	protected void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard) {
		scoreboard.addLines(FixedLine.EMPTY_LINE, OlympaPlayerWarfare.LINE_LIVES, FixedLine.EMPTY_LINE, OlympaPlayerWarfare.LINE_KIT);
	}
	
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
		deadOP.lives.decrement();
		Team team = Team.getPlayerTeam(dead);
		if (killer != null) {
			Kits deadKit = deadOP.usedKit.get();
			OlympaPlayerWarfare killerOP = OlympaPlayerWarfare.get(killer);
			Kits killerKit = null;
			if (killerOP != null)
				killerKit = killerOP.usedKit.get();
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
				e.setDeathMessage("§c☠ " + team.getColor() + "§l" + dead.getName() + "§c (" + deadKit.getName() + ") §7" + (afar ? "🏹" : "⚔") + " §4§l" + killer.getName() + "§4 (" + killerKit.getName() + ") §7~ " + deadOP.lives.get() + "§c❤");
				legitKill = true;
			}
			
		}
		if (!legitKill) e.setDeathMessage("§c☠ " + team.getColor() + "§l" + dead.getName() + "§7 est mort. ~ " + deadOP.lives.get() + "§c❤");
		
		e.setDroppedExp(0);
		e.getDrops().clear();
		
		if (deadOP.lives.get() > 0) {
			e.setKeepInventory(true);
			
			Prefix.DEFAULT.sendMessage(dead, "Tu es mort...");
		}else {
			e.setDeathMessage(e.getDeathMessage() + "\n§4✖ " + team.getColor() + "§l" + dead.getName() + " §4est éliminé !");
			Prefix.DEFAULT_BAD.sendMessage(dead, "Tu es éliminé... Tu peux maintenant regarder la fin des combats sans y participer.");
			
			living.remove(dead);
			if (team.getPlayers().stream().noneMatch(living::contains)) {
				going.remove(team);
				e.setDeathMessage(e.getDeathMessage() + "\n§4§lL'" + team.getName() + " est éliminée !");
				if (going.size() <= 1) {
					tdm.setState(tdm -> new EndGameState(tdm, going.get(0)));
				}
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Team team = Team.getPlayerTeam(e.getPlayer());
		e.setRespawnLocation(team.getSpawnpoint());
		OlympaPlayerWarfare player = OlympaPlayerWarfare.get(e.getPlayer());
		if (player.lives.get() <= 0) {
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
		}
	}
	
	@Override
	public void onChat(AsyncPlayerChatEvent e) {
		if (living.contains(e.getPlayer())) {
			super.onChat(e);
		}else {
			e.getRecipients().removeAll(living);
			e.setFormat("§7[SPECTATEURS] " + Team.getPlayerTeam(e.getPlayer()).getColor() + "%s : %s");
		}
	}
	
}
