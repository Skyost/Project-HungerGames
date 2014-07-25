package fr.skyost.hungergames.utils.hooks;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

import fr.skyost.hungergames.HungerGames;

public class MultiverseHook {
	
	private final MVWorldManager manager;
	
	public MultiverseHook(final Plugin multiverse) {
		manager = ((MultiverseCore)multiverse).getMVWorldManager();
	}
	
	public final boolean createWorld(final String world, final Environment environment, final long seed, final WorldType type, final boolean generateStructures, final String generator) {
		try {
			return manager.addWorld(world, environment, String.valueOf(seed), type, generateStructures, generator, false);
		}
		catch(IllegalArgumentException ex) {
			return true;
		}
	}
	
	public final boolean deleteWorld(final String world) {
		try {
			return manager.deleteWorld(world);
		}
		catch(final Exception ex) {
			HungerGames.logsManager.log("An error occured while deleting the world '" + world + "'. Maybe it has been deleted manually ?");
		}
		return false;
	}
	
	public final World getWorld(final String world) {
		return manager.getMVWorld(world).getCBWorld();
	}
	
}
