package fr.olympa.warfare.teamdeathmatch.gamestates;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.classes.WarfareClass;
import fr.olympa.warfare.teamdeathmatch.GameState;
import fr.olympa.warfare.teamdeathmatch.TDM;
import fr.olympa.warfare.teamdeathmatch.TDMPlayer;
import fr.olympa.warfare.teamdeathmatch.Team;

public class WaitPlayingGameState extends GameState {
	
	private final WarfareClass defaultKit = WarfareClass.SOLDAT;
	
	private final FixedLine<Scoreboard<OlympaPlayerWarfare>> LINE_TITLE = new FixedLine<>("§8> §7Début de la partie...");
	
	private BukkitTask task;
	private int countdown = 5;
	
	public WaitPlayingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start(GameState from) {
		tdm.setInGame(true);
		for (Team team : Team.values()) {
			team.getPlayers().forEach(x -> {
				x.teleport(team.getSpawnpoints().get(0)); // avant super.start() comme ça le move n'est pas cancel
				TDMPlayer player = tdm.getPlayer(x);
				if (player.usedClass.isEmpty()) {
					player.usedClass.set(defaultKit);
					Prefix.DEFAULT.sendMessage(x, "La classe %s t'as été donnée par défaut.", defaultKit.getName());
				}
				x.getInventory().clear();
				player.usedClass.get().give(x);
				x.sendTitle("§6Warfare", "§7Début dans §c5 secondes", 10, 20, 0);
				x.setGameMode(GameMode.ADVENTURE);
			});
		}
		super.start(from);
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
	
	@Override
	protected void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard) {
		scoreboard.addLines(FixedLine.EMPTY_LINE, LINE_TITLE, TDMPlayer.LINE_CLASS);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		if (Bukkit.getOnlinePlayers().size() == 1) tdm.setState(null);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (SpigotUtils.isSameLocationXZ(e.getFrom(), e.getTo())) return;
		e.setCancelled(true);
	}
	
}
