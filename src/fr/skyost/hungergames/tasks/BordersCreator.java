package fr.skyost.hungergames.tasks;

import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.WorldEditBorderCreator;

public class BordersCreator extends BukkitRunnable {
	
	private final BorderParams params;
	
	public BordersCreator(final BorderParams params) {
		this.params = params;
	}
	
	@Override
	public void run() {
		try {
			WorldEditBorderCreator.build(params);
			HungerGames.tasks.set(4, -1);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			HungerGames.logger.log(Level.SEVERE, "Error while generating borders, please check the stacktrace above.");
		}
	}
	
}
