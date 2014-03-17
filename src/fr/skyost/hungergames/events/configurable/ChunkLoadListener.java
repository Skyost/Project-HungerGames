package fr.skyost.hungergames.events.configurable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import fr.skyost.hungergames.HungerGames;

public class ChunkLoadListener implements Listener {
	
	private final int SPAWN_X;
	private final int SPAWN_Z;
	
	private final int SPAWN_X_PLUS_LIMIT;
	private final int SPAWN_Z_PLUS_LIMIT;
	
	private final int SPAWN_X_LESS_LIMIT;
	private final int SPAWN_Z_LESS_LIMIT;
	
	public ChunkLoadListener() {
		final Location spawn = HungerGames.currentMap.getSpawnLocation();
		SPAWN_X = spawn.getBlockX();
		SPAWN_Z = spawn.getBlockZ();
		final int limit = HungerGames.config.Maps_Limit_Size / 2;
		SPAWN_X_PLUS_LIMIT = SPAWN_X + limit;
		SPAWN_Z_PLUS_LIMIT = SPAWN_Z + limit;
		SPAWN_X_LESS_LIMIT = SPAWN_X - limit;
		SPAWN_Z_LESS_LIMIT = SPAWN_Z - limit;
	}
	
	@EventHandler
	private final void onChunkLoad(final ChunkLoadEvent event) {
		final Chunk chunk = event.getChunk();
		if(chunk.getWorld().equals(HungerGames.currentMap)) {
			final int x = chunk.getX();
			final int z = chunk.getZ();
			if(x + SPAWN_X >= SPAWN_X_PLUS_LIMIT || x - SPAWN_X <= SPAWN_X_LESS_LIMIT || z + SPAWN_Z >= SPAWN_Z_PLUS_LIMIT || z - SPAWN_Z <= SPAWN_Z_LESS_LIMIT) {
				chunk.unload(false, false);
			}
		}
	}
	
}
