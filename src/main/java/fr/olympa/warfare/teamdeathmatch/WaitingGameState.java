package fr.olympa.warfare.teamdeathmatch;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.utils.Prefix;

public class WaitingGameState extends GameState {
	
	private BukkitTask task;
	
	private int countdown = -1;
	
	public WaitingGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void onJoin(PlayerJoinEvent e) {
		int online = Bukkit.getOnlinePlayers().size();
		updateCountdown(online);
		e.setJoinMessage(e.getJoinMessage() + getOnlineString(online));
		for (Team team : Team.values()) e.getPlayer().getInventory().setItem(team.getSlot(), team.getItem());
	}
	
	@Override
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
			if (oldTeam != null) oldTeam.removePlayer(p);
			chosen.addPlayer(p);
			tdm.teamChanged(p);
			e.setCancelled(true);
		}
	}
	
	public String getOnlineString(int online) {
		return " §e(" + online + "/" + tdm.getMinPlayers() + ")";
	}
	
	public void updateCountdown(int online) {
		int min = tdm.getMinPlayers();
		if (online == min) {
			if (task != null) return;
			countdown = 60;
			task = Bukkit.getScheduler().runTaskTimer(tdm.getPlugin(), () -> {
				if (countdown == 0) {
					tdm.setState(PlayingGameState::new);
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
