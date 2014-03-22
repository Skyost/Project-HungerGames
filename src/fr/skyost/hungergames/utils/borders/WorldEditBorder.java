package fr.skyost.hungergames.utils.borders;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;

public abstract class WorldEditBorder extends Border {
	
	/**
	 * WorldEdit sessions.
	 */
	
	public static final HashMap<String, LinkedList<EditSession>> sessions = new HashMap<String, LinkedList<EditSession>>();
	
	/**
	 * Create a new EditSession with the specified params.
	 * 
	 * @param params The border params.
	 */
	
	public static final EditSession newEditSession(final BorderParams params) {
		final String worldName = params.getWorldName();
		LinkedList<EditSession> linkedList;
		if(sessions.get(worldName) == null) {
			linkedList = new LinkedList<EditSession>();
			sessions.put(worldName, linkedList);
		}
		else {
			linkedList = sessions.get(worldName);
		}
		final EditSession session = new EditSession(new BukkitWorld(Bukkit.getWorld(worldName)), Integer.MAX_VALUE);
		linkedList.add(session);
		return session;
	}
	
	/**
	 * Undo WorldEdit borders for the specified world.
	 * 
	 * @param world The world.
	 * @return <b>true</b> If it is a success.
	 * <br><b>false</b> If an error occured.
	 */
	
	public static final boolean undoForWorld(final String world) {
		final LinkedList<EditSession> linkedList = sessions.get(world);
		if(linkedList == null || linkedList.size() == 0) {
			return false;
		}
		final EditSession last = linkedList.getLast();
		last.undo(last);
		linkedList.removeLast();
		return true;
	}
	
}
