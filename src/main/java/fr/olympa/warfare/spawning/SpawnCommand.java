package fr.olympa.warfare.spawning;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.spigot.command.OlympaCommand;
import fr.olympa.api.common.permission.OlympaSpigotPermission;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.PvPKitListener;

public class SpawnCommand extends OlympaCommand {
	
	public SpawnCommand(Plugin plugin) {
		super(plugin, "spawn", "Téléporte le joueur au spawn.", (OlympaSpigotPermission) null);
		setAllowConsole(false);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = getPlayer();
		OlympaWarfare.getInstance().teleportationManager.teleport(player, player.getWorld().getSpawnLocation(), Prefix.DEFAULT_GOOD.formatMessage("Bienvenue au spawn !"), () -> {
			OlympaPlayerWarfare.get(player).setInPvPZone(null);
			PvPKitListener.giveMenuItem(player);
		});
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
