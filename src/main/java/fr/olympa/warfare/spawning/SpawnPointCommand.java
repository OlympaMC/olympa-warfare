package fr.olympa.warfare.spawning;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;

import fr.olympa.api.common.chat.TxtComponentBuilder;
import fr.olympa.api.common.command.complex.Cmd;
import fr.olympa.api.common.command.complex.CommandContext;
import fr.olympa.api.spigot.command.ComplexCommand;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.WarfarePermissions;

public class SpawnPointCommand extends ComplexCommand {

	public SpawnPointCommand(OlympaWarfare plugin) {
		super(plugin, "spawnpoint", "Gérer les points de spawns.", WarfarePermissions.SPAWNPOINT_COMMAND_MANAGE, "sp");
		setAllowConsole(false);
	}

	@Cmd
	public boolean add(CommandContext cmd) {
		OlympaWarfare pvpKitPlugin = (OlympaWarfare) plugin;
		SpawnPointsManager spawnPoints = pvpKitPlugin.spawnPoints;
		try {
			spawnPoints.addSpawnPoint(getPlayer().getLocation());
			sendSuccess("Un spawnpoint a été ajouté à la liste (contient %d spawnpoints)", spawnPoints.getLocations().size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Cmd()
	public boolean list(CommandContext cmd) {
		OlympaWarfare pvpKitPlugin = (OlympaWarfare) plugin;
		SpawnPointsManager spawnPoints = pvpKitPlugin.spawnPoints;
		List<Location> locations = spawnPoints.getLocations();
		TxtComponentBuilder builder = new TxtComponentBuilder("&eIl y a &6%d&e spawn points.", locations.size()).extraSpliterBN();
		int i = 0;
		for (Location loc : locations)
			builder.extra(new TxtComponentBuilder("&a" + i++ + " &2%d, %d, %d", loc.getX(), loc.getY(), loc.getZ()).onHoverText("&6Clique pour s'y tp").onClickCommand("/tp %d %d %d %f %f", loc.getX(), loc.getY(),
					loc.getZ(), loc.getPitch(),
					loc.getYaw()));
		player.spigot().sendMessage(builder.build());
		return false;
	}

	@Cmd()
	public boolean seeAll(CommandContext cmd) {
		OlympaWarfare pvpKitPlugin = (OlympaWarfare) plugin;
		SpawnPointsManager spawnPoints = pvpKitPlugin.spawnPoints;
		List<Location> locations = spawnPoints.getLocations();
		//		if (!isConsole())
		//			OlympaTask task = pvpKitPlugin.getTask();
		//		Collection<Shulker> shulkers = player.getWorld().getEntitiesByClass(Shulker.class);
		//		if (!shulkers.stream().anyMatch(s -> s.isGlowing()))
		for (Location loc : locations) {
			for (Entity entity : loc.getChunk().getEntities())
				if (entity.getType().equals(EntityType.SHULKER)) {

					Shulker s = (Shulker) entity;
					if (s.isGlowing())
						s.remove();
				}
			Shulker shulker = loc.getWorld().spawn(loc, Shulker.class);
			shulker.setPersistent(false);
			shulker.setAI(false);
			shulker.setGravity(false);
			shulker.setInvulnerable(true);
			shulker.setSilent(true);
			shulker.setGlowing(true);
		}
		sendMessage(Prefix.DEFAULT, "Toutes les potitions sont montrées. (%d)", locations.size());
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}

}
