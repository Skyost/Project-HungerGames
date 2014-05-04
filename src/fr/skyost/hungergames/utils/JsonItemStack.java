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
	
	private final String material;
	private final String name;
	private final List<String> lore;
	private final HashMap<String, Long> enchantments = new HashMap<String, Long>();
	private final long amount;
	
	public JsonItemStack(final ItemStack item) {
		material = item.getType().name();
		final ItemMeta meta = item.getItemMeta();
		name = meta.getDisplayName();
		lore = meta.getLore();
		for(final Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
			enchantments.put(entry.getKey().getName(), Long.valueOf(entry.getValue()));
		}
		this.amount = item.getAmount();
	}
	
	public JsonItemStack(final String material, final String name, final String lore, final String enchantment, final Long enchantmentLevel, final Long amount) {
		this(material, name, Arrays.asList(lore), new HashMap<String, Long>() {
			private static final long serialVersionUID = 1L; {
				put(enchantment, enchantmentLevel);
			}
		}, amount);
	}
	
	public JsonItemStack(final String material, final String name, final List<String> lore, final HashMap<String, Long> enchantments, final Long amount) {
		this.material = material;
		this.name = name;
		this.lore = lore;
		if(enchantments != null) {
			this.enchantments.putAll(enchantments);
		}
		this.amount = amount == null ? 1 : amount;
	}
	
	@SuppressWarnings("unchecked")
	public final String toJson() {
		try {
			final JSONObject object = new JSONObject();
			object.put("material", material);
			object.put("name", name);
			object.put("lore", lore);
			object.put("enchantments", enchantments);
			object.put("amount", amount);
			return object.toJSONString();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			HungerGames.logsManager.log("This error has not been sent but you can send it manually.", Level.WARNING);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static final JsonItemStack fromJson(final String jsonItemStack) {
		try {
			final JSONObject array = (JSONObject)JSONValue.parse(jsonItemStack);
			return new JsonItemStack((String)array.get("material"), (String)array.get("name"), (List<String>)array.get("lore"), (HashMap<String, Long>)array.get("enchantments"), (Long)array.get("amount"));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			HungerGames.logsManager.log("This error has not been sent but you can send it manually.", Level.WARNING);
		}
		return null;
	}
	
	public final ItemStack toItemStack() {
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
		if(enchantments.size() != 0) {
			for(final Entry<String, Long> entry : enchantments.entrySet()) {
				meta.addEnchant(Enchantment.getByName(entry.getKey()), Ints.checkedCast(entry.getValue()), true);
			}
			applyMeta = true;
		}
		itemStack.setItemMeta(meta);
		if(applyMeta) {
			itemStack.setAmount(Ints.checkedCast(amount));
		}
		return itemStack;
	}
	
}
