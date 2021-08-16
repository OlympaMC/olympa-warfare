package fr.olympa.warfare.teamdeathmatch.gamestates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.classes.ClassListGUI;
import fr.olympa.warfare.teamdeathmatch.GameState;
import fr.olympa.warfare.teamdeathmatch.TDM;
import fr.olympa.warfare.teamdeathmatch.Team;

public class ChooseClassGameState extends GameState {
	
	private final int waitSeconds = 15;
	
	private final FixedLine<Scoreboard<OlympaPlayerWarfare>> LINE_TITLE = new FixedLine<>("§8> §7La partie va commencer.\n\n§8> §eChoisissez votre classe!");
	
	private BukkitTask task;
	
	public ChooseClassGameState(TDM tdm) {
		super(tdm);
	}
	
	@Override
	public void start(GameState from) {
		super.start(from);
		tdm.setInGame(false);
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		List<Player> toTest = new ArrayList<>(players);
		int playersSize = players.size();
		double perTeamD = playersSize / (double) Team.values().length;
		int perTeam = (int) perTeamD;
		int perTeamMax = (int) Math.ceil(perTeamD);
		
		List<Player> toDispatch = new ArrayList<>();
		Map<Team, Integer> toAdd = new HashMap<>();
		for (Team team : Team.values()) {
			toTest.removeAll(team.getPlayers());
			int teamPlayersSize = team.getPlayers().size();
			if (perTeam > teamPlayersSize) { // pas assez de joueurs
				toAdd.put(team, perTeam - teamPlayersSize);
			}else if (teamPlayersSize > perTeamMax) { // trop de joueurs
				int toRemove = teamPlayersSize - perTeamMax;
				for (int i = teamPlayersSize - toRemove; i < teamPlayersSize; i++) {
					Player player = team.getPlayers().get(teamPlayersSize - toRemove);
					team.removePlayer(player);
					toDispatch.add(player);
				}
			}
		}
		toDispatch.addAll(toTest); // ajoute ceux qui n'ont pas choisi de team
		
		int toDispatchIndex = 0;
		for (Iterator<Entry<Team, Integer>> iterator = toAdd.entrySet().iterator(); iterator.hasNext();) {
			Entry<Team, Integer> entry = iterator.next();
			if (iterator.hasNext()) {
				int toIndex = toDispatchIndex + entry.getValue().intValue();
				for (; toDispatchIndex < toIndex; toDispatchIndex++) {
					try {
						entry.getKey().addPlayer(toDispatch.get(toDispatchIndex));
					}catch (IndexOutOfBoundsException ex) {
						ex.printStackTrace();
					}
				}
			}else { //dernière team à rajouter : donner tous les joueurs
				for (; toDispatchIndex < toDispatch.size(); toDispatchIndex++) {
					entry.getKey().addPlayer(toDispatch.get(toDispatchIndex));
				}
			}
		}
		if (toDispatchIndex < toDispatch.size()) {
			OlympaWarfare.getInstance().sendMessage("§cAjout de joueurs à la team par défaut. Ceci est un BUG.");
			for (; toDispatchIndex < toDispatch.size(); toDispatchIndex++) {
				Team.values()[0].addPlayer(toDispatch.get(toDispatchIndex));
			}
		}
		toDispatch.forEach(tdm::teamChanged);
		
		players.forEach(this::playerInventory);
		
		Prefix.BROADCAST.sendMessage(players, "Choisissez votre classe ! La partie commence dans %d secondes.", waitSeconds);
		task = Bukkit.getScheduler().runTaskLater(OlympaWarfare.getInstance(), () -> tdm.setState(WaitPlayingGameState::new), waitSeconds * 20L);
	}
	
	private void playerInventory(Player p) {
		PlayerInventory inventory = p.getInventory();
		inventory.clear();
		inventory.setItem(4, ItemUtils.item(Material.NETHER_STAR, "§bChoisis ta classe"));
		new ClassListGUI(OlympaPlayerWarfare.get(p)).create(p);
	}
	
	@Override
	public void stop() {
		super.stop();
		task.cancel();
	}
	
	@Override
	protected void handleScoreboard(Scoreboard<OlympaPlayerWarfare> scoreboard) {
		scoreboard.addLines(FixedLine.EMPTY_LINE, LINE_TITLE, FixedLine.EMPTY_LINE, OlympaPlayerWarfare.LINE_CLASS);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Team min = null;
		for (Team team : Team.values()) {
			if (min == null || team.getPlayers().size() < min.getPlayers().size()) min = team;
		}
		min.addPlayer(e.getPlayer());
		playerInventory(e.getPlayer());
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Team team = Team.getPlayerTeam(e.getPlayer());
		team.removePlayer(e.getPlayer());
		if (team.getPlayers().size() < 2) {
			Prefix.BROADCAST.sendMessage(Bukkit.getOnlinePlayers(), "§cIl n'y a plus assez de joueurs pour commencer la partie...");
			task.cancel();
			tdm.setState(WaitingGameState::new);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (e.getItem() == null) return;
		Player p = e.getPlayer();
		if (p.getInventory().getHeldItemSlot() == 4) {
			new ClassListGUI(OlympaPlayerWarfare.get(p)).create(p);
			e.setCancelled(true);
		}
	}
	
}
