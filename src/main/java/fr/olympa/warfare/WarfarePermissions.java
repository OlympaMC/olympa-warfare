package fr.olympa.warfare;

import fr.olympa.api.common.groups.OlympaGroup;
import fr.olympa.api.common.permission.OlympaSpigotPermission;

public class WarfarePermissions {
	
	private WarfarePermissions() {}
	
	public static final OlympaSpigotPermission TP_TIME_BYPASS = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission KIT_MANAGE_COMMAND = new OlympaSpigotPermission(OlympaGroup.GAMEMASTER);
	
	public static final OlympaSpigotPermission LEVEL_COMMAND = new OlympaSpigotPermission(OlympaGroup.PLAYER);
	public static final OlympaSpigotPermission LEVEL_COMMAND_OTHER = new OlympaSpigotPermission(OlympaGroup.ASSISTANT);
	public static final OlympaSpigotPermission LEVEL_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
	public static final OlympaSpigotPermission SPAWNPOINT_COMMAND_MANAGE = new OlympaSpigotPermission(OlympaGroup.RESP);
	
}
