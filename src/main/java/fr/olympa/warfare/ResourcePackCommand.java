package fr.olympa.warfare;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.common.permission.OlympaSpigotPermission;
import fr.olympa.api.spigot.command.OlympaCommand;
import fr.olympa.api.utils.Prefix;

public class ResourcePackCommand extends OlympaCommand {
	
	public String url;
	public String hash;
	
	public ResourcePackCommand(Plugin plugin, ConfigurationSection packConfig) {
		super(plugin, "resourcepack", "Donne un lien pour télécharger le resource pack.", (OlympaSpigotPermission) null, "texturepack");
		set(packConfig);
	}
	
	public void set(ConfigurationSection packConfig) {
		url = packConfig.getString("url");
		hash = packConfig.getString("hash");
		//url = url + "#" + hash;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sendHoverAndURL(Prefix.DEFAULT_GOOD, "Vous pouvez télécharger le pack ici !", "Clique pour télécharger le pack", url);
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}
	
}
