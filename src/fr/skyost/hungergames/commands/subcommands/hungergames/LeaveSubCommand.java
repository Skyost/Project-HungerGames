package fr.skyost.hungergames.commands.subcommands.hungergames;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;

public class LeaveSubCommand implements CommandInterface {

	@Override
	public final String[] names() {
		return new String[]{"leave"};
	}
	
	@Override
	public final boolean forcePlayer() {
		return true;
	}
	
	@Override
	public final String getPermission() {
		return "hungergames.leave";
	}
	
	@Override
	public final int getMinArgsLength() {
		return 0;
	}
	
	@Override
	public final String getUsage() {
		return "leave";
	}
	
	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) {
		if(HungerGames.config.gameDedicatedServer) {
			sender.sendMessage(HungerGames.messages.message24);
		}
		else {
			if(HungerGames.players.get((Player)sender) == null) {
				sender.sendMessage(HungerGames.messages.message6);
				return true;
			}
			sender.sendMessage(HungerGames.messages.message11);
			HungerGamesAPI.removePlayer((Player)sender, false);
		}
		return true;
	}

}
