package fr.skyost.hungergames.utils.borders.types;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.borders.Border;
import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;

public class Void extends Border implements Listener {
	
	private int xMax;
	private int xMin;
	private int zMax;
	private int zMin;

	@Override
	protected void createBorder(final BorderParams params) throws WorldEditMaxChangedBlocksException {
		final int x = params.getX();
		final int z = params.getZ();
		final int radius = params.getRadius();
		xMax = x + radius;
		xMin = x - radius;
		zMax = z + radius;
		zMin = z - radius;
		Bukkit.getPluginManager().registerEvents(this, HungerGames.instance);
	}

	@Override
	public Type getType() {
		return Type.VOID;
	}

	@Override
	public String getDescription() {
		return "Makes a void border around the map.";
	}
	
	@EventHandler
	private final void onChunkLoad(final ChunkLoadEvent event) {
		final World world = event.getWorld();
		if(world != null && world.equals(HungerGames.currentMap)) { //We do not need to call params.getWorldName().
			final Chunk chunk = event.getChunk();
			final int x = chunk.getX();
			final int z = chunk.getZ();
			if(x > xMax || x < xMin || z > zMax || z < zMin) {
				chunk.unload(false, false);
			}
		}
	}
	
	@EventHandler
	private final void onCreatureSpawn(final CreatureSpawnEvent event) {
		final LivingEntity entity = event.getEntity();
		if(entity.getWorld().equals(HungerGames.currentMap)) {
			final Location location = entity.getLocation();
			final int x = location.getBlockX();
			final int z = location.getBlockZ();
			if(x > xMax || x < xMin || z > zMax || z < zMin) {
				event.setCancelled(true);
			}
		}
	}
	
}
