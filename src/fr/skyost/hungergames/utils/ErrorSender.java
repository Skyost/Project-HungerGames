package fr.skyost.hungergames.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
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
	 * Report the specified exception.
	 * 
	 * @param throwable The exception.
	 */
	
	public static final void report(final Throwable throwable) {
		if(HungerGames.config.BugsReport_Enable) {
			new ErrorSender(HungerGames.config.BugsReport_Name, HungerGames.config.BugsReport_Mail, throwable.toString()).report();
		}
	}
	
	/**
	 * Send the email.
	 */
	
	public final void report() {
		new ReportSender().start();
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
				HungerGames.logsManager.log("[ErrorSender] Sending an error report from " + name + "<" + email + ">...", Level.INFO);
				final String encodedName = URLEncoder.encode(name, "UTF-8");
				final String encodedEmail = URLEncoder.encode(email, "UTF-8");
				final String encodedMessage = URLEncoder.encode(message, "UTF-8");
				final HttpURLConnection connection = (HttpURLConnection)new URL("http", "www.skyost.eu", "/sendmail.php?name=" + encodedName + "&email=" + encodedEmail + "&message=" + encodedMessage).openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "Project HungerGames");
				final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				final StringBuilder builder = new StringBuilder();
				while((inputLine = reader.readLine()) != null) {
					builder.append(inputLine);
				}
				reader.close();
				connection.disconnect();
				final String response = builder.toString();
				final boolean success = response.equals("1");
				HungerGames.logsManager.log("[ErrorSender] (" + response + ") " + (success ? "Success !" : "Error..."), success ? Level.INFO : Level.SEVERE);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				HungerGames.logsManager.log("[ErrorSender] Error while sending error report.");
			}
		}
		
	}
	
}
