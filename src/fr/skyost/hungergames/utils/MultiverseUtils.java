package fr.skyost.hungergames.utils;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class MultiverseUtils {
	
	private final MultiverseCore multiverse;
	private final MVWorldManager manager;
	
	public MultiverseUtils(final Plugin multiverse) {
		this.multiverse = (MultiverseCore)multiverse;
		manager = this.multiverse.getMVWorldManager();
		manager.getDefaultWorldGenerators();
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
		return manager.deleteWorld(world);
	}
	
	public final World getWorld(final String world) {
		return manager.getMVWorld(world).getCBWorld();
	}
	
}
