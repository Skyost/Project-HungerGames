package fr.skyost.hungergames.commands.subcommands.hungergames;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;

public class InfosSubCommand implements CommandInterface {

	@Override
	public final String[] names() {
		return new String[]{"infos"};
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
		return "infos";
	}
	
	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) {
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
		sender.sendMessage(HungerGames.messages.message13.replace("/n/", String.valueOf(HungerGames.players.size())).replace("/players/", players).replaceAll("/map/", HungerGames.currentMap.getName()).replace("/status/", HungerGamesAPI.getCurrentMotd()).replace("/line-separator/", "\n"));
		return true;
	}

}
