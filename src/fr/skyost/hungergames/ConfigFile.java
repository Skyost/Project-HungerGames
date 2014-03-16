package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;

import fr.skyost.hungergames.HungerGames.Mode;
import fr.skyost.hungergames.utils.Config;

public class ConfigFile extends Config {
	
	public boolean EnableUpdater = true;
	public boolean EnableMetrics = true;
	
	public String Maps_Folder;
	public boolean Maps_Generate_Enable = false;
	public String Maps_Generate_Name = "generated_map";
	
	public String Lobby_World = "hungergames_lobby";
	public int Lobby_Countdown_Time = 30;
	public boolean Lobby_Countdown_ExpBarLevel = true;
	
	public int Game_MinPlayers = 2;
	public int Game_MaxPlayers = 8;
	public int Game_SpawnDistance = 200;
	public int Game_Random_Delay = 1000;
	public List<Material> Game_Random_Items = Arrays.asList(Material.GOLD_SWORD, Material.EMERALD, Material.COAL);
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
	
	public ConfigFile(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "config.yml");
		CONFIG_HEADER = "Project HungerGames by Skyost";
		
		Maps_Folder = new File(dataFolder + File.separator + "maps").getPath();
	}
	
}