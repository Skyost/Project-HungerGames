package fr.skyost.hungergames.tasks;

import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.ErrorSender;
import fr.skyost.hungergames.utils.borders.BorderParams;
import fr.skyost.hungergames.utils.borders.BorderCreator;

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
			ErrorSender.uploadAndSend(ex);
			HungerGames.logsManager.log("Error while generating borders, please check the stacktrace above.", Level.SEVERE);
		}
	}
	
}
