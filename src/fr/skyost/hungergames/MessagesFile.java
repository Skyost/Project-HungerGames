package fr.skyost.hungergames;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fr.skyost.hungergames.utils.Skyoconfig;

public class MessagesFile extends Skyoconfig {
	
	public int VERSION = 2;
	
	@ConfigOptions(name = "messages.1")
	public String message1 = "§cThe game is full or has already begun !";
	@ConfigOptions(name = "messages.2")
	public String message2 = "§cYou are already in the game !";
	@ConfigOptions(name = "messages.3")
	public String message3 = "§6The game starts in /n/ seconds.";
	@ConfigOptions(name = "messages.4")
	public String message4 = "Damages will be enabled in /n/ seconds.";
	@ConfigOptions(name = "messages.5")
	public String message5 = "§4Damages are enabled !";
	@ConfigOptions(name = "messages.6")
	public String message6 = "§cYou must be in the game !";
	@ConfigOptions(name = "messages.7")
	public String message7 = "§cThe game was cancelled because there is not the required amount of players.";
	@ConfigOptions(name = "messages.8")
	public String message8 = "§2You win !";
	@ConfigOptions(name = "messages.9")
	public String message9 = "§4You lose !";
	@ConfigOptions(name = "messages.10")
	public String message10 = "§7An item has spawned at §bX : /x/ Y : /y/ Z : /z/§7.";
	@ConfigOptions(name = "messages.11")
	public String message11 = "§dGoodbye you <3";
	@ConfigOptions(name = "messages.12")
	public String message12 = "§9You are a spectator now. You can leave the game by using /hg leave.";
	@ConfigOptions(name = "messages.13")
	public String message13 = "§6/n/ players : /players//line-separator/Map : /map//line-separator/Current status : /status/";
	@ConfigOptions(name = "messages.14")
	public String message14 = "§5(/n///n-max/) /player/ joined the lobby.";
	@ConfigOptions(name = "messages.15")
	public String message15 = "§aYou have won /n/ exp from /player/.";
	@ConfigOptions(name = "messages.16")
	public String message16 = "Winner of the /n//ordinal-suffix/ Hunger Games : /player/.";
	@ConfigOptions(name = "messages.17")
	public String message17 = "Page /n/ of /total-pages/. View the next page with /hg winners <page>./line-separator/The above list is scrollable.";
	@ConfigOptions(name = "messages.18")
	public String message18 = "§cPlease enter a number between 1 and /total-pages/.";
	@ConfigOptions(name = "messages.19")
	public String message19 = "§cYou cannot bypass the borders !";
	@ConfigOptions(name = "messages.20")
	public String message20 = "§cThis kit already exists !";
	@ConfigOptions(name = "messages.21")
	public String message21 = "§cThis kit does not exists !";
	@ConfigOptions(name = "messages.22")
	public String message22 = "§2Success !";
	@ConfigOptions(name = "messages.23")
	public String message23 = "§cYour inventory is empty or contains only the kit selector !";
	@ConfigOptions(name = "messages.24")
	public String message24 = "§cYou cannot leave the game !";
	@ConfigOptions(name = "messages.permission")
	public String messagePermission = "§cYou do not have the permission to perform this action !";
	
	@ConfigOptions(name = "motds.1")
	public String motd1 = "§cWe need /n/ more player(s) to start.";
	@ConfigOptions(name = "motds.2")
	public String motd2 = "§9The game starts in a few seconds. Join now !";
	@ConfigOptions(name = "motds.3")
	public String motd3 = "§cThe game is started but damages are not enabled.";
	@ConfigOptions(name = "motds.4")
	public String motd4 = "§4Game started. Damages enabled. /n/ players remaining.";
	
	@ConfigOptions(name = "random-messages.enable")
	public boolean randomMessagesEnable = true;
	@ConfigOptions(name = "random-messages.delay")
	public int randomMessagesDelay = 750;
	@ConfigOptions(name = "random-messages.messages")
	public List<String> randomMessagesMessages = Arrays.asList("§2This plugin has been coded with love by Skyost (http://www.skyost.eu).", "Are you enjoying this game ?", "You can add (or remove) any message(s) you want in the configuration file ! Be sure to stop your server before editing it.");
	
	@ConfigOptions(name = "death-messages")
	public List<String> deathMessages = Arrays.asList("§4Grim Reaper has taken /player/.", "§4/player/ is out !", "§4/player/ is dead.");
	
	@ConfigOptions(name = "ordinal-suffixes")
	public List<String> ordinalSuffixes = Arrays.asList("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th");

	public MessagesFile(final File dataFolder) {
		super(new File(dataFolder, "messages.yml"), Arrays.asList("Project HungerGames - By Skyost"));
	}
	
}