package fr.skyost.hungergames.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

public class LogsManager {
	
	private Logger logger;
	private File logsFolder;
	
	private static final String DATE_HOUR = "HH:mm:ss";
	private static final String DATE_DAY = "yyyy-MM-dd";
	
	public enum DateType {
		HOUR,
		DAY;
	}
	
	public LogsManager() {}
	
	public LogsManager(final Plugin plugin) {
		logger = new PluginLogger(plugin);
		logsFolder = new File(plugin.getDataFolder() + File.separator + "logs");
		if(!logsFolder.exists()) {
			logsFolder.mkdir();
		}
	}
	
	public LogsManager(final Logger logger, final File logsFolder) {
		this.logger = logger;
		this.logsFolder = logsFolder;
		if(!logsFolder.exists()) {
			logsFolder.mkdir();
		}
	}
	
	public final void setLogger(final Logger logger) {
		this.logger = logger;
	}
	
	public final void setLogsFolder(final File logsFolder) {
		this.logsFolder = logsFolder;
		if(!logsFolder.exists()) {
			logsFolder.mkdir();
		}
	}
	
	public static final String date(final DateType date) {
		return new SimpleDateFormat(date == DateType.HOUR ? DATE_HOUR : DATE_DAY).format(new Date());
	}
	
	public final void log(final String message) {
		log(message, Level.INFO);
	}
	
	public void log(final String message, final Level level) {
		if(logger != null) {
			logger.log(level, message);
		}
		if(logsFolder != null) {
			try {
				final File dayFile = new File(logsFolder, date(DateType.DAY) + ".log");
				if(!dayFile.exists()) {
					dayFile.createNewFile();
				}
				final FileWriter fileWriter = new FileWriter(dayFile, true);
				final PrintWriter printWriter = new PrintWriter(fileWriter, true);
				printWriter.println("[" + date(DateType.HOUR) + "] [" + level + "] " + message);
				printWriter.close();
				fileWriter.close();
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
}
