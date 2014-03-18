package fr.skyost.hungergames.utils.borders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import fr.skyost.hungergames.utils.borders.exceptions.BorderTypeNotFoundException;
import fr.skyost.hungergames.utils.borders.exceptions.WorldEditMaxChangedBlocksException;
import fr.skyost.hungergames.utils.borders.exceptions.WorldNotFoundException;
import fr.skyost.hungergames.utils.borders.types.Cylinder;
import fr.skyost.hungergames.utils.borders.types.Roofing;
import fr.skyost.hungergames.utils.borders.types.Square;

/**
 * World edit references from BorderCreator to go here and stop classdefnotfound
 * @author ghowden
 *
 */
public abstract class WorldEditBorderCreator {
	
	private static HashMap<String, LinkedList<EditSession>> sessions = new HashMap<String, LinkedList<EditSession>>();
	
	private static ArrayList<WorldEditBorder> types = new ArrayList<WorldEditBorder>();
	
	public static ArrayList<WorldEditBorder> getTypes() {
		return types;
	}
	
	public static void build(BorderParams bp) throws WorldEditMaxChangedBlocksException, WorldNotFoundException, BorderTypeNotFoundException {
		try {
			World w = Bukkit.getWorld(bp.getWorldName());
			if(w == null) {
				throw new WorldNotFoundException();
			}
			if(!sessions.containsKey(w.getName())) {
				sessions.put(w.getName(), new LinkedList<EditSession>());
			}
			WorldEditBorder web = getBorderByID(bp.getTypeID());
			if(web == null) {
				throw new BorderTypeNotFoundException();
			}
			LinkedList<EditSession> esl = sessions.get(w.getName());
			EditSession es = new EditSession(new BukkitWorld(w), Integer.MAX_VALUE);
			esl.add(es);
			web.createBorder(bp, es);
		}
		catch(MaxChangedBlocksException e) {
			throw new WorldEditMaxChangedBlocksException();
		}
	}
	
	public static List<String> getBorderIDs() {
		ArrayList<String> r = new ArrayList<String>();
		for(WorldEditBorder web : types) {
			r.add(web.getID());
		}
		return r;
	}
	
	public static WorldEditBorder getBorderByID(String id) {
		for(WorldEditBorder web : types) {
			if(web.getID().equalsIgnoreCase(id)) {
				return web;
			}
		}
		return null;
	}
	
	public static boolean undoForWorld(String world) {
		LinkedList<EditSession> es = sessions.get(world);
		if(es == null || es.size() == 0) {
			return false;
		}
		es.getLast().undo(es.getLast());
		es.removeLast();
		return true;
	}
	
	public static void initialize() {
		types.clear();
		types.add(new Cylinder());
		types.add(new Square());
		types.add(new Roofing());
	}
}
