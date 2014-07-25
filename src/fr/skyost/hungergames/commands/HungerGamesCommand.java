package fr.skyost.hungergames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HungerGamesCommand extends SubCommandsExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.GOLD + "Project HungerGames - By Skyost (http://www.skyost.eu)");
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}

}
