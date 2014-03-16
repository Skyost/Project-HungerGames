package fr.skyost.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import fr.skyost.hungergames.utils.Config;

public class WinnersFile extends Config {
	
	public List<String> Winners = new ArrayList<String>();

	public WinnersFile(final File dataFolder) {
		CONFIG_FILE = new File(dataFolder, "winners.yml");
		CONFIG_HEADER = "Project HungerGames by Skyost";
	}
	
}