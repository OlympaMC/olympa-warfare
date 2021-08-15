package fr.olympa.warfare;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.common.permission.OlympaSpigotPermission;

public class WarfarePermissions {
	
	private WarfarePermissions() {}
	
	public static final OlympaSpigotPermission GROUP_SURVIVANT = new OlympaSpigotPermission(OlympaGroup.RESP, new OlympaGroup[] { OlympaGroup.ZTA_SURVIVANT, OlympaGroup.ZTA_RODEUR, OlympaGroup.ZTA_SAUVEUR, OlympaGroup.ZTA_HEROS, OlympaGroup.ZTA_LEGENDE });
	public static final OlympaSpigotPermission GROUP_RODEUR = new OlympaSpigotPermission(OlympaGroup.RESP, new OlympaGroup[] { OlympaGroup.ZTA_RODEUR, OlympaGroup.ZTA_SAUVEUR, OlympaGroup.ZTA_HEROS, OlympaGroup.ZTA_LEGENDE });
	public static final OlympaSpigotPermission GROUP_SAUVEUR = new OlympaSpigotPermission(OlympaGroup.RESP, new OlympaGroup[] { OlympaGroup.ZTA_SAUVEUR, OlympaGroup.ZTA_HEROS, OlympaGroup.ZTA_LEGENDE });
	public static final OlympaSpigotPermission GROUP_HEROS = new OlympaSpigotPermission(OlympaGroup.RESP, new OlympaGroup[] { OlympaGroup.ZTA_HEROS, OlympaGroup.ZTA_LEGENDE });
	public static final OlympaSpigotPermission GROUP_LEGENDE = new OlympaSpigotPermission(OlympaGroup.RESP, new OlympaGroup[] { OlympaGroup.ZTA_LEGENDE });
	
	public static final OlympaSpigotPermission TP_TIME_BYPASS = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission KIT_MANAGE_COMMAND = new OlympaSpigotPermission(OlympaGroup.GAMEMASTER);
	
	public static final OlympaSpigotPermission LEVEL_COMMAND = new OlympaSpigotPermission(OlympaGroup.PLAYER);
	public static final OlympaSpigotPermission LEVEL_COMMAND_OTHER = new OlympaSpigotPermission(OlympaGroup.ASSISTANT);
	public static final OlympaSpigotPermission LEVEL_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission SPAWNPOINT_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
}
