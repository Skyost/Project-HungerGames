package fr.skyost.hungergames.commands.subcommands.hungergames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Joiner;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.hungergames.utils.JsonItemStack;
import fr.skyost.hungergames.utils.Utils;

public class KitSubCommand implements CommandInterface {
	
	@Override
	public final String[] names() {
		return new String[]{"kit", "kits"};
	}
	
	@Override
	public final boolean forcePlayer() {
		return true;
	}
	
	@Override
	public final String getPermission() {
		return null;
	}
	
	@Override
	public final int getMinArgsLength() {
		return 2;
	}
	
	@Override
	public final String getUsage() {
		return "kit <[create|delete]> <kitname>";
	}

	@Override
	public boolean onCommand(final CommandSender sender, final String[] args) throws InvalidConfigurationException {
		final String kitName = Utils.colorize(Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));
		if(args[0].equalsIgnoreCase("create")) {
			if(!sender.hasPermission("hungergames.kits.create")) {
				sender.sendMessage(HungerGames.messages.messagePermission);
				return true;
			}
			if(HungerGames.config.kitsList.get(kitName) != null) {
				sender.sendMessage(HungerGames.messages.message20);
				return true;
			}
			final List<String> items = new ArrayList<String>();
			final PlayerInventory inventory = ((Player)sender).getInventory();
			if(Utils.isInventoryEmpty(inventory, HungerGames.kitSelector)) {
				sender.sendMessage(HungerGames.messages.message23);
				return true;
			}
			ItemStack item = null;
			Material material = null;
			for(int i = 0, x = 0; i != inventory.getSize(); i++) {
				item = inventory.getItem(i);
				if(item != null && !item.equals(HungerGames.kitSelector)) {
					if(item != null && item.getType() != Material.AIR) {
						items.add(new JsonItemStack(item).toJson());
						x++;
					}
					if(x == 1) {
						material = item.getType();
					}
				}
			}
			HungerGames.config.kitsList.put(kitName, items);
			item = new ItemStack(material);
			final ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(kitName);
			item.setItemMeta(meta);
			HungerGames.kitsMenu.addItem(item);
		}
		else if(args[0].equalsIgnoreCase("delete")) {
			if(!sender.hasPermission("hungergames.kits.delete")) {
				sender.sendMessage(HungerGames.messages.messagePermission);
				return true;
			}
			if(HungerGames.config.kitsList.get(kitName) == null) {
				sender.sendMessage(HungerGames.messages.message21);
				return true;
			}
			HungerGames.config.kitsList.remove(kitName);
			for(final ItemStack item : HungerGames.kitsMenu.getContents()) {
				if(item != null && item.getType() != Material.AIR && item.getItemMeta().getDisplayName().equals(kitName)) {
					HungerGames.kitsMenu.remove(item);
				}
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + getUsage());
			return true;
		}
		HungerGames.config.save();
		sender.sendMessage(HungerGames.messages.message22);
		return true;
	}

}
