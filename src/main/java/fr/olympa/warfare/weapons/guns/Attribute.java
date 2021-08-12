package fr.olympa.warfare.weapons.guns;

import java.util.HashMap;
import java.util.Map;

public class Attribute{
	
	private float baseValue;
	private final Map<String, AttributeModifier> modifiers = new HashMap<>();
	
	private float cachedValue;

	public Attribute(float baseValue){
		setBaseValue(baseValue);
	}
	
	public float getBaseValue(){
		return baseValue;
	}
	
	public void setBaseValue(float newValue) {
		this.baseValue = newValue;
		computeValue();
	}
	
	public void addModifier(AttributeModifier modifier){
		modifiers.put(modifier.getName(), modifier);
		computeValue();
	}
	
	public void removeModifier(String name) {
		modifiers.remove(name);
		computeValue();
	}
	
	public int getModifiersSize() {
		return modifiers.size();
	}
	
	public float getValue(){
		return cachedValue;
	}
	
	private void computeValue() {
		if (modifiers.isEmpty()) {
			cachedValue = baseValue;
		}else {
			float value = baseValue;
			float multiplicator = 1;
			for (AttributeModifier modifier : modifiers.values()) {
				switch (modifier.getOperation()) {
				case ADD_NUMBER:
					value += modifier.getAmount();
					break;
				case MULTIPLY_VALUE:
					value *= modifier.getAmount();
					break;
				case ADD_MULTIPLICATOR:
					multiplicator += modifier.getAmount();
					break;
				}
			}
			value *= multiplicator;
			cachedValue = value;
		}
	}
	
}
