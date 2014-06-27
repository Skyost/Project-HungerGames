package fr.skyost.hungergames.commands.subcommands.hungergames;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;

public class JoinSubCommand implements CommandInterface {

	@Override
	public final String[] names() {
		return new String[]{"join"};
	}
	
	@Override
	public final boolean forcePlayer() {
		return true;
	}
	
	@Override
	public final String getPermission() {
		return "hungergames.join";
	}
	
	@Override
	public final int getMinArgsLength() {
		return 0;
	}
	
	@Override
	public final String getUsage() {
		return "join";
	}
	
	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) {
		if(HungerGames.players.get((Player)sender) != null) {
			sender.sendMessage(HungerGames.messages.message2);
			return true;
		}
		if(!HungerGames.config.spectatorsEnable && (HungerGames.totalPlayers == HungerGames.config.gameMaxPlayers || (HungerGames.currentStep != Step.LOBBY && HungerGames.currentStep != Step.FIRST_COUNTDOWN))) {
			sender.sendMessage(HungerGames.messages.message1);
			return true;
		}
		boolean spectator = false;
		if(HungerGames.currentStep == Step.SECOND_COUNTDOWN || HungerGames.currentStep == Step.GAME) {
			sender.sendMessage(HungerGames.messages.message12);
			spectator = true;
		}
		HungerGamesAPI.addPlayer((Player)sender, spectator);
		return true;
	}

}
