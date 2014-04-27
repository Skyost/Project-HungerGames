package fr.skyost.hungergames.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.primitives.Ints;

import fr.skyost.hungergames.HungerGames;

public class Utils {
	
	/**
	 * Delete a file or a folder.
	 * 
	 * @param path The path of the file (or the folder).
	 * 
	 * @author Windcode.
	 */
	
	public static final void delete(final File path) {
		if(path.isDirectory()) {
			final String[] files = path.list();
			if(files.length == 0) {
				path.delete();
			}
			else {
				for(final String tmp : files) {
					File del = new File(path, tmp);
					delete(del);
				}
				if(path.list().length == 0) {
					path.delete();
				}
			}
		}
		else {
			path.delete();
		}
	}
	
	/**
	 * Copy a file or a folder.
	 * 
	 * @param sourceLocation The path of the file (or the folder).
	 * @param targetLocation The target.
	 * 
	 * @author Mkyong.
	 */
	
	public static final void copy(final File sourceLocation, final File targetLocation) throws IOException {
		if(sourceLocation.isDirectory()) {
			if(!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			final String[] children = sourceLocation.list();
			for(int i = 0; i < children.length; i++) {
				copy(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		}
		else {
			final InputStream in = new FileInputStream(sourceLocation);
			final OutputStream out = new FileOutputStream(targetLocation);
			final byte[] buf = new byte[1024];
			int len;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	/**
	 * <b>player.getTotalExperience()</b> sometimes reports the wrong XP due to a bug with enchanting not updating player's total XP.
	 * This function figures out the player's total XP based on their level and percentage to their next level. 
	 * 
	 * @param player
	 * @return The true total experience of a player.
	 * 
	 * @author Cyprias.
	 */
	
	public static final int getPlayerXP(final Player player) {
		final double userLevel = player.getLevel() + player.getExp();
		return safeLongToInt(Math.round((1.75D * Math.pow(userLevel, 2.0D) + 5.0D * userLevel)));
	}
	
	/**
	 * Used to cast a long to an int in a safely way.
	 * 
	 * @param l The long.
	 * @return An int.
	 * 
	 * @author Jon Skeet.
	 */
	
	public static final int safeLongToInt(final long l) {
		return Ints.checkedCast(l);
	}
	
	/**
	 * Used only in the goal to suppress the horrible annotation from the other code.
	 * 
	 * @param player The player.
	 */
	
	@SuppressWarnings("deprecation")
	public static final void updateInventory(final Player player) {
		player.updateInventory();
	}
	
	/**
	 * Used only in the goal to suppress the horrible annotation from the other code.
	 * 
	 * @param material The material.
	 */
	
	@SuppressWarnings("deprecation")
	public static final int getId(final Material material) {
		return material.getId();
	}
	
	/**
	 * Test if a String is numeric.
	 * 
	 * @param string The String to test.
	 * @return <b>true></b>If the String is numeric.
	 * <br><b>false</b> If the String is not numeric.
	 */
	
	public static final boolean isNumeric(final String string) {
		try {
			Integer.parseInt(string);
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}
	
	/**
	 * Get the ordinal suffix of a number.
	 * 
	 * @param i The number.
	 * @return The ordinal suffix.
	 * 
	 * @author Bohemian.
	 */
	
	public static final String getOrdinalSuffix(final int i) {
		switch(i % 100) {
		case 11:
		case 12:
		case 13:
			return HungerGames.messages.OrdinalSuffixes.get(0);
		default:
			return HungerGames.messages.OrdinalSuffixes.get(i % 10);
		}
	}
	
	/**
	 * Get a NMS class (without any import).
	 * 
	 * @param name The class name.
	 * @throws ClassNotFoundException If it cannot fiend the required class.
	 * @return The required class.
	 * 
	 * @author BigTeddy98.
	 */
	
	public static final Class<?> getMCClass(final String name) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + name);
	}
	
	/**
	 * Get a CraftBukkit class (without any import).
	 * 
	 * @param name The class name.
	 * @throws ClassNotFoundException If it cannot fiend the required class.
	 * @return The required class.
	 * 
	 * @author BigTeddy98.
	 */
	
	public static final Class<?> getCraftClass(final String name) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + name);
	}
	
	/**
	 * Attempt some workaround for experience orbs :
	 * prevent it getting near the player.
	 * @param target The targetted player.
	 * @param entity The entity.
	 * 
	 * @author asofold.
	 */
	
	public static final void repellExpOrb(final Player player, final ExperienceOrb entity) {
		final Location pLoc = player.getLocation();
		final Location oLoc = entity.getLocation();
		final Vector dir = oLoc.toVector().subtract(pLoc.toVector());
		final double dx = Math.abs(dir.getX());
		final double dz = Math.abs(dir.getZ());
		if((dx == 0.0) && (dz == 0.0)) {
			// Special case probably never happens
			dir.setX(0.001);
		}
		if((dx < 3.0) && (dz < 3.0)) {
			final Vector nDir = dir.normalize();
			final Vector newV = nDir.clone().multiply(0.3);
			newV.setY(0);
			entity.setVelocity(newV);
			if((dx < 1.0) && (dz < 1.0)) {
				// maybe oLoc
				entity.teleport(oLoc.clone().add(nDir.multiply(1.0)), TeleportCause.PLUGIN);
			}
			if((dx < 0.5) && (dz < 0.5)) {
				entity.remove();
			}
		}
	}
	
	/**
	 * Colorize a String.
	 * 
	 * @param in The String.
	 * @return The colorized String.
	 */
	
	public static final String colorize(final String in) {
		return (" " + in).replaceAll("([^\\\\](\\\\\\\\)*)&(.)", "$1§$3").replaceAll("([^\\\\](\\\\\\\\)*)&(.)", "$1§$3").replaceAll("(([^\\\\])\\\\((\\\\\\\\)*))&(.)", "$2$3&$5").replaceAll("\\\\\\\\", "\\\\").trim();
	}
	
	/**
	 * Decolorize a String.
	 * 
	 * @param in The String.
	 * @return The Decolorized String.
	 */
	
	public static final String decolorize(final String in) {
		return (" " + in).replaceAll("\\\\", "\\\\\\\\").replaceAll("&", "\\\\&").replaceAll("§", "&").trim();
	}
	
	/**
	 * Round a number to the next specified multiple.
	 * 
	 * @param number The number.
	 * @param value The multiple.
	 * @return The rounded int.
	 * 
	 * @author Arkia.
	 */
	
	public static final int round(final double number, final int value) {
		return (int)(Math.ceil(number / value) * value);
	}
	
	/**
	 * Used to check if an inventory is empty.
	 * 
	 * @param inventory The inventory.
	 * @return <b>true</b> If the specified inventory is empty.
	 * <br><b>false</b> If it not empty.
	 */
	
	public static final boolean isInventoryEmpty(final Inventory inventory) {
		for(final ItemStack item : inventory.getContents()) {
			if(item != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Used to check if an inventory is empty and exclude a specified item from verification.
	 * 
	 * @param inventory The inventory.
	 * @param exclude The item to exclude.
	 * @return <b>true</b> If the specified inventory is empty.
	 * <br><b>false</b> If it not empty.
	 */
	
	public static final boolean isInventoryEmpty(final Inventory inventory, final ItemStack exclude) {
		for(final ItemStack item : inventory.getContents()) {
			if(item != null && (!item.equals(exclude))) {
				return false;
			}
		}
		return true;
	}
	
}
