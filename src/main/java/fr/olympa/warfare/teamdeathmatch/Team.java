package fr.olympa.warfare.teamdeathmatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import net.md_5.bungee.api.ChatColor;

public enum Team {
	
	BLUE(
			"§9§lÉquipe bleue",
			3,
			Material.BLUE_DYE,
			ChatColor.BLUE,
			Color.BLUE,
			new Location(Bukkit.getWorld("world"), 14, 91, 231, 180, 0),
			new Location(Bukkit.getWorld("world"), -39.5, 91, 234.5, -130, 0),
			new Location(Bukkit.getWorld("world"), -77.5, 91, 238.5, 116, 0)
			),
	RED(
			"§c§lÉquipe rouge",
			5,
			Material.RED_DYE,
			ChatColor.RED,
			Color.RED,
			new Location(Bukkit.getWorld("world"), -113, 91, 19),
			new Location(Bukkit.getWorld("world"), -104, 91, 57, 180, 0),
			new Location(Bukkit.getWorld("world"), -104, 91, 6, 0, 0)
			),
	;
	
	private final String name;
	private final int slot;
	private final ItemStack item;
	private final ChatColor color;
	private final Color armorColor;
	private final List<Location> spawnpoints;
	
	private List<Player> players = new ArrayList<>();
	
	private Team(String name, int slot, Material material, ChatColor color, Color armorColor, Location... spawnpoints) {
		this.name = name;
		this.slot = slot;
		this.color = color;
		this.armorColor = armorColor;
		this.spawnpoints = Arrays.asList(spawnpoints);
		this.item = ItemUtils.item(material, "§7Rejoindre l'" + name);
	}
	
	public String getName() {
		return name;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public Color getArmorColor() {
		return armorColor;
	}
	
	public List<Location> getSpawnpoints() {
		return spawnpoints;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void addPlayer(Player p) {
		if (players.contains(p)) return;
		Prefix.DEFAULT.sendMessage(players, "§e§l%s §7rejoint l'%s", p.getName(), name);
		Prefix.DEFAULT.sendMessage(p, "Tu as rejoint l'%s§7 !", name);
		players.add(p);
	}
	
	public void removePlayer(Player p) {
		if (players.remove(p)) {
			Prefix.DEFAULT.sendMessage(players, "§c§l%s §7quitte l'%s", p.getName(), name);
		}
	}
	
	public static Team getPlayerTeam(Player p) {
		return Arrays.stream(values()).filter(x -> x.players.contains(p)).findAny().orElse(null);
	}

}
