package fr.skyost.hungergames.tasks;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.MobBarAPI;

public class Countdown extends BukkitRunnable {
	
	private final int originalTime;
	private int time;
	private final boolean expBarLevel;
	private final boolean mobBar;
	private final BukkitRunnable postExecute;
	
	private final Set<Player> players;
	private final MobBarAPI mobBarApi = MobBarAPI.getInstance();
	
	public Countdown(final int time, final boolean expBarLevel, final boolean mobBar, final BukkitRunnable postExecute) {
		this.originalTime = time;
		this.time = time;
		this.expBarLevel = expBarLevel;
		this.mobBar = mobBar;
		this.postExecute = postExecute;
		players = HungerGames.players.keySet();
		for(final Player player : players) {
			if(expBarLevel) {
				player.setLevel(time);
			}
			if(mobBar) {
				mobBarApi.setStatus(player, String.valueOf(time), 100, true);
			}
		}
	}

	@Override
	public void run() {
		time--;
		for(final Player player : players) {
			if(expBarLevel) {
				player.setLevel(time);
			}
			if(mobBar) {
				mobBarApi.setStatus(player, String.valueOf(time), (100 * time) / originalTime, true); // TODO: Fix, Mob bar does not disappears.
			}
		}
		if(time == 0) {
			HungerGames.tasks.set(0, -1);
			HungerGames.tasks.set(1, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, postExecute));
			this.cancel();
		}
	}
	
}
