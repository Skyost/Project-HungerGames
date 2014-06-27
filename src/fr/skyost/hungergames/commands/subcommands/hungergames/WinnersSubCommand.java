package fr.skyost.hungergames.commands.subcommands.hungergames;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;

public class WinnersSubCommand implements CommandInterface {

	@Override
	public final String[] names() {
		return new String[]{"winners"};
	}
	
	@Override
	public final boolean forcePlayer() {
		return false;
	}
	
	@Override
	public final String getPermission() {
		return "hungergames.infos";
	}
	
	@Override
	public final int getMinArgsLength() {
		return 0;
	}
	
	@Override
	public final String getUsage() {
		return "infos [page]";
	}
	
	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) throws InvalidConfigurationException {
		if(!sender.hasPermission("hungergames.winners")) {
			sender.sendMessage(HungerGames.messages.messagePermission);
			return true;
		}
		if(HungerGames.winnersMap.size() == 0) {
			sender.sendMessage(ChatColor.RED + "There is not any winner !");
			return true;
		}
		final int page;
		if(args.length >= 2) {
			if(CharMatcher.DIGIT.matchesAllOf(args[1])) {
				page = Integer.parseInt(args[1]);
			}
			else {
				sender.sendMessage(ChatColor.RED + this.getUsage());
				return true;
			}
		}
		else {
			page = 1;
		}
		final int pages = HungerGames.pages.getTotalPages();
		if(page < 1 || page > pages) {
			sender.sendMessage(HungerGames.messages.message18.replace("/total-pages/", String.valueOf(pages)));
			return true;
		}
		sender.sendMessage(HungerGames.pages.getPage(page));
		return true;
	}

}
