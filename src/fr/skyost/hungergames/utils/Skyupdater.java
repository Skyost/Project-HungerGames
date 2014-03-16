package fr.skyost.hungergames.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Joiner;

/**
 * A simple auto-updater.
 * <br>Please follow this link to read more about checking for updates in your plugin : http://url.skyost.eu/3.
 * <br><br>Thanks to Gravity for his updater (this file use some parts of his code) !
 * 
 * @author Skyost
 */

public class Skyupdater {
	
	private Plugin plugin;
	private File pluginFile;
	private Logger logger;
	private int id;
	private boolean download;
	private boolean announce;
	private boolean isEnabled = true;
	
	private String apiKey;
	private URL url;
	private File skyupdaterFolder;
	private File updateFolder;
	private Result result = Result.SUCCESS;
	private String[] updateData;
	private String response;
	private Thread updaterThread;
	
	private static final String SKYUPDATER_VERSION = "0.3.5";
	
	public enum Result {
		
		/**
		 * A new version has been found, downloaded and will be loaded at the next server reload / restart.
		 */
		
		SUCCESS,
		
		/**
		 * A new version has been found but nothing was downloaded.
		 */
		
		UPDATE_AVAILABLE,
		
		/**
		 * No update found.
		 */
		
		NO_UPDATE,
		
		/**
		 * The updater is disabled.
		 */
		
		DISABLED,
		
		/**
		 * An error occured.
		 */
		
		ERROR;
	}
	
	public enum InfoType {
		
		/**
		 * Get the download URL.
		 */
		
		DOWNLOAD_URL,
		
		/**
		 * Get the file name.
		 */
		
		FILE_NAME,
		
		/**
		 * Get the game version.
		 */
		
		GAME_VERSION,
		
		/**
		 * Get the file title.
		 */
		
		FILE_TITLE,
		
		/**
		 * Get the release type.
		 */
		
		RELEASE_TYPE;
	}
	
	/**
	 * Initialize Skyupdater.
	 * 
	 * @param plugin Your plugin.
	 * @param id Your plugin ID on BukkitDev (you can get it here : https://api.curseforge.com/servermods/projects?search=your+plugin).
	 * @param pluginFile The plugin file.
	 * @param download If you want to download the file.
	 * @param announce If you want to announce the progress of the Updater.
	 * @throws IOException InputOutputException.
	 */
	
	public Skyupdater(final Plugin plugin, final int id, final File pluginFile, final boolean download, final boolean announce) throws IOException {
		this.plugin = plugin;
		this.id = id;
		this.pluginFile = pluginFile;
		this.download = download;
		this.announce = announce;
		final Server server = plugin.getServer();
		logger = server.getLogger();
		updateFolder = server.getUpdateFolderFile();
		if(!updateFolder.exists()) {
			updateFolder.mkdir();
		}
		skyupdaterFolder = new File(plugin.getDataFolder().getParentFile() + System.getProperty("file.separator", "/") + "Skyupdater");
		if(!skyupdaterFolder.exists()) {
			skyupdaterFolder.mkdir();
		}
		final File propertiesFile = new File(skyupdaterFolder, "skyupdater.properties");
		final Properties config = new Properties();
		if(propertiesFile.exists()) {
			config.load(new FileInputStream(propertiesFile));
			apiKey = config.getProperty("api-key", "NONE");
			if(apiKey.equalsIgnoreCase("NONE") || apiKey.length() == 0) {
				apiKey = null;
			}
			isEnabled = Boolean.valueOf(config.getProperty("enable", "true"));
			if(!isEnabled) {
				result = Result.DISABLED;
				if(announce) {
					logger.log(Level.INFO, "[Skyupdater] Skyupdater is disabled.");
				}
			}
		}
		else {
			final String lineSeparator = System.lineSeparator();
			config.put("enable", "true");
			config.put("api-key", "NONE");
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Skyupdater configuration - http://www.skyost.eu/Skyupdater.txt");
			stringBuilder.append(lineSeparator);
			stringBuilder.append(lineSeparator);
			stringBuilder.append("What is Skyupdater ?");
			stringBuilder.append(lineSeparator);
			stringBuilder.append("Skyupdater is a simple auto-updater created by Skyost (http://www.skyost.eu) for Bukkit plugins.");
			stringBuilder.append(lineSeparator);
			stringBuilder.append(lineSeparator);
			stringBuilder.append("So what is this file ?");
			stringBuilder.append(lineSeparator);
			stringBuilder.append("This file is just a config file for the auto-updater.");
			stringBuilder.append(lineSeparator);
			stringBuilder.append(lineSeparator);
			stringBuilder.append("Configuration :");
			stringBuilder.append(lineSeparator);
			stringBuilder.append("'enable': Choose if you want to enable the auto-updater.");
			stringBuilder.append(lineSeparator);
			stringBuilder.append("'api-key': OPTIONAL. Your BukkitDev API Key.");
			stringBuilder.append(lineSeparator);
			stringBuilder.append(lineSeparator);
			stringBuilder.append("Good game, I hope you will enjoy your plugins always up-to-date ;)");
			stringBuilder.append(lineSeparator);
			config.store(new FileOutputStream(propertiesFile), stringBuilder.toString());
		}
		url = new URL("https://api.curseforge.com/servermods/files?projectIds=" + id);
		updaterThread = new Thread(new UpdaterThread());
		updaterThread.start();
	}
	
