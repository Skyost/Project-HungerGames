package fr.skyost.hungergames.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.primitives.Ints;

import fr.skyost.hungergames.HungerGames;

public class JsonItemStack {
	
	private final Object otherData;
	private final String material;
	private final String name;
	private List<String> lore;
	private HashMap<String, Long> enchantments;
	private final Long amount;
	
	public JsonItemStack(final ItemStack item) {
		otherData = null;
		material = item.getType().name();
		final ItemMeta meta = item.getItemMeta();
		name = meta.getDisplayName();
		lore = meta.getLore();
		enchantments = new HashMap<String, Long>();
		for(final Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
			enchantments.put(entry.getKey().getName(), Long.valueOf(entry.getValue()));
		}
		this.amount = Long.valueOf(item.getAmount());
	}
	
	public JsonItemStack(final Object otherData, final String material, final String name, final String lore, final String enchantment, final Long enchantmentLevel, final Long amount) {
		this.otherData = otherData;
		this.material = material;
		this.name = name;
		if(lore != null) {
			this.lore = Arrays.asList(lore);
		}
		if(enchantment != null) {
			final HashMap<String, Long> enchantments = new HashMap<String, Long>();
			enchantments.put(enchantment, enchantmentLevel);
			this.enchantments = enchantments;
		}
		this.amount = amount;
	}
	
	public JsonItemStack(final Object otherData, final String material, final String name, final List<String> lore, final HashMap<String, Long> enchantments, final Long amount) {
		this.otherData = otherData;
		this.material = material;
		this.name = name;
		this.lore = lore;
		this.enchantments = enchantments;
		this.amount = amount == null ? 1 : amount;
	}
	
	public final Object getOtherData() {
		return otherData;
	}
	
	@SuppressWarnings("unchecked")
	public final JSONObject toJsonObject(final String otherData) {
		final JSONObject object = new JSONObject();
		if(otherData != null) {
			object.put(otherData, this.otherData);
		}
		object.put("material", material);
		object.put("name", name);
		object.put("lore", lore);
		object.put("enchantments", enchantments);
		object.put("amount", amount);
		return object;
	}
	
	public final String toJson(final String otherData) {
		return toJsonObject(otherData).toJSONString();
	}

	@SuppressWarnings("unchecked")
	public static final JsonItemStack fromJson(final String jsonItemStack, final String otherData) {
		try {
			final JSONObject json = (JSONObject)JSONValue.parse(jsonItemStack);
			return new JsonItemStack(json.get(otherData), (String)json.get("material"), (String)json.get("name"), (List<String>)json.get("lore"), (HashMap<String, Long>)json.get("enchantments"), (Long)json.get("amount"));
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			HungerGames.logsManager.log("This error has not been sent but you can send it manually.", Level.WARNING);
		}
		return null;
	}
	
	public final ItemStack toItemStack() {
		if(!Utils.isEnum(Material.class, material)) {
			HungerGames.logsManager.log("\"" + material + "\" is not a valid material.", Level.WARNING);
			return null;
		}
		final ItemStack itemStack = new ItemStack(material == null ? Material.GRASS : Material.valueOf(material));
		final ItemMeta meta = itemStack.getItemMeta();
		boolean applyMeta = false;
		if(name != null) {
			meta.setDisplayName(name);
			applyMeta = true;
		}
		if(lore != null) {
			meta.setLore(lore);
			applyMeta = true;
		}
		if(enchantments != null && enchantments.size() != 0) {
			for(final Entry<String, Long> entry : enchantments.entrySet()) {
				final String enchantmentName = entry.getKey();
				final Enchantment enchantment = Enchantment.getByName(enchantmentName);
				if(enchantment == null) {
					HungerGames.logsManager.log("The enchantment \"" + enchantmentName + "\" was not found.", Level.WARNING);
					continue;
				}
				meta.addEnchant(enchantment, Ints.checkedCast(entry.getValue()), true);
			}
			applyMeta = true;
		}
		itemStack.setItemMeta(meta);
		if(applyMeta && amount != null) {
			itemStack.setAmount(Ints.checkedCast(amount));
		}
		return itemStack;
	}
	
}
