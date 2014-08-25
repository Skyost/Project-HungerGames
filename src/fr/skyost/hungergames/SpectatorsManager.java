package fr.skyost.hungergames;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Based on GhostFactory.
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
	
	private List<Player> spectatorsList;
	private SpectatorsManagerMode mode;
	
	private Team spectatorsTeam;
	
	public enum SpectatorsManagerMode {
		GHOST_FACTORY,
		INVISIBLE_POTION;
	}
	
	/**
	 * Constructs a new instance of SpectatorsManager.
	 * 
	 * @param plugin The plugin which is used for the Scheduler.
	 */
	
	public SpectatorsManager(final Plugin plugin, final SpectatorsManagerMode mode) {
		this.mode = mode;
		if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
			// Initialize
			createTask(plugin);
			createGetTeam();
		}
		else {
			spectatorsList = new ArrayList<Player>();
		}
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
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run() {
				OfflinePlayer ghostPlayer;
				for(final Object objPlayer : getSpectators()) {
					ghostPlayer = (OfflinePlayer)objPlayer;
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
		if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
			if(spectatorsTeam != null) {
				OfflinePlayer ghostPlayer;
				for(final Object objPlayer : getSpectators()) {
					ghostPlayer = (OfflinePlayer)objPlayer;
					spectatorsTeam.removePlayer(ghostPlayer);
					final Player player = ghostPlayer.getPlayer();
					if(player != null) {
						removeSpectator(player);
					}
				}
			}
		}
		else {
			for(final Player player : spectatorsList) {
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			spectatorsList.clear();
		}
	}
	
	public final void addSpectator(final Player player) {
		if(!spectatorsTeam.hasPlayer(player)) {
			if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
				spectatorsTeam.addPlayer(player);
			}
			else {
				spectatorsList.add(player);
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
		}
	}
	
	public final boolean hasSpectator(final Player player) {
		if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
			return spectatorsTeam.hasPlayer(player);
		}
		return spectatorsList.contains(player);
	}
	
	public final void removeSpectator(final Player player) {
		if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
			if(!spectatorsTeam.removePlayer(player)) {
				return;
			}
		}
		else {
			spectatorsList.remove(player);
		}
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}
	
	public final Object[] getSpectators() {
		if(mode == SpectatorsManagerMode.GHOST_FACTORY) {
			Set<OfflinePlayer> players = spectatorsTeam.getPlayers();
			if(players != null) {
				return players.toArray(new OfflinePlayer[0]);
			}
			else {
				return EMPTY_PLAYERS;
			}
		}
		else {
			return spectatorsList.toArray();
		}
	}
	
}
