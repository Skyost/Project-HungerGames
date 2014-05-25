package fr.skyost.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.skyost.hungergames.utils.Skyoconfig;

public class WinnersFile extends Skyoconfig {
	
	public int VERSION = 2;
	
	public List<String> winners = new ArrayList<String>();

	public WinnersFile(final File dataFolder) {
		super(new File(dataFolder, "winners.yml"), Arrays.asList("Project HungerGames - By Skyost"));
	}
	
}