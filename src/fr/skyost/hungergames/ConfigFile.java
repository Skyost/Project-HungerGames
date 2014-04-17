package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;

import fr.skyost.hungergames.SpectatorsManager.SpectatorsManagerMode;
import fr.skyost.hungergames.utils.Config;
import fr.skyost.hungergames.utils.JsonItemStack;
import fr.skyost.hungergames.utils.borders.Border.Type;

public class ConfigFile extends Config {
	
	public int VERSION = 2;
	
	public boolean EnableUpdater = true;
	public boolean EnableMetrics = true;
	
	public String Bungee_ServerName = "srv001";
	
	public boolean BugsReport_Enable = true;
	public String BugsReport_Name = "My Name";
	public String BugsReport_Mail = "your@mail.com";
	
	public String Maps_Folder;
	public HashMap<String, String> Maps_GameRules = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("naturalRegeneration", "false");
		}
	};
	public int Maps_DefaultTime = 0;
	public boolean Maps_Generate_Enable = false;
	public String Maps_Generate_Name = "generated_map";
	public boolean Maps_Borders_Enable = true;
	public int Maps_Borders_Radius = 1000;
	public Type Maps_Borders_Type = Type.INVISIBLE;
	public Material Maps_Borders_Material = Material.BEDROCK;
	public int Maps_Borders_Meta = 0;
	
	public String Lobby_World = "hungergames_lobby";
	public double Lobby_Spawn_X;
	public double Lobby_Spawn_Y;
	public double Lobby_Spawn_Z;
	public int Lobby_Countdown_Time = 30;
	public boolean Lobby_Countdown_ExpBarLevel = true;
	public boolean Lobby_Protect = false;
	
	public boolean Game_DedicatedServer = false;
	public int Game_MinPlayers = 2;
	public int Game_MaxPlayers = 8;
	public int Game_SpawnDistance = 200;
	public boolean Game_AutoSneak = true;
	public int Game_RandomItems_Delay = 1000;
	public HashMap<String, String> Game_RandomItem_Items = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("10", new JsonItemStack(Material.GOLD_SWORD.name(), "§6Gold sword", "§oCan be useful.", Enchantment.DAMAGE_ALL.getName(), Long.valueOf(Enchantment.DAMAGE_ALL.getMaxLevel()), null).toJson());
			put("20", new JsonItemStack(Material.EMERALD.name(), "§aEmerald", "§oI know you love it too.", null, null, null).toJson());
			put("50", new JsonItemStack(Material.COAL.name(), null, Arrays.asList("For Christmas.", "- Mom"), null, Long.valueOf(5)).toJson());
		}
	};
	public boolean Game_RandomItems_Chests = true;
	public int Game_RandomItems_Distance = 100;
	public boolean Game_RandomItems_Thundering = true;
	public boolean Game_Motd_Change = false;
	public int Game_Countdown_Time = 60;
	public boolean Game_Countdown_ExpBarLevel = true;
	public Sound Game_DeathSound_Sound = Sound.WITHER_SPAWN;
	public String Game_DeathSound_Volume = "1";
	public String Game_DeathSound_Pitch = "0.75";
	public HashMap<String, String> Game_Rewards = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("1", new JsonItemStack(Material.GOLD_INGOT.name(), "§6Congracubations !", null, null, null, Long.valueOf(3)).toJson());
		}
	};
	public boolean Game_Rewards_Enable = true;
	
	public boolean Spectators_Enable = true;
	public SpectatorsManagerMode Spectators_Mode = SpectatorsManagerMode.GHOST_FACTORY;
	public boolean Spectators_Permissions_Chat = false;
	public boolean Spectators_Permissions_PickupItems = false;
	public boolean Spectators_Permissions_Interact = false;
	
	public String Kits_Selector_Name = "§6Select a kit !";
	public Material Kits_Selector_Material = Material.NETHER_STAR;
	public HashMap<String, List<String>> Kits_List = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L; {
			put("§7Iron", Arrays.asList(new JsonItemStack(Material.IRON_INGOT.name(), null, null, null, null).toJson(), new JsonItemStack(Material.IRON_HELMET.name(), null, null, null, null).toJson(), new JsonItemStack(Material.IRON_CHESTPLATE.name(), null, null, null, null).toJson(), new JsonItemStack(Material.IRON_LEGGINGS.name(), null, null, null, null).toJson(), new JsonItemStack(Material.IRON_BOOTS.name(), null, null, null, null).toJson()));
		}
	};
	public boolean Kits_Permissions = false;
	
	public boolean Log_Console = true;
	public boolean Log_File_Enable = false;
	public String Log_File_Directory;
	
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
		
		Log_File_Directory = new File(dataFolder + File.separator + "logs").getPath();
	}
	
}