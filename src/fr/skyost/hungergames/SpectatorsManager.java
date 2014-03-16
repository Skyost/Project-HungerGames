package fr.skyost.hungergames;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Based on SpectatorFactory.
 * 
 * @author Original : <b>lenis0012</b>.
 * <br>Modified version : <b>Comphenix</b>.
 * <br>Spectators version : <b>Skyost</b>.
 */

public class SpectatorsManager {
	
	private static final String SPECTATORS_TEAM_NAME = "Spectators";
	private static final long UPDATE_DELAY = 5L;
	
	// No players in the ghost factory
	private static final OfflinePlayer[] EMPTY_PLAYERS = new OfflinePlayer[0];
	
	private Team spectatorsTeam;
	
	// Task that must be cleaned up
	private BukkitTask task;
	private boolean closed;
	
	/**
	 * Constructs a new instance of SpectatorsManager.
	 * 
	 * @param plugin The plugin which is used for the Scheduler.
	 */
	
	public SpectatorsManager(final Plugin plugin) {
		// Initialize
		createTask(plugin);
		createGetTeam();
	}
	
	private final void createGetTeam() {
		final Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		spectatorsTeam = board.getTeam(SPECTATORS_TEAM_NAME);
		// Create a new ghost team if needed
		if(spectatorsTeam == null) {
			spectatorsTeam = board.registerNewTeam(SPECTATORS_TEAM_NAME);
		}
		spectatorsTeam.setCanSeeFriendlyInvisibles(true);
	}
	
	private final void createTask(final Plugin plugin) {
		task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run() {
				for(final OfflinePlayer ghostPlayer : getSpectators()) {
					final Player player = ghostPlayer.getPlayer();
					if(player != null) {
						if(!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
						}
					}
					else {
						spectatorsTeam.removePlayer(ghostPlayer);
					}
				}
			}
			
		}, UPDATE_DELAY, UPDATE_DELAY);
	}
	
	public final void clearSpectators() {
		if(spectatorsTeam != null) {
			for(final OfflinePlayer ghostPlayer : getSpectators()) {
				spectatorsTeam.removePlayer(ghostPlayer);
				final Player player = ghostPlayer.getPlayer();
				if(player != null) {
					removeSpectator(player);
				}
			}
		}
	}
	
	public final void addSpectator(final Player player) {
		validateState();
		if(!spectatorsTeam.hasPlayer(player)) {
			spectatorsTeam.addPlayer(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			player.setAllowFlight(true);
		}
	}
	
	public final void addSpectator(final Player player, final boolean allowFlight) {
		validateState();
		if(!spectatorsTeam.hasPlayer(player)) {
			spectatorsTeam.addPlayer(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
			player.setAllowFlight(allowFlight);
		}
	}
	
	public final boolean hasSpectator(final Player player) {
		validateState();
		return spectatorsTeam.hasPlayer(player);
	}
	
	public final void removeSpectator(final Player player) {
		validateState();
		if(spectatorsTeam.removePlayer(player)) {
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.setAllowFlight(false);
		}
	}
	
	public final void removeSpectator(final Player player, boolean allowFlight) {
		validateState();
		if(spectatorsTeam.removePlayer(player)) {
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.setAllowFlight(allowFlight);
		}
	}
	
	public final OfflinePlayer[] getSpectators() {
		validateState();
		Set<OfflinePlayer> players = spectatorsTeam.getPlayers();
		if(players != null) {
			return players.toArray(new OfflinePlayer[0]);
		}
		else {
			return EMPTY_PLAYERS;
		}
	}
	
	public final void close() {
		if(!closed) {
			task.cancel();
			spectatorsTeam.unregister();
			closed = true;
		}
	}
	
	public final boolean isClosed() {
		return closed;
	}
	
	private final void validateState() {
		if(closed) {
			throw new IllegalStateException("SpectatorsManager has closed. Cannot reuse instances.");
		}
	}
	
}
