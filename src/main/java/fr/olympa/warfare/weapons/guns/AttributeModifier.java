package fr.olympa.warfare.weapons.guns;

import java.util.UUID;

public class AttributeModifier{
	
	private final UUID uuid;
	private final String name;
	private final Operation operation;
	private final float amount;
	
	private org.bukkit.attribute.AttributeModifier bukkit = null;
	
	public AttributeModifier(String name, Operation operation, float amount){
		this(UUID.randomUUID(), name, operation, amount);
	}
	
	public AttributeModifier(UUID uuid, String name, Operation operation, float amount) {
		this.uuid = uuid;
		this.name = name;
		this.operation = operation;
		this.amount = amount;
	}
	
	public String getName(){
		return name;
	}
	
	public Operation getOperation(){
		return operation;
	}
	
	public float getAmount(){
		return amount;
	}
	
	public enum Operation{
		ADD_NUMBER, ADD_MULTIPLICATOR, MULTIPLY_VALUE;
		
		public org.bukkit.attribute.AttributeModifier.Operation getBukkitOperation() {
			return switch (this) {
			case ADD_MULTIPLICATOR -> org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR;
			case ADD_NUMBER -> org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
			case MULTIPLY_VALUE -> org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1;
			default -> throw new IllegalArgumentException("Unexpected value: " + this);
			};
		}
	}
	
	public org.bukkit.attribute.AttributeModifier getBukkitModifier() {
		if (bukkit == null) bukkit = new org.bukkit.attribute.AttributeModifier(uuid, name, amount, operation.getBukkitOperation());
		return bukkit;
	}
	
	@Override
	public int hashCode() {
		int hash = name.hashCode();
		hash += 37 * operation.ordinal();
		hash += 37 * amount;
		return hash;
	}

}
