package fr.skyost.hungergames.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.primitives.Ints;

public class JsonItemStack {
	
	private final String material;
	private final String name;
	private final List<String> lore;
	private final HashMap<String, Long> enchantments = new HashMap<String, Long>();
	
	public JsonItemStack(final String material, final String name, final String lore, final String enchantment, final Long enchantmentLevel) {
		this.material = material;
		this.name = name;
		this.lore = Arrays.asList(lore);
		if(enchantment != null) {
			enchantments.put(enchantment, enchantmentLevel == null ? 1L : enchantmentLevel);
		}
	}
	
	public JsonItemStack(final String material, final String name, final List<String> lore, final HashMap<String, Long> enchantments) {
		this.material = material;
		this.name = name;
		this.lore = lore;
		if(enchantments != null) {
			this.enchantments.putAll(enchantments);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String toJson() {
		try {
			final JSONObject object = new JSONObject();
			object.put("material", material);
			object.put("name", name);
			object.put("lore", lore);
			object.put("enchantments", enchantments);
			return object.toJSONString();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.uploadAndSend(ex);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static JsonItemStack fromJson(final String jsonItemStack) {
		try {
			final JSONObject array = (JSONObject)JSONValue.parse(jsonItemStack);
			return new JsonItemStack((String)array.get("material"), (String)array.get("name"), (List<String>)array.get("lore"), (HashMap<String, Long>)array.get("enchantments"));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.uploadAndSend(ex);
		}
		return null;
	}
	
	public ItemStack toItemStack() {
		final ItemStack itemStack = new ItemStack(Material.valueOf(material));
		final ItemMeta meta = itemStack.getItemMeta();
		if(name != null) {
			meta.setDisplayName(name);
		}
		if(lore != null) {
			meta.setLore(lore);
		}
		if(enchantments.size() != 0) {
			for(final Entry<String, Long> entry : enchantments.entrySet()) {
				meta.addEnchant(Enchantment.getByName(entry.getKey()), Ints.checkedCast(entry.getValue()), true);
			}
		}
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	
}
