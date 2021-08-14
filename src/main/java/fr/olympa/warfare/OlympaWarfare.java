package fr.olympa.warfare;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.common.permission.OlympaPermission;
import fr.olympa.api.common.permission.list.OlympaAPIPermissionsSpigot;
import fr.olympa.api.common.plugin.OlympaAPIPlugin;
import fr.olympa.api.common.provider.AccountProviderAPI;
import fr.olympa.api.common.server.OlympaServer;
import fr.olympa.api.spigot.CombatManager;
import fr.olympa.api.spigot.lines.CyclingLine;
import fr.olympa.api.spigot.lines.DynamicLine;
import fr.olympa.api.spigot.lines.FixedLine;
import fr.olympa.api.spigot.region.Region;
import fr.olympa.api.spigot.region.tracking.ActionResult;
import fr.olympa.api.spigot.region.tracking.RegionEvent.EntryEvent;
import fr.olympa.api.spigot.region.tracking.flags.*;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.api.spigot.scoreboard.sign.ScoreboardManager;
import fr.olympa.api.spigot.utils.ProtocolAPI;
import fr.olympa.api.spigot.utils.TeleportationManager;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.ranking.BestKillStreakRank;
import fr.olympa.warfare.ranking.TotalKillRank;
import fr.olympa.warfare.weapons.guns.GunRegistry;
import fr.olympa.warfare.xp.LevelCommand;
import fr.olympa.warfare.xp.XPManagement;

public class OlympaWarfare extends OlympaAPIPlugin {

	private static OlympaWarfare instance;

	public static OlympaWarfare getInstance() {
		return instance;
	}

	public CombatManager combat;
	public TeleportationManager teleportationManager;
	
	public GunRegistry gunRegistry = new GunRegistry();

	public ScoreboardManager<OlympaPlayerWarfare> scoreboards;
	public DynamicLine<Scoreboard<OlympaPlayerWarfare>> lineKills = new DynamicLine<>(x -> "§7Kills: §6" + x.getOlympaPlayer().getKills().get());
	public DynamicLine<Scoreboard<OlympaPlayerWarfare>> lineLevel = new DynamicLine<>(x -> "§7Niveau: §6" + x.getOlympaPlayer().getLevel() + " §e(" + XPManagement.formatExperience(x.getOlympaPlayer().getXP()) + "/"
			+ XPManagement.formatExperience(XPManagement.getXPToLevelUp(x.getOlympaPlayer().getLevel())) + ")");
	public DynamicLine<Scoreboard<OlympaPlayerWarfare>> lineKit = new DynamicLine<>(x -> "§7Kit: " + (x.getOlympaPlayer().isInPvPZone() ? x.getOlympaPlayer().getUsedKit().getName() : "§8§oaucun"));

	public TotalKillRank totalKillRank;
	public BestKillStreakRank bestKSRank;

	public Location pvpLocation;
	public Region safeZone;

	@Override
	public void onEnable() {
		instance = this;
		super.onEnable();
		OlympaCore.getInstance().setOlympaServer(OlympaServer.WARFARE);
		OlympaCore.getInstance().getVersionHandler().disableAllUnderI(ProtocolAPI.V1_16_4);
		OlympaPermission.registerPermissions(WarfarePermissions.class);
		AccountProviderAPI.getter().setPlayerProvider(OlympaPlayerWarfare.class, OlympaPlayerWarfare::new, "pvpkit", OlympaPlayerWarfare.COLUMNS);

		OlympaCore.getInstance().getRegionManager().awaitWorldTracking("world", e -> e.getRegion().registerFlags(
				new ItemDurabilityFlag(true),
				new PhysicsFlag(true),
				new PlayerBlocksFlag(true),
				new GameModeFlag(GameMode.ADVENTURE),
				new DropFlag(true),
				new FoodFlag(true),
				new FrostWalkerFlag(false),
				new PlayerBlockInteractFlag(false, true, true)));

		scoreboards = new ScoreboardManager<OlympaPlayerWarfare>(this, "§6Olympa §e§lWarfare").addLines(
				FixedLine.EMPTY_LINE,
				lineKills,
				FixedLine.EMPTY_LINE,
				lineLevel,
				FixedLine.EMPTY_LINE,
				lineKit)
				.addFooters(
						FixedLine.EMPTY_LINE,
						CyclingLine.olympaAnimation());

		Bukkit.getPluginManager().registerEvents(new PvPKitListener(), this);
		Bukkit.getPluginManager().registerEvents(combat = new CombatManager(this, 15), this);
		combat.setSendMessages(false);

		pvpLocation = getConfig().getLocation("pvpLocation");
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

		teleportationManager = new TeleportationManager(this, WarfarePermissions.TP_TIME_BYPASS);

		new LevelCommand(this).register();

		OlympaCore.getInstance().getNameTagApi().addNametagHandler(EventPriority.LOWEST, (nametag, player, to) -> nametag.appendSuffix(XPManagement.getLevelPrefix(((OlympaPlayerWarfare) player).getLevel())));

		OlympaAPIPermissionsSpigot.GAMEMODE_COMMAND.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.FLY_COMMAND.setMinGroup(OlympaGroup.MODP);
		OlympaAPIPermissionsSpigot.TP_COMMAND_NOT_VANISH.setMinGroup(OlympaGroup.ADMIN);
		OlympaAPIPermissionsSpigot.GAMEMODE_COMMAND_OTHER.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.INVSEE_COMMAND_INTERACT.setMinGroup(OlympaGroup.MOD);
		OlympaAPIPermissionsSpigot.ECSEE_COMMAND_INTERACT.setMinGroup(OlympaGroup.MOD);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
