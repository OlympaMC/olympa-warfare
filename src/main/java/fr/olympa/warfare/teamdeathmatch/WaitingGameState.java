package fr.olympa.warfare.teamdeathmatch;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.lines.TimerLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;

public class WaitingGameState extends GameState {
	
	private BukkitTask task;
	
	private int countdown = -1;
	
	private final TimerLine<Scoreboard<OlympaPlayerWarfare>> LINE_TITLE;
	
	public WaitingGameState(TDM tdm) {
		super(tdm);
		tdm.setInGame(false);
		
		LINE_TITLE = new TimerLine<>(x -> {
			if (task == null) {
				return "§c> En attente de\n§c  joueurs... §7(" + Bukkit.getOnlinePlayers().size() + "/" + tdm.getMinPlayers() + ")";
			}else {
				return "§8> §7Début dans\n§a  §l" + countdown + "§7§l secondes §7!";
			}
		}, tdm.getPlugin(), 2);
	}
	
	@Override
	protected void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard) {
		scoreboard.addLines(FixedLine.EMPTY_LINE, LINE_TITLE);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		int online = Bukkit.getOnlinePlayers().size();
		updateCountdown(online);
		e.setJoinMessage(e.getJoinMessage() + getOnlineString(online));
		for (Team team : Team.values()) e.getPlayer().getInventory().setItem(team.getSlot(), team.getItem());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		int online = Bukkit.getOnlinePlayers().size() - 1;
		updateCountdown(online);
		e.setQuitMessage(e.getQuitMessage() + getOnlineString(online));
		Team team = Team.getPlayerTeam(e.getPlayer());
		if (team != null) team.removePlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (e.getItem() == null) return;
		Player p = e.getPlayer();
		int slot = p.getInventory().getHeldItemSlot();
		Team chosen = Arrays.stream(Team.values()).filter(x -> x.getSlot() == slot).findAny().orElse(null);
		if (chosen != null) {
			Team oldTeam = Team.getPlayerTeam(p);
			if (oldTeam == chosen) return;
			if (oldTeam != null) oldTeam.removePlayer(p);
			chosen.addPlayer(p);
			tdm.teamChanged(p);
			e.setCancelled(true);
		}
	}
	
	public String getOnlineString(int online) {
		return " §e(" + online + "/" + Bukkit.getMaxPlayers() + ")";
	}
	
	public void updateCountdown(int online) {
		int min = tdm.getMinPlayers();
		if (online == min) {
			if (task != null) return;
			countdown = 60;
			task = Bukkit.getScheduler().runTaskTimer(tdm.getPlugin(), () -> {
				if (countdown == 0) {
					tdm.setState(ChooseClassGameState::new);
					task.cancel();
					task = null;
				}
				boolean broadcast = countdown <= 5 || countdown % 10 == 0;
				if (broadcast) Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "§aLa partie commence dans §l%d§a secondes !", countdown);
				countdown--;
			}, 0L, 20L);
		}else if (online > min) {
			int possible = countdown;
			int diff = online - min;
			if (diff >= 4) {
				possible = 30;
			}else if (diff >= 6) {
				possible = 10;
			}
			if (possible < countdown) countdown = possible;
		}else {
			if (task != null) {
				task.cancel();
				task = null;
				Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "La partie ne peut pas commencer, il y a trop peu de joueurs.");
			}
		}
	}
	
}
