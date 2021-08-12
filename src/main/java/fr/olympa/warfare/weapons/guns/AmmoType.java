package fr.olympa.warfare.weapons.guns;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public enum AmmoType{
	
	LIGHT("Munitions légères", Material.GREEN_DYE, Material.LIME_DYE, 3, ChatColor.YELLOW),
	HEAVY("Munitions lourdes", Material.LAPIS_LAZULI, Material.LIGHT_BLUE_DYE, 3, ChatColor.GREEN),
	HANDWORKED("Munitions artisanales", Material.RED_DYE, Material.PINK_DYE, 3, ChatColor.GRAY),
	CARTRIDGE("Cartouches", Material.GRAY_DYE, Material.LIGHT_GRAY_DYE, 1, ChatColor.RED);
	
	private String name;
	private Material empty, fill;
	private int ammosPerItem;
	private ChatColor color;
	
	private ItemStack itemFilled, itemEmpty;
	private ShapelessRecipe recipe;

	private AmmoType(String name, Material empty, Material fill, int ammosPerItem, ChatColor color) {
		this.name = name;
		this.empty = empty;
		this.fill = fill;
		this.ammosPerItem = ammosPerItem;
		this.color = color;

		itemFilled = new ItemStack(fill);
		ItemMeta meta = itemFilled.getItemMeta();
		meta.setCustomModelData(1);
		meta.setDisplayName("§a" + name + " (x" + ammosPerItem + ")");
		itemFilled.setItemMeta(meta);
		
		itemEmpty = new ItemStack(empty);
		meta = itemEmpty.getItemMeta();
		meta.setCustomModelData(1);
		meta.setDisplayName("§b" + name + " vides (x" + ammosPerItem + ")");
		meta.setLore(Arrays.asList("§8> §7Associez-y de la", "§7poudre à canon."));
		itemEmpty.setItemMeta(meta);
	}
	
	public String getName(){
		return name;
	}
	
	public String getColoredName() {
		return color + name;
	}
	
	public Material getEmptyAmmoType(){
		return empty;
	}
	
	public Material getFilledAmmoType(){
		return fill;
	}
	
	public int getAmmosPerItem() {
		return ammosPerItem;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public ShapelessRecipe getRecipe() {
		return recipe;
	}
	
	/**
	 * @param p Player sur qui compter les munitions
	 * @return Nombre de munitions que le joueur a dans son inventaire
	 */
	public int getAmmos(Player p){
		int i = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (is != null && is.getType() == fill) i += is.getAmount();
		}
		return i;
	}
	
	/**
	 * Retirer la quantité données de munitions de l'inventaire du joueur.<br>
	 * <b>Aucune vérification de quantité n'est incluse, veuillez l'effectuer au préalable !</b>
	 * @param p Joueur à qui retirer les munitions
	 * @param amount Quantité de munitions à retirer
	 * @return Quantité de munitions retirées
	 */
	public int removeAmmos(Player p, int amount){
		int removed = 0;
		ItemStack[] contents = p.getInventory().getContents();
		for (int i = 0; i < contents.length; i++) {
			ItemStack is = contents[i];
			if (is == null || is.getType() != fill) continue;
			if (is.getAmount() == amount) {
				p.getInventory().setItem(i, null);
				removed += amount;
				break;
			}else if (is.getAmount() > amount) {
				removed += amount;
				is.setAmount(is.getAmount() - amount);
				break;
			}else if (is.getAmount() < amount) {
				amount -= is.getAmount();
				removed += is.getAmount();
				p.getInventory().setItem(i, null);
			}
		}
		return removed;
	}
	
	/**
	 * @param amount Quantité de munitions
	 * @param filled <tt>true</tt> si les munitions sont pleines
	 * @return Item correspondant aux paramètres des munitions
	 */
	public ItemStack getAmmo(int amount, boolean filled) {
		ItemStack item = filled ? itemFilled : itemEmpty;
		item = item.clone();
		item.setAmount(amount);
		return item;
	}

	public static ItemStack getPowder(int amount) {
		ItemStack item = new ItemStack(Material.WHITE_DYE);
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(1);
		meta.setDisplayName("§aPoudre à canon");
		meta.setLore(Arrays.asList("§8> §7Permet de charger", "§7des cartouches vides."));
		item.setItemMeta(meta);
		return item;
	}

}
