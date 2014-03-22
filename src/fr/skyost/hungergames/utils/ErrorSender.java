package fr.skyost.hungergames.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.plugin.PluginLogger;

import fr.skyost.hungergames.HungerGames;

/**
 * Used to send me emails through my website.
 */

public class ErrorSender {
	
	private String name;
	private String email;
	private String message;
	
	/**
	 * Create a new error report.
	 * 
	 * @param name The sender's name.
	 * @param email The sender's email.
	 * @param message The sender's message.
	 */
	
	public ErrorSender(final String name, final String email, final String message) {
		this.name = name;
		this.email = email;
		this.message = message;
	}
	
	/**
	 * Send the email.
	 */
	
	public final void report() {
		new ReportSender().start();
	}
	
	/**
	 * Automatically send errors.
	 * 
	 * @param logger The plugin logger.
	 */
	
	public static final void addHandler(final PluginLogger logger) {
		logger.addHandler(new Handler() {

			@Override
			public void close() throws SecurityException {}
			
			@Override
			public void flush() {}
			
			@Override
			public void publish(final LogRecord record) {
				if(record.getLevel() == Level.SEVERE) {
					new ErrorSender(HungerGames.config.BugsReport_Name, HungerGames.config.BugsReport_Mail, record.getMessage()).report();
				}
			}
			
		});
	}
	
	/**
	 * Get the sender's name.
	 * 
	 * @return His name.
	 */
	
	public final String getName() {
		return name;
	}
	
	/**
	 * Set the name.
	 * 
	 * @param name The name to set
	 */
	
	public final void setName(final String name) {
		this.name = name;
	}

	
	/**
	 * Get the email address.
	 * 
	 * @return The email address.
	 */
	
	public final String getEmail() {
		return email;
	}
	
	/**
	 * Set the email address.
	 * 
	 * @param email The email address to set.
	 */
	
	public final void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Get the message.
	 * 
	 * @return The message
	 */
	
	public final String getMessage() {
		return message;
	}
	
	/**
	 * Set the message.
	 * 
	 * @param message The message to set.
	 */
	
	public final void setMessage(final String message) {
		this.message = message;
	}

	private class ReportSender extends Thread {
		
		@Override
		public void run() {
			try {
				HungerGames.logger.log(Level.INFO, "[ErrorSender] Sending an error report from " + name + "<" + email + ">...");
				final String encodedName = URLEncoder.encode(name, "UTF-8");
				final String encodedEmail = URLEncoder.encode(email, "UTF-8");
				final String encodedMessage = URLEncoder.encode(message, "UTF-8");
				final HttpURLConnection connection = (HttpURLConnection)new URL("http", "www.skyost.eu", "/sendmail.php?name=" + encodedName + "&email=" + encodedEmail + "&message=" + encodedMessage).openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "Project HungerGames");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				final StringBuilder builder = new StringBuilder();
				while((inputLine = reader.readLine()) != null) {
					builder.append(inputLine);
				}
				reader.close();
				final String response = builder.toString();
				final boolean success = response.equals("1");
				HungerGames.logger.log(success ? Level.INFO : Level.SEVERE, "[ErrorSender] (" + response + ") " + (success ? "Success !" : "Error..."));
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
}
