package fr.olympa.warfare.xp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.common.command.complex.Cmd;
import fr.olympa.api.common.command.complex.CommandContext;
import fr.olympa.api.spigot.command.ComplexCommand;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.WarfarePermissions;

public class LevelCommand extends ComplexCommand {
	
	public LevelCommand(Plugin plugin) {
		super(plugin, "level", "Permet de gérer les niveaux d'expérience.", WarfarePermissions.LEVEL_COMMAND, "xp", "lvl");
	}
	
	@Override
	public boolean noArguments(CommandSender sender) {
		if (!(sender instanceof Player)) return false;
		sendXP(getOlympaPlayer());
		return true;
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_OTHER", min = 1, args = "PLAYERS", syntax = "<joueur>", description = "Permet de voir l'expérience d'un autre joueur")
	public void get(CommandContext cmd) {
		sendXP(OlympaPlayerWarfare.get(cmd.getArgument(0)));
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_MANAGE", min = 3, args = { "PLAYERS", "INTEGER", "xp|level" }, syntax = "<joueur> <quantité> <xp/level>")
	public void give(CommandContext cmd) {
		OlympaPlayerWarfare player = OlympaPlayerWarfare.get(cmd.getArgument(0));
		int amount = cmd.getArgument(1);
		boolean xp;
		String type = cmd.getArgument(2);
		if (type.equals("xp")) {
			xp = true;
		}else if (type.equals("level")) {
			xp = false;
		}else {
			sendError("Donnée invalide: %s. Accepté: xp/level", type);
			return;
		}
		if (xp) {
			player.setXP(player.getXP() + amount);
			sendSuccess("%s a reçu %d xp.", player.getName(), amount);
		}else {
			player.setLevel(player.getLevel() + amount);
			sendSuccess("%s a reçu %d niveaux.", player.getName(), amount);
		}
	}
	
	@Cmd (permissionName = "LEVEL_COMMAND_MANAGE", min = 3, args = { "PLAYERS", "INTEGER", "xp|level" }, syntax = "<joueur> <quantité> <xp/level>")
	public void set(CommandContext cmd) {
		OlympaPlayerWarfare player = OlympaPlayerWarfare.get(cmd.getArgument(0));
		int amount = cmd.getArgument(1);
		boolean xp;
		String type = cmd.getArgument(2);
		if (type.equals("xp")) {
			xp = true;
		}else if (type.equals("level")) {
			xp = false;
		}else {
			sendError("Donnée invalide: %s. Accepté: xp/level", type);
			return;
		}
		if (xp) {
			player.setXP(amount);
			sendSuccess("%s a maintenant %d xp.", player.getName(), amount);
		}else {
			player.setLevel(amount);
			sendSuccess("%s a maintenant %d niveaux.", player.getName(), amount);
		}
	}
	
	public void sendXP(OlympaPlayerWarfare player) {
		sendSuccess("Expérience de %s:"
				+ "\n§e➤ Niveau: %d"
				+ "\n§e➤ Expérience: %s/%s ", player.getName(), player.getLevel(), XPManagement.formatExperience(player.getXP()), XPManagement.formatExperience(XPManagement.getXPToLevelUp(player.getLevel())));
	}
	
}
