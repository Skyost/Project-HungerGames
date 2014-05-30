package fr.skyost.hungergames.tasks;

import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.ErrorReport;
import fr.skyost.hungergames.utils.borders.BorderCreator;
import fr.skyost.hungergames.utils.borders.BorderParams;

public class BorderCreatorTask extends BukkitRunnable {
	
	private final BorderParams params;
	
	public BorderCreatorTask(final BorderParams params) {
		this.params = params;
	}
	
	@Override
	public void run() {
		try {
			new BorderCreator(params);
			HungerGames.tasks.set(4, -1);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
			HungerGames.logsManager.log("Error while generating borders, please check the stacktrace above.", Level.SEVERE);
		}
	}
	
}
