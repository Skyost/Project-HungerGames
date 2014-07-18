package fr.skyost.hungergames.events;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;

public class WorldListener implements Listener {
	
	@EventHandler
	private final void onChunkUnload(final ChunkUnloadEvent event) {
		final Chunk chunk = event.getChunk();
		if(HungerGames.generatedChunks.contains(chunk)) {
			if(HungerGames.currentStep == Step.GAME) {
				HungerGames.generatedChunks.remove(chunk);
			}
			else {
				event.setCancelled(true);
			}
		}
	}
	
}
