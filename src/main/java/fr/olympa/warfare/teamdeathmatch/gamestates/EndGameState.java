package fr.olympa.warfare.teamdeathmatch.gamestates;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.spigot.lines.BlinkingLine;
import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.utils.Prefix;
import fr.olympa.api.utils.Utils;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.teamdeathmatch.GameState;
import fr.olympa.warfare.teamdeathmatch.TDM;
import fr.olympa.warfare.teamdeathmatch.Team;

public class EndGameState extends GameState {
	
	private final Team winning;
	private final BlinkingLine<Scoreboard<OlympaPlayerWarfare>> LINE_TITLE;
	
	private BukkitTask task;
	private BukkitTask fireworks;
	
	public EndGameState(TDM tdm, Team winning) {
		super(tdm);
		this.winning = winning;
		if (winning == null)
			LINE_TITLE = new BlinkingLine<>((color, x) -> color + "§lPartie nulle !", tdm.getPlugin(), 40, ChatColor.RED, ChatColor.DARK_RED);
		else
			LINE_TITLE = new BlinkingLine<>((color, x) -> color + "Victoire de\n" + color + "l'" + winning.getName() + color + "!", tdm.getPlugin(), 40, ChatColor.GOLD, ChatColor.YELLOW);
	}
	
	@Override
	public void start(GameState from) {
		tdm.setInGame(true);
		for (Team team : Team.values()) {
			team.getPlayers().forEach(x -> {
				x.sendTitle(team == winning ? "§6§lVictoire !" : "§cDéfaite...", "§7Félicitations à tous !", 5, 200, 55);
				x.setGameMode(GameMode.SPECTATOR);
				if (winning == null)
					Prefix.BROADCAST.sendMessage(x, "§cPartie nulle...");
				else
					Prefix.BROADCAST.sendMessage(x, "§aVictoire de l'%s§a !", winning.getName());
			});
		}
		super.start(from);
		task = Bukkit.getScheduler().runTaskLater(tdm.getPlugin(), () -> {
			Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "Fermeture du serveur %s !", OlympaCore.getInstance().getServerName());
			tdm.setState(null);
		}, 250);
		fireworks = Bukkit.getScheduler().runTaskTimer(tdm.getPlugin(), () -> {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			Player p = Utils.getRandomFrom(random, (List<? extends Player>) Bukkit.getOnlinePlayers());
			p.getWorld().spawn(p.getLocation().add(0, 3, 0), Firework.class, firework -> {
				FireworkMeta meta = firework.getFireworkMeta();
				meta.setPower(random.nextInt(5));
				meta.addEffect(FireworkEffect.builder().with(Utils.getRandomFrom(random, Type.values())).withColor(Color.YELLOW).withFade(Color.ORANGE).withTrail().build());
				firework.setFireworkMeta(meta);
				firework.setPersistent(false);
			});
		}, 1, 15);
	}
	
	@Override
	public void stop() {
		super.stop();
		task.cancel();
		fireworks.cancel();
	}
	
	@Override
	protected void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard) {
		scoreboard.addLines(FixedLine.EMPTY_LINE, LINE_TITLE);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		if (Bukkit.getOnlinePlayers().size() == 1) tdm.setState(null); // dernier joueur à quitter
	}
	
	@Override
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		e.setFormat(Team.getPlayerTeam(e.getPlayer()).getColor() + "%s " + OlympaPlayerWarfare.get(e.getPlayer()).getGroup().getChatSuffix() + " %s");
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(tdm.getPlugin().waitRespawnLocation);
		e.getPlayer().setGameMode(GameMode.SPECTATOR);
	}
	
}
