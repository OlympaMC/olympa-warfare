package fr.olympa.warfare.weapons.guns;

import org.bukkit.entity.Player;

import fr.olympa.api.spigot.region.tracking.flags.AbstractProtectionFlag;
import fr.olympa.api.utils.Prefix;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class GunFlag extends AbstractProtectionFlag {

	private static final BaseComponent[] NOPE_TEXT = TextComponent.fromLegacyText(Prefix.DEFAULT_BAD.formatMessage("Tu es dans une zone protégée !"));
	
	private boolean freeAmmos;
	
	public GunFlag(boolean protectedByDefault, boolean freeAmmos) {
		super(protectedByDefault);
		this.freeAmmos = freeAmmos;
	}

	public boolean isFireEnabled(Player p, boolean sendMessage) {
		boolean b = !protectedByDefault || !applies(p);
		if (!b) sendError(p);
		return b;
	}
	
	public boolean isFreeAmmos() {
		return freeAmmos;
	}

	public void sendError(Player p) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, NOPE_TEXT);
	}
	
}