	/**
	 * Get the version of Skyupdater.
	 * 
	 * @return The version of Skyupdater.
	 */
	
	public static String getVersion() {
		return SKYUPDATER_VERSION;
	}
	
	/**
	 * Get the result of Skyupdater.
	 * 
	 * @return The result of the update process.
	 */
	
	public Result getResult() {
		waitForThread();
		return result;
	}
	
	/**
	 * Get informations about the latest file.
	 * 
	 * @param type The type of information you want.
	 * 
	 * @return The information you want.
	 */
	
	public String getLatestFileInfo(final InfoType type) {
		waitForThread();
		switch(type) {
		case DOWNLOAD_URL:
			return updateData[0];
		case FILE_NAME:
			return updateData[1];
		case GAME_VERSION:
			return updateData[2];
		case FILE_TITLE:
			return updateData[3];
		case RELEASE_TYPE:
			return updateData[4];
		}
		return null;
	}
	
	/**
	 * Get raw data about the latest file.
	 * 
	 * @return An array string which contains all data you want !
	 */
	
	public String[] getLatestFileData() {
		waitForThread();
		return updateData;
	}
	
	/**
	 * Downloads a file.
	 * 
	 * @param site The URL of the file you want to download.
	 * @param pathTo The path where you want the file to be downloaded.
	 * 
	 * @throws IOException InputOutputException.
	 */
	
	private boolean download(final String site, final File pathTo) {
		try {
			final HttpURLConnection connection = (HttpURLConnection)new URL(site).openConnection();
			connection.addRequestProperty("User-Agent", "Skyupdater v" + SKYUPDATER_VERSION);
			response = connection.getResponseCode() + " " + connection.getResponseMessage();
			if(!response.startsWith("2")) {
				if(announce) {
					logger.log(Level.INFO, "[Skyupdater] Bad response : '" + response + "' when trying to download the update.");
				}
				result = Result.ERROR;
				return false;
			}
			final long size = connection.getContentLengthLong();
			final long koSize = size / 1000;
			long lastPercent = 0;
			long percent = 0;
			float totalDataRead = 0;
			final InputStream inputStream = connection.getInputStream();
			final FileOutputStream fileOutputStream = new FileOutputStream(pathTo);
			final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
			final byte[] data = new byte[1024];
			int i = 0;
			while((i = inputStream.read(data, 0, 1024)) >= 0) {
				totalDataRead += i;
				bufferedOutputStream.write(data, 0, i);
				if(announce) {
					percent = ((long)(totalDataRead * 100) / size);
					if(lastPercent != percent) {
						lastPercent = percent;
						logger.log(Level.INFO, "[Skyupdater] " + percent + "% of " + koSize + "ko...");
					}
				}
			}
			bufferedOutputStream.close();
			fileOutputStream.close();
			inputStream.close();
			return true;
		}
		catch(Exception ex) {
			logger.log(Level.SEVERE, "Exception '" + ex + "' occured when downloading update. Please check your network connection.");
			result = Result.ERROR;
		}
		return false;
	}
	
	/**
	 * Compare two versions.
	 * 
	 * @param version1 The version you want to compare to.
	 * @param version2 The version you want to compare with.
	 * 
	 * @return <b>true</b> If <b>versionTo</b> is inferior than <b>versionWith</b>.
	 * <br><b>false</b> If <b>versionTo</b> is superior or equals to <b>versionWith</b>.
	 */
	
	public static final boolean compareVersions(final String versionTo, final String versionWith) {
		final int cmp = normalisedVersion(versionTo, ".", 4).compareTo(normalisedVersion(versionWith, ".", 4));
		if(cmp < 0) {
			return false;
		}
		else if(cmp > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the formatted name of a version.
	 * <br>Used for the method <b>compareVersions(...)</b> of this class.
	 * 
	 * @param version The version you want to format.
	 * @param separator The separator between the numbers of this version.
	 * @param maxWidth The max width of the formatted version.
	 * 
	 * @return A string which the formatted version of your version.
	 * 
	 * @author Peter Lawrey.
	 */

	private static final String normalisedVersion(final String version, final String separator, final int maxWidth) {
		final StringBuilder stringBuilder = new StringBuilder();
		for(final String normalised : Pattern.compile(separator, Pattern.LITERAL).split(version)) {
			stringBuilder.append(String.format("%" + maxWidth + 's', normalised));
		}
		return stringBuilder.toString();
	}
	
	/**
	 * As the result of Updater output depends on the thread's completion,
	 * <br>it is necessary to wait for the thread to finish before allowing anyone to check the result.
	 * 
	 * @author <b>Gravity</b> from his Updater.
	 */
	
	private void waitForThread() {
		if(updaterThread != null && updaterThread.isAlive()) {
			try {
				updaterThread.join();
			}
			catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class UpdaterThread implements Runnable {
	
		@Override
		public void run() {
			if(isEnabled) {
				try {
					final HttpURLConnection con = (HttpURLConnection)url.openConnection();
					con.addRequestProperty("User-Agent", "Skyupdater v" + SKYUPDATER_VERSION);
					if(apiKey != null) {
						con.addRequestProperty("X-API-Key", apiKey);
					}
					response = con.getResponseCode() + " " + con.getResponseMessage();
					if(!response.startsWith("2")) {
						if(announce) {
							logger.log(Level.INFO, "[Skyupdater] Bad response : '" + response + (response.startsWith("402") ? "'." : "'. Maybe your API Key is invalid ?"));
						}
						result = Result.ERROR;
						return;
					}
					final String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
					if(response != null && !response.equals("[]")) {
						final JSONArray jsonArray = (JSONArray)JSONValue.parseWithException(response);
						final JSONObject jsonObject = (JSONObject)jsonArray.get(jsonArray.size() - 1);
						updateData = new String[] {String.valueOf(jsonObject.get("downloadUrl")), String.valueOf(jsonObject.get("fileName")), String.valueOf(jsonObject.get("gameVersion")), String.valueOf(jsonObject.get("name")), String.valueOf(jsonObject.get("releaseType"))};
						if(compareVersions(updateData[3].split(" v")[1], plugin.getDescription().getVersion()) && updateData[0].toLowerCase().endsWith(".jar")) {
							result = Result.UPDATE_AVAILABLE;
							if(download) {
								if(announce) {
									logger.log(Level.INFO, "[Skyupdater] Downloading a new update : " + updateData[3] + "...");
								}
								if(download(updateData[0], new File(updateFolder, pluginFile.getName()))) {
									result = Result.SUCCESS;
									if(announce) {
										logger.log(Level.INFO, "[Skyupdater] The update of '" + plugin.getName() + "' has been downloaded and installed. It will be loaded at the next server load / reload.");
									}
								}
								else {
									result = Result.ERROR;
								}
							}
							else if(announce) {
								logger.log(Level.INFO, "[Skyupdater] An update has been found for '" + plugin.getName() + "' but nothing was downloaded.");
							}
							return;
						}
						else {
							result = Result.NO_UPDATE;
							if(announce) {
								logger.log(Level.INFO, "[Skyupdater] No update found for '" + plugin.getName() + "'.");
							}
						}
					}
					else {
						logger.log(Level.SEVERE, "[Skyupdater] The ID '" + id + "' was not found (or no files found for this project) ! Maybe the author(s) (" + Joiner.on(", ").join(plugin.getDescription().getAuthors()) + ") of '" + plugin.getName() + "' has misconfigured his plugin ?");
						result = Result.ERROR;
					}
				}
				catch(Exception ex) {
					logger.log(Level.SEVERE, "Exception '" + ex + "'. Please check your network connection.");
					result = Result.ERROR;
				}
			}
		}
		
	}

}
