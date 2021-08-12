package fr.olympa.warfare.teamdeathmatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

public enum Team {
	
	BLUE(
			"§8Équipe bleue"),
	RED(
			"§cÉquipe rouge");
	
	private final String name;
	
	private List<Player> players = new ArrayList<>();
	
	private Team(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Team getPlayerTeam(Player p) {
		return Arrays.stream(values()).filter(x -> x.players.contains(p)).findAny().orElse(null);
	}

}
