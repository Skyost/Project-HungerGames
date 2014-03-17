package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;

import fr.skyost.hungergames.HungerGames.Mode;
import fr.skyost.hungergames.utils.Config;

public class ConfigFile extends Config {
	
	public boolean EnableUpdater = true;
	public boolean EnableMetrics = true;
	
	public String Maps_Folder;
	public boolean Maps_Generate_Enable = false;
	public String Maps_Generate_Name = "generated_map";
	public boolean Maps_Limit_Enable = true;
	public int Maps_Limit_Size = 1000;
	
	public String Lobby_World = "hungergames_lobby";
	public double Lobby_Spawn_X;
	public double Lobby_Spawn_Y;
	public double Lobby_Spawn_Z;
	public int Lobby_Countdown_Time = 30;
	public boolean Lobby_Countdown_ExpBarLevel = true;
	
	public int Game_MinPlayers = 2;
	public int Game_MaxPlayers = 8;
	public int Game_SpawnDistance = 200;
	public boolean Game_AutoSneak = true;
	public int Game_Random_Delay = 1000;
	public List<List<String>> Game_Random_Items = Arrays.asList(Arrays.asList(Material.GOLD_SWORD.name(), "§6Gold sword", "§oCan be useful.", Enchantment.DAMAGE_ALL.getName(), String.valueOf(Enchantment.DAMAGE_ALL.getMaxLevel())), Arrays.asList(Material.EMERALD.name(), "§aEmerald", "§oI know you love it too."), Arrays.asList(Material.COAL.name()));
	public boolean Game_Random_Chests = true;
	public int Game_Random_Distance = 100;
	public boolean Game_Random_Thundering = true;
	public boolean Game_Motd_Change = false;
	public int Game_Countdown_Time = 60;
	public boolean Game_Countdown_ExpBarLevel = true;
	public Sound Game_DeathSound_Sound = Sound.WITHER_SPAWN;
	public String Game_DeathSound_Volume = "1";
	public String Game_DeathSound_Pitch = "0.75";
	
	public boolean Spectators_Enable = true;
	public Mode Spectators_Mode = Mode.GHOST_FACTORY;
	public boolean Spectators_Permissions_Chat = false;
	public boolean Spectators_Permissions_PickupItems = false;
	public boolean Spectators_Permissions_Interact = false;
	
	public boolean Log_Console = true;
	
	public ConfigFile(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "config.yml");
		CONFIG_HEADER = "Project HungerGames by Skyost";
		
		Maps_Folder = new File(dataFolder + File.separator + "maps").getPath();
		
		HungerGames.lobby = Bukkit.getWorld(Lobby_World);
		if(HungerGames.lobby == null) {
			HungerGames.lobby = Bukkit.createWorld(new WorldCreator(Lobby_World));
		}
		final Location spawn = HungerGames.lobby.getSpawnLocation();
		Lobby_Spawn_X = spawn.getX();
		Lobby_Spawn_Y = spawn.getY();
		Lobby_Spawn_Z = spawn.getZ();
	}
	
}