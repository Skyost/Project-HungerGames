package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import fr.skyost.hungergames.SpectatorsManager.SpectatorsManagerMode;
import fr.skyost.hungergames.utils.Skyoconfig;
import fr.skyost.hungergames.utils.borders.Border.Type;

public class PluginConfig extends Skyoconfig {
	
	public int VERSION = 3;
	
	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;
	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	
	@ConfigOptions(name = "bungee.server-name")
	public String bungeeServerName = "srv001";
	
	@ConfigOptions(name = "bugs-report.enable")
	public boolean bugsReportEnable = true;
	@ConfigOptions(name = "bugs-report.your-name")
	public String bugsReportName = Bukkit.getServerName();
	@ConfigOptions(name = "bugs-report.your-mail")
	public String bugsReportMail = "your@mail.com";
	
	@ConfigOptions(name = "maps.folder")
	public String mapsFolder;
	@ConfigOptions(name = "maps.game-rules")
	public HashMap<String, String> mapsGameRules = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L; {
			put("naturalRegeneration", "false");
		}
	};
	@ConfigOptions(name = "maps.default-time")
	public int mapsDefaultTime = 0;
	@ConfigOptions(name = "maps.generate.enable")
	public boolean mapsGenerateEnable = false;
	@ConfigOptions(name = "maps.generate.map-name")
	public String mapsGenerateName = "generated_map";
	@ConfigOptions(name = "maps.borders.enable")
	public boolean mapsBordersEnable = true;
	@ConfigOptions(name = "maps.borders.radius")
	public int mapsBordersRadius = 1000;
	@ConfigOptions(name = "maps.borders.type")
	public Type mapsBordersType = Type.INVISIBLE;
	@ConfigOptions(name = "maps.borders.material")
	public Material mapsBordersMaterial = Material.BEDROCK;
	@ConfigOptions(name = "maps.borders.meta")
	public int mapsBordersMeta = 0;
	
	@ConfigOptions(name = "lobby.world")
	public String lobbyWorld = "hungergames_lobby";
	@ConfigOptions(name = "lobby.spawn.x")
	public double lobbySpawnX = 0;
	@ConfigOptions(name = "lobby.spawn.y")
	public double lobbySpawnY = 0;
	@ConfigOptions(name = "lobby.spawn.z")
	public double lobbySpawnZ = 0;
	@ConfigOptions(name = "lobby.countdown.time")
	public int lobbyCountdownTime = 30;
	@ConfigOptions(name = "lobby.countdown.exp-bar-level")
	public boolean lobbyCountdownExpBarLevel = true;
	@ConfigOptions(name = "lobby.countdown.mob-bar")
	public boolean lobbyCountdownMobBar = false;
	@ConfigOptions(name = "lobby.protect")
	public boolean lobbyProtect = false;
	
	@ConfigOptions(name = "game.dedicated-server")
	public boolean gameDedicatedServer = false;
	@ConfigOptions(name = "game.min-players")
	public int gameMinPlayers = 2;
	@ConfigOptions(name = "game.max-players")
	public int gameMaxPlayers = 8;
	@ConfigOptions(name = "game.spawn-distance")
	public int gameSpawnDistance = 200;
	@ConfigOptions(name = "game.auto-sneak")
	public boolean gameAutoSneak = true;
	@ConfigOptions(name = "game.compass-nearest-player")
	public boolean gameCompassNearestPlayer = false;
	@ConfigOptions(name = "game.death-name-sky")
	public boolean gameDeathNameSky = true;
	@ConfigOptions(name = "game.random-items.delay")
	public int gameRandomItemsDelay = 1000;
	@ConfigOptions(name = "game.random-items.directory")
	public String gameRandomItemsDirectory;
	@ConfigOptions(name = "game.random-items.chests")
	public boolean gameRandomItemsChests = true;
	@ConfigOptions(name = "game.random-items.distance")
	public int gameRandomItemsDistance = 100;
	@ConfigOptions(name = "game.random-items.thundering")
	public boolean gameRandomItemsThundering = true;
	@ConfigOptions(name = "game.motd-change")
	public boolean gameMotdChange = false;
	@ConfigOptions(name = "game.countdown.time")
	public int gameCountdownTime = 60;
	@ConfigOptions(name = "game.countdown.exp-bar-level")
	public boolean gameCountdownExpBarLevel = true;
	@ConfigOptions(name = "game.countdown.mob-bar")
	public boolean gameCountdownMobBar = false;
	@ConfigOptions(name = "game.death-sound.sound")
	public Sound gameDeathSoundSound = Sound.WITHER_SPAWN;
	@ConfigOptions(name = "game.death-sound.volume")
	public float gameDeathSoundVolume = 1f;
	@ConfigOptions(name = "game.death-sound.pitch")
	public float gameDeathSoundPitch = 0.75f;
	@ConfigOptions(name = "game.rewards.directory")
	public String gameRewardsDirectory;
	@ConfigOptions(name = "game.rewards.enable")
	public boolean gameRewardsEnable = true;
	@ConfigOptions(name = "game.blood.players")
	public boolean gameBloodPlayers = true;
	@ConfigOptions(name = "game.blood.mobs")
	public boolean gameBloodMobs = true;
	
	@ConfigOptions(name = "spectators.enable")
	public boolean spectatorsEnable = true;
	@ConfigOptions(name = "spectators.mode")
	public SpectatorsManagerMode spectatorsMode = SpectatorsManagerMode.GHOST_FACTORY;
	@ConfigOptions(name = "spectators.permissions.chat")
	public boolean spectatorsPermissionsChat = false;
	@ConfigOptions(name = "spectators.permissions.pickup-items")
	public boolean spectatorsPermissionsPickupItems = false;
	@ConfigOptions(name = "spectators.permissions.interact")
	public boolean spectatorsPermissionsInteract = false;
	
	@ConfigOptions(name = "kits.selector.name")
	public String kitsSelectorName = "§6Select a kit !";
	@ConfigOptions(name = "kits.selector.material")
	public Material kitsSelectorMaterial = Material.NETHER_STAR;
	@ConfigOptions(name = "kits.directory")
	public String kitsDirectory;
	@ConfigOptions(name = "kits.permissions")
	public boolean kitsPermissions = true;
	
	@ConfigOptions(name = "logs.console")
	public boolean logConsole = true;
	@ConfigOptions(name = "logs.file.enable")
	public boolean logFileEnable = false;
	@ConfigOptions(name = "logs.file.directory")
	public String logFileDirectory;
	
	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("Project HungerGames - By Skyost", "A documentation is available here : http://url.skyost.eu/caF."));
		mapsFolder = new File(dataFolder + File.separator + "maps").getPath();
		logFileDirectory = new File(dataFolder + File.separator + "logs").getPath();
		final File configData = new File(dataFolder + File.separator + "config-data");
		if(!configData.exists()) {
			configData.mkdir();
		}
		gameRandomItemsDirectory =  new File(configData + File.separator + "random-items").getPath();
		gameRewardsDirectory = new File(configData + File.separator + "rewards").getPath();
		kitsDirectory = new File(configData + File.separator + "kits").getPath();
	}
	
}