package fr.skyost.hungergames.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Joiner;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.utils.ErrorSender;
import fr.skyost.hungergames.utils.JsonItemStack;
import fr.skyost.hungergames.utils.Utils;

public class HungerGamesCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
		try {
			if(!(commandSender instanceof Player)) {
				commandSender.sendMessage(ChatColor.RED + "Please perform this command from the game !");
				return true;
			}
			if(args.length == 0) {
				return false;
			}
			final Player player = (Player)commandSender;
			switch(args[0].toLowerCase()) {
			case "kits":
			case "kit":
				if(args.length < 3) {
					return false;
				}
				final String kitName = Utils.colourize(Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length)));
				if(args[1].equalsIgnoreCase("create")) {
					if(!commandSender.hasPermission("hungergames.kits.create")) {
						commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
						return true;
					}
					if(HungerGames.config.Kits_List.get(kitName) != null) {
						commandSender.sendMessage(HungerGames.messages.Messages_20);
						return true;
					}
					final List<String> items = new ArrayList<String>();
					final PlayerInventory inventory = player.getInventory();
					ItemStack item;
					Material material = null;
					for(int i = 0, x = 0; i != inventory.getSize(); i++) {
						item = inventory.getItem(i);
						if(item != null && item.getType() != Material.AIR) {
							items.add(new JsonItemStack(item).toJson());
							x++;
						}
						if(x == 1) {
							material = item.getType();
						}
					}
					HungerGames.config.Kits_List.put(kitName, items);
					item = new ItemStack(material);
					final ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(kitName);
					item.setItemMeta(meta);
					HungerGames.kitsMenu.addItem(item);
				}
				else if(args[1].equalsIgnoreCase("delete")) {
					if(!commandSender.hasPermission("hungergames.kits.delete")) {
						commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
						return true;
					}
					if(HungerGames.config.Kits_List.get(kitName) == null) {
						commandSender.sendMessage(HungerGames.messages.Messages_21);
						return true;
					}
					HungerGames.config.Kits_List.remove(kitName);
					for(final ItemStack item : HungerGames.kitsMenu.getContents()) {
						if(item != null && item.getType() != Material.AIR && item.getItemMeta().getDisplayName().equals(kitName)) {
							HungerGames.kitsMenu.remove(item);
						}
					}
				}
				else {
					return false;
				}
				HungerGames.config.save();
				commandSender.sendMessage(HungerGames.messages.Messages_22);
				break;
			case "join":
				if(!commandSender.hasPermission("hungergames.join")) {
					commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
					return true;
				}
				if(HungerGames.players.get(player) != null) {
					commandSender.sendMessage(HungerGames.messages.Messages_2);
					return true;
				}
				if(!HungerGames.config.Spectators_Enable && (HungerGames.totalPlayers == HungerGames.config.Game_MaxPlayers || (HungerGames.currentStep != Step.LOBBY && HungerGames.currentStep != Step.FIRST_COUNTDOWN))) {
					commandSender.sendMessage(HungerGames.messages.Messages_1);
					return true;
				}
				boolean spectator = false;
				if(HungerGames.currentStep == Step.SECOND_COUNTDOWN || HungerGames.currentStep == Step.GAME) {
					player.sendMessage(HungerGames.messages.Messages_12);
					spectator = true;
				}
				HungerGamesAPI.addPlayer(player, spectator);
				break;
			case "leave":
				if(!commandSender.hasPermission("hungergames.leave")) {
					commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
					return true;
				}
				if(HungerGames.players.get(player) == null) {
					commandSender.sendMessage(HungerGames.messages.Messages_6);
					return true;
				}
				player.sendMessage(HungerGames.messages.Messages_11);
				HungerGamesAPI.removePlayer(player, false);
				break;
			case "infos":
				if(!commandSender.hasPermission("hungergames.infos")) {
					commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
					return true;
				}
				final String players;
				if(HungerGames.players.size() == 0) {
					players = "";
				}
				else {
					final StringBuilder builder = new StringBuilder();
					for(final Player playerKey : HungerGames.players.keySet()) {
						builder.append(HungerGames.spectatorsManager.hasSpectator(playerKey) ? "(S)" : "(P)" + playerKey.getName() + " ");
					}
					players = builder.toString();
				}
				commandSender.sendMessage(HungerGames.messages.Messages_13.replaceAll("/n/", String.valueOf(HungerGames.players.size())).replaceAll("/players/", players).replaceAll("/map/", HungerGames.currentMap.getName()).replaceAll("/status/", HungerGamesAPI.getCurrentMotd()).replaceAll("/line-separator/", "\n"));
				break;
			case "winners":
				if(!commandSender.hasPermission("hungergames.winners")) {
					commandSender.sendMessage(ChatColor.RED + "You do not have the permission to perform this command.");
					return true;
				}
				if(HungerGames.winnersMap.size() == 0) {
					commandSender.sendMessage(ChatColor.RED + "There is not any winner !");
					return true;
				}
				final int page;
				if(args.length >= 2) {
					if(Utils.isNumeric(args[1])) {
						page = Integer.parseInt(args[1]);
					}
					else {
						return false;
					}
				}
				else {
					page = 1;
				}
				final int pages = HungerGames.pages.getTotalPages();
				if(page < 1 || page > pages) {
					commandSender.sendMessage(HungerGames.messages.Messages_18.replaceAll("/total-pages/", String.valueOf(pages)));
					return true;
				}
				commandSender.sendMessage(HungerGames.pages.getPage(page));
				break;
			default:
				return false;
			}
		}
		catch(Exception ex) {
			ErrorSender.uploadAndSend(ex);
		}
		return true;
	}
	
}
