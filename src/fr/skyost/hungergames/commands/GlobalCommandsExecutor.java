package fr.skyost.hungergames.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.ErrorReport;

public class GlobalCommandsExecutor extends SubCommandsExecutor {
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
		try {
			if(args.length <= 0) {
				return false;
			}
			final CommandInterface commandInterface = this.getExecutor(args[0]);
			if(commandInterface == null) {
				return false;
			}
			if(commandInterface.forcePlayer() && !(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Please perform this command from the game !");
				return true;
			}
			final String permission = commandInterface.getPermission();
			if(permission != null && !sender.hasPermission(permission)) {
				sender.sendMessage(HungerGames.messages.messagePermission);
				return true;
			}
			args = Arrays.copyOfRange(args, 1, args.length);
			if(args.length < commandInterface.getMinArgsLength()) {
				sender.sendMessage(ChatColor.RED + command.getName() + " " + commandInterface.getUsage());
				return true;
			}
			return commandInterface.onCommand(sender, args);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
		}
		return false;
	}

}
