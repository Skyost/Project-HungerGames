package fr.skyost.hungergames.tasks;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import fr.skyost.hungergames.HungerGames;

public class Countdown extends BukkitRunnable {
	
	private int time;
	private final boolean expBarLevel;
	private final BukkitRunnable postExecute;
	private final Set<Player> players;
	
	public Countdown(final int time, final boolean expBarLevel, final BukkitRunnable postExecute) {
		this.time = time;
		this.expBarLevel = expBarLevel;
		this.postExecute = postExecute;
		players = HungerGames.players.keySet();
		if(expBarLevel) {
			setExpLevel(time);
		}
	}

	@Override
	public void run() {
		time--;
		if(expBarLevel) {
			setExpLevel(time);
		}
		if(time == 0) {
			HungerGames.tasks.set(0, -1);
			HungerGames.tasks.set(1, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, postExecute));
			this.cancel();
		}
	}
	
	private final void setExpLevel(final int level) {
		for(final Player player : players) {
			player.setLevel(level);
		}
	}
	
}
