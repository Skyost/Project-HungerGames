package fr.skyost.hungergames.commands.subcommands.hungergames;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;

public class SetLobbySubCommand implements CommandInterface {

	@Override
	public final String[] names() {
		return new String[]{"setlobby", "set-lobby"};
	}
	
	@Override
	public final boolean forcePlayer() {
		return true;
	}
	
	@Override
	public final String getPermission() {
		return "hungergames.lobby.set";
	}
	
	@Override
	public final int getMinArgsLength() {
		return 0;
	}
	
	@Override
	public final String getUsage() {
		return "setlobby";
	}
	
	@Override
	public final boolean onCommand(final CommandSender sender, final String[] args) throws InvalidConfigurationException {
		final Location location = ((Player)sender).getLocation();
		HungerGames.config.lobbyWorld = location.getWorld().getName();
		HungerGames.config.lobbySpawnX = location.getX();
		HungerGames.config.lobbySpawnY = location.getY();
		HungerGames.config.lobbySpawnZ = location.getZ();
		HungerGames.config.save();
		sender.sendMessage(HungerGames.messages.message22);
		return true;
	}

}
