package fr.skyost.hungergames.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;
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
			case "setlobby":
			case "set-lobby":
				if(!commandSender.hasPermission("hungergames.lobby.set")) {
					commandSender.sendMessage(HungerGames.messages.messagePermission);
					return true;
				}
				final Location location = player.getLocation();
				HungerGames.config.lobbyWorld = location.getWorld().getName();
				HungerGames.config.lobbySpawnX = location.getX();
				HungerGames.config.lobbySpawnY = location.getY();
				HungerGames.config.lobbySpawnZ = location.getZ();
				HungerGames.config.save();
				commandSender.sendMessage(HungerGames.messages.message22);
				break;
			case "kits":
			case "kit":
				if(args.length < 3) {
					return false;
				}
				final String kitName = Utils.colorize(Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length)));
				if(args[1].equalsIgnoreCase("create")) {
					if(!commandSender.hasPermission("hungergames.kits.create")) {
						commandSender.sendMessage(HungerGames.messages.messagePermission);
						return true;
					}
					if(HungerGames.config.kitsList.get(kitName) != null) {
						commandSender.sendMessage(HungerGames.messages.message20);
						return true;
					}
					final List<String> items = new ArrayList<String>();
					final PlayerInventory inventory = player.getInventory();
					if(Utils.isInventoryEmpty(inventory, HungerGames.kitSelector)) {
						commandSender.sendMessage(HungerGames.messages.message23);
						return true;
					}
					ItemStack item;
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
				else if(args[1].equalsIgnoreCase("delete")) {
					if(!commandSender.hasPermission("hungergames.kits.delete")) {
						commandSender.sendMessage(HungerGames.messages.messagePermission);
						return true;
					}
					if(HungerGames.config.kitsList.get(kitName) == null) {
						commandSender.sendMessage(HungerGames.messages.message21);
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
					return false;
				}
				HungerGames.config.save();
				commandSender.sendMessage(HungerGames.messages.message22);
				break;
			case "join":
				if(!commandSender.hasPermission("hungergames.join")) {
					commandSender.sendMessage(HungerGames.messages.messagePermission);
					return true;
				}
				if(HungerGames.players.get(player) != null) {
					commandSender.sendMessage(HungerGames.messages.message2);
					return true;
				}
				if(!HungerGames.config.spectatorsEnable && (HungerGames.totalPlayers == HungerGames.config.gameMaxPlayers || (HungerGames.currentStep != Step.LOBBY && HungerGames.currentStep != Step.FIRST_COUNTDOWN))) {
					commandSender.sendMessage(HungerGames.messages.message1);
					return true;
				}
				boolean spectator = false;
				if(HungerGames.currentStep == Step.SECOND_COUNTDOWN || HungerGames.currentStep == Step.GAME) {
					player.sendMessage(HungerGames.messages.message12);
					spectator = true;
				}
				HungerGamesAPI.addPlayer(player, spectator);
				break;
			case "leave":
				if(HungerGames.config.gameDedicatedServer) {
					commandSender.sendMessage(HungerGames.messages.message24);
				}
				else {
					if(!commandSender.hasPermission("hungergames.leave")) {
						commandSender.sendMessage(HungerGames.messages.messagePermission);
						return true;
					}
					if(HungerGames.players.get(player) == null) {
						commandSender.sendMessage(HungerGames.messages.message6);
						return true;
					}
					player.sendMessage(HungerGames.messages.message11);
					HungerGamesAPI.removePlayer(player, false);
				}
				break;
			case "infos":
				if(!commandSender.hasPermission("hungergames.infos")) {
					commandSender.sendMessage(HungerGames.messages.messagePermission);
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
				commandSender.sendMessage(HungerGames.messages.message13.replaceAll("/n/", String.valueOf(HungerGames.players.size())).replaceAll("/players/", players).replaceAll("/map/", HungerGames.currentMap.getName()).replaceAll("/status/", HungerGamesAPI.getCurrentMotd()).replaceAll("/line-separator/", "\n"));
				break;
			case "winners":
				if(!commandSender.hasPermission("hungergames.winners")) {
					commandSender.sendMessage(HungerGames.messages.messagePermission);
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
					commandSender.sendMessage(HungerGames.messages.message18.replaceAll("/total-pages/", String.valueOf(pages)));
					return true;
				}
				commandSender.sendMessage(HungerGames.pages.getPage(page));
				break;
			default:
				return false;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.createReport(ex).report();
			commandSender.sendMessage(ex.getClass().getName());
		}
		return true;
	}
	
}
