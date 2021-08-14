package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.kits.Kits;

public class WaitPlayingGameState extends GameState {
	
	private final Kits defaultKit = Kits.SOLDAT;
	
	private BukkitTask task;
	private int countdown = 5;
	
	public WaitPlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start() {
		for (Team team : Team.values()) {
			team.getPlayers().forEach(x -> {
				x.teleport(team.getSpawnpoint());
				OlympaPlayerWarfare player = OlympaPlayerWarfare.get(x);
				if (player.usedKit == null) {
					player.usedKit = defaultKit;
					Prefix.DEFAULT.sendMessage(x, "Le kit %s t'as été donné par défaut.", defaultKit.getName());
				}
				player.usedKit.give(x);
				x.sendTitle("§6Warfare", "§7Début dans §c5 secondes", 10, 20, 0);
			});
		}
		super.start();
		task = Bukkit.getScheduler().runTaskTimer(OlympaWarfare.getInstance(), () -> {
			if (--countdown == 0) {
				tdm.setState(PlayingGameState::new);
			}else Bukkit.getOnlinePlayers().forEach(x -> x.sendTitle("§6Warfare", "§7Début dans §c%d secondes".formatted(countdown), 10, 20, 0));
		}, 20, 20);
	}
	
	@Override
	public void stop() {
		super.stop();
		task.cancel();
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
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (SpigotUtils.isSameLocationXZ(e.getFrom(), e.getTo())) return;
		e.setCancelled(true);
	}
	
}
