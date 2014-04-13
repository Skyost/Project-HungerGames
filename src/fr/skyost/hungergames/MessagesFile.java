package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fr.skyost.hungergames.utils.Config;

public class MessagesFile extends Config {
	
	public int VERSION = 1;
	
	public String Messages_1 = "§cThe game is full or has already begun !";
	public String Messages_2 = "§cYou are already in the game !";
	public String Messages_3 = "§6The game starts in /n/ seconds.";
	public String Messages_4 = "Damages will be enabled in /n/ seconds.";
	public String Messages_5 = "§4Damages are enabled !";
	public String Messages_6 = "§cYou must be in the game !";
	public String Messages_7 = "§cThe game was cancelled because there is not the required amount of players.";
	public String Messages_8 = "§2You win !";
	public String Messages_9 = "§4You lose !";
	public String Messages_10 = "§7An item has spawned at §bX : /x/ Y : /y/ Z : /z/§7.";
	public String Messages_11 = "§dGoodbye you <3";
	public String Messages_12 = "§9You are a spectator now. You can leave the game by using /hg leave.";
	public String Messages_13 = "§6/n/ players : /players//line-separator/Map : /map//line-separator/Current status : /status/";
	public String Messages_14 = "§5(/n///n-max/) /player/ joined the lobby.";
	public String Messages_15 = "§aYou have won /n/ exp from /player/.";
	public String Messages_16 = "Winner of the /n//ordinal-suffix/ Hunger Games : /player/.";
	public String Messages_17 = "Page /n/ of /total-pages/. View the next page with /hg winners <page>./line-separator/The above list is scrollable.";
	public String Messages_18 = "§cPlease enter a number between 1 and /total-pages/.";
	public String Messages_19 = "§cYou cannot bypass the borders !";
	public String Messages_20 = "§cThis kit already exists !";
	public String Messages_21 = "§cThis kit does not exists !";
	public String Messages_22 = "§2Success !";
	public String Messages_23 = "§cYour inventory is empty or contains only the kit selector !";
	public String Messages_24 = "§cYou cannot leave the game !";
	
	public String Motds_1 = "§cWe need /n/ more player(s) to start.";
	public String Motds_2 = "§9The game starts in a few seconds. Join now !";
	public String Motds_3 = "§cThe game is started but damages are not enabled.";
	public String Motds_4 = "§4Game started. Damages enabled. /n/ players remaining.";
	
	public String PermissionMessage = "§cYou do not have the permission to perform this action !";
	
	public boolean RandomMessages_Enable = true;
	public int RandomMessages_Delay = 750;
	public List<String> RandomMessages_Messages = Arrays.asList("§2This plugin has been coded with love by Skyost (http://www.skyost.eu).", "Are you enjoying this game ?", "You can add (or remove) any message(s) you want in the configuration file ! Be sure to stop your server before editing it.");
	
	public List<String> DeathMessages = Arrays.asList("§4Grim Reaper has taken /player/.", "§4/player/ is out !", "§4/player/ is dead.");
	
	public List<String> OrdinalSuffixes = Arrays.asList("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th");

	public MessagesFile(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "messages.yml");
		CONFIG_HEADER = "Project HungerGames by Skyost";
	}
	
}