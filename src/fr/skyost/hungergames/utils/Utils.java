package fr.skyost.hungergames.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

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
		return Ints.checkedCast(Math.round((1.75D * Math.pow(userLevel, 2.0D) + 5.0D * userLevel)));
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
			return HungerGames.messages.ordinalSuffixes.get(0);
		default:
			return HungerGames.messages.ordinalSuffixes.get(i % 10);
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
		if(number <= 0) {
			return value;
		}
		return (int)Math.ceil(number / value) * value;
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
	
	/**
	* Gets the Minecraft server version.
	* 
	* @return The <b>Minecraft server version</b>.
	*/
	
	public static final String getMinecraftServerVersion() {
		final String version = Bukkit.getVersion().split("\\(MC\\: ")[1];
		return version.substring(0, version.length() - 1);
	}
	
	/**
	 * Check if the given String is an enum.
	 * 
	 * @param enumClass The enum class.
	 * @param test The specified String.
	 * @return <b>true</b> If it is an enum of the selected type.
	 * <b>false</b> Otherwise.
	 */
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final boolean isEnum(final Class<? extends Enum> enumClass, final String test) {
		try {
			Enum.valueOf(enumClass, test);
			return true;
		}
		catch(final IllegalArgumentException ex) {}
		return false;
	}
	
	public static final String getFileContent(final File file, final String lineSeparator) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		final StringBuilder builder = new StringBuilder();
		try {
			String line = reader.readLine();
			while(line != null) {
				builder.append(line);
				if(lineSeparator != null) {
					builder.append(lineSeparator);
				}
				line = reader.readLine();
			}
		}
		finally {
			reader.close();
		}
		return builder.toString();
	}
	
	public static final void writeToFile(final File file, final String content) throws IOException {
		if(!file.exists()) {
			file.createNewFile();
		}
		final FileWriter fileWriter = new FileWriter(file, false);
		final PrintWriter printWriter = new PrintWriter(fileWriter, true);
		printWriter.println(content);
		printWriter.close();
		fileWriter.close();
	}
	
}
