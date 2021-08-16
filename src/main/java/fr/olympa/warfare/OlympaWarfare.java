package fr.olympa.warfare;

import java.sql.SQLException;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.common.permission.OlympaPermission;
import fr.olympa.api.common.permission.list.OlympaAPIPermissionsSpigot;
import fr.olympa.api.common.plugin.OlympaAPIPlugin;
import fr.olympa.api.common.provider.AccountProviderAPI;
import fr.olympa.api.common.server.OlympaServer;
import fr.olympa.api.spigot.lines.CyclingLine;
import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.region.Region;
import fr.olympa.api.spigot.region.tracking.ActionResult;
import fr.olympa.api.spigot.region.tracking.RegionEvent.EntryEvent;
import fr.olympa.api.spigot.region.tracking.flags.*;
import fr.olympa.api.spigot.scoreboard.sign.ScoreboardManager;
import fr.olympa.api.spigot.utils.ProtocolAPI;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.ranking.TotalKillRank;
import fr.olympa.warfare.teamdeathmatch.TDM;
import fr.olympa.warfare.weapons.guns.GunFlag;
import fr.olympa.warfare.weapons.guns.GunRegistry;
import fr.olympa.warfare.xp.LevelCommand;
import fr.olympa.warfare.xp.XPManagement;

public class OlympaWarfare extends OlympaAPIPlugin {

	private static OlympaWarfare instance;

	public static OlympaWarfare getInstance() {
		return instance;
	}
	
	public GunRegistry gunRegistry;

	public ScoreboardManager<OlympaPlayerWarfare> scoreboards;

	public TotalKillRank totalKillRank;

	public Region safeZone;
	public Location waitRespawnLocation;

	public ResourcePackCommand resourcePackCommand;
	
	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		OlympaCore.getInstance().setOlympaServer(OlympaServer.WARFARE);
		OlympaCore.getInstance().getVersionHandler().disableAllUnderI(ProtocolAPI.V1_16_4);
		OlympaPermission.registerPermissions(WarfarePermissions.class);
		AccountProviderAPI.getter().setPlayerProvider(OlympaPlayerWarfare.class, OlympaPlayerWarfare::new, "warfare", OlympaPlayerWarfare.COLUMNS);

		gunRegistry = new GunRegistry();
		
		OlympaCore.getInstance().getRegionManager().awaitWorldTracking("world", e -> e.getRegion().registerFlags(
				new ItemDurabilityFlag(true),
				new PhysicsFlag(true),
				new PlayerBlocksFlag(true),
				new GameModeFlag(GameMode.ADVENTURE),
				new DropFlag(true),
				new FoodFlag(true),
				new GunFlag(false, true),
				new FrostWalkerFlag(false),
				new PlayerBlockInteractFlag(true, true, true)));

		scoreboards = new ScoreboardManager<OlympaPlayerWarfare>(this, "§6Olympa §e§lWarfare")
				.addFooters(
						FixedLine.EMPTY_LINE,
						CyclingLine.olympaAnimation());

		waitRespawnLocation = getConfig().getLocation("waitRespawnLocation");
		safeZone = getConfig().getSerializable("safeZone", Region.class);
		OlympaCore.getInstance().getRegionManager().registerRegion(safeZone, "safeZone", EventPriority.HIGH, new DamageFlag(false));
		OlympaCore.getInstance().getRegionManager().registerRegion(getConfig().getSerializable("killbox", Region.class), "killbox", EventPriority.HIGH, new Flag() {
			@Override
			public fr.olympa.api.spigot.region.tracking.ActionResult enters(EntryEvent event) {
				getTask().runTask(() -> event.getPlayer().damage(100000));
				return ActionResult.ALLOW;
			}
		});

		try {
			totalKillRank = new TotalKillRank(getConfig().getLocation("rankingHolograms.totalKills"));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		new LevelCommand(this).register();

		OlympaCore.getInstance().getNameTagApi().addNametagHandler(EventPriority.LOWEST, (nametag, player, to) -> nametag.appendPrefix(XPManagement.getLevelPrefix(((OlympaPlayerWarfare) player).getLevel())));

		OlympaAPIPermissionsSpigot.GAMEMODE_COMMAND.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.FLY_COMMAND.setMinGroup(OlympaGroup.MODP);
		OlympaAPIPermissionsSpigot.TP_COMMAND_NOT_VANISH.setMinGroup(OlympaGroup.ADMIN);
		OlympaAPIPermissionsSpigot.GAMEMODE_COMMAND_OTHER.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.INVSEE_COMMAND_INTERACT.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.ECSEE_COMMAND_INTERACT.setMinGroup(OlympaGroup.MOD);
		
		resourcePackCommand = new ResourcePackCommand(this, getConfig().getConfigurationSection("resourcePack"));
		resourcePackCommand.register();
		
		getServer().getPluginManager().registerEvents(new WarfareListener(), this);
		
		new TDM(this, getConfig().getInt("minPlayers"));
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
