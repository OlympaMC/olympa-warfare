package fr.olympa.warfare.kits;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.olympa.api.common.command.complex.ArgumentParser;
import fr.olympa.api.common.command.complex.Cmd;
import fr.olympa.api.common.command.complex.CommandContext;
import fr.olympa.api.spigot.command.ComplexCommand;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.WarfarePermissions;
import fr.olympa.warfare.kits.gui.KitEditionGUI;

public class KitManageCommand extends ComplexCommand {
	
	public KitManageCommand(Plugin plugin) {
		super(plugin, "kitmanage", "Permet de modifier les kits.", WarfarePermissions.KIT_MANAGE_COMMAND);
		setAllowConsole(false);
		addArgumentParser("KIT", new ArgumentParser<>((sender, arg) -> OlympaWarfare.getInstance().kits.getKits().stream().map(Kit::getId).collect(Collectors.toList()), x -> OlympaWarfare.getInstance().kits.getKit(x), x -> "Le kit " + x + " est introuvable", false));
	}
	
	@Cmd (min = 2, syntax = "<id> <niveau> [nom]", description = "Créer un kit", args = { "", "INTEGER", "" })
	public void add(CommandContext cmd) {
		String id = cmd.getArgument(0);
		if (OlympaWarfare.getInstance().kits.getKit(id) != null) {
			sendError("Le kit %s existe déjà.", id);
			return;
		}
		Player player = getPlayer();
		new KitEditionGUI(new ItemStack[0], items -> {
			try {
				Kit kit = OlympaWarfare.getInstance().kits.addKit(id, cmd.getArgumentsLength() == 2 ? id : cmd.getFrom(2), items, items[0], cmd.getArgument(1));
				Prefix.DEFAULT_GOOD.sendMessage(player, "Le kit %s a été créé ! (%d items)", kit.getId(), kit.getItems().length);
			}catch (SQLException | IOException e) {
				e.printStackTrace();
				Prefix.ERROR.sendMessage(player, "Une erreur est survenue lors de la création du kit.");
				player.getInventory().addItem(items);
			}
		}).create(player);
	}
	
	@Cmd (min = 2, args = "KIT", syntax = "<id> <nouveau nom>", description = "Modifier le nom du kit")
	public void changeName(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		kit.setName(cmd.getFrom(1));
		sendSuccess("Le kit %s a été renommé.", kit.getId());
	}
	
	@Cmd (min = 1, args = "KIT", syntax = "<id>", description = "Modifier le contenu du kit")
	public void changeItems(CommandContext cmd) {
		Player player = getPlayer();
		Kit kit = cmd.getArgument(0);
		new KitEditionGUI(kit.getItems(), items -> {
			kit.setItems(items);
			Prefix.DEFAULT_GOOD.sendMessage(player, "Le kit %s a été modifié. (%d items)", kit.getId(), kit.getItems().length);
		}).create(player);
	}
	
	@Cmd (min = 2, args = "KIT", syntax = "<id> <description d'un item>", description = "Ajouter une description d'item")
	public void addItemDescription(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		kit.addItemDescription(cmd.getFrom(1));
		sendSuccess("Une description d'item a été rajoutée au kit %s.", kit.getId());
	}
	
	@Cmd (min = 2, args = { "KIT", "INTEGER" }, syntax = "<id> <index de la description>", description = "Supprimer une description d'item")
	public void removeItemDescription(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		try {
			kit.removeItemDescription(cmd.getArgument(1));
			sendSuccess("Une description d'item a été supprimée du kit %s.", kit.getId());
		}catch (IndexOutOfBoundsException ex) {
			sendError("L'index spécifié est invalide.");
		}
	}
	
	@Cmd (min = 2, args = { "KIT", "INTEGER" }, syntax = "<id> <nouveau niveau>", description = "Modifier le niveau du kit")
	public void changeLevel(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		kit.setMinLevel(cmd.getArgument(1));
		sendSuccess("Le kit %s a changé de niveau.", kit.getId());
	}
	
	@Cmd (min = 1, args = "KIT", syntax = "<kit>", description = "Modifier l'icône du kit")
	public void changeIcon(CommandContext cmd) {
		Kit kit = cmd.getArgument(0);
		ItemStack icon = getPlayer().getInventory().getItemInMainHand();
		if (icon.getType() == Material.AIR) {
			sendError("Tu dois tenir un item dans ta main !");
			return;
		}
		kit.setIcon(icon);
		sendSuccess("L'icône du kit %s a été modifiée !", kit.getId());
	}
	
	@Cmd (min = 1, args = "KIT", syntax = "<id>", description = "Supprimer le kit")
	public void delete(CommandContext cmd) {
		try {
			Kit kit = cmd.getArgument(0);
			OlympaWarfare.getInstance().kits.removeKit(kit);
			sendSuccess("Le kit %s a été supprimé.", kit.getId());
		}catch (SQLException e) {
			e.printStackTrace();
			sendError(e);
		}
	}
	
}
