package fr.skyost.hungergames.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

import com.google.common.base.Throwables;

import fr.skyost.hungergames.HungerGames;

/**
 * Used to send reports.
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
	 * Upload the Throwable on paste.skyost.eu and send it to me.
	 * 
	 * @param throwable The Throwable.
	 */
	
	public static final void uploadAndSend(final Throwable throwable) {
		new Thread() {
			
			@Override
			public void run() {
				try {
					HungerGames.logsManager.log("[ErrorSender] Uploading your error to paste.skyost.eu...");
					final HttpURLConnection connection = (HttpURLConnection)new URL("http", "paste.skyost.eu", "/api/create").openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("User-Agent", "Project HungerGames");
					connection.setDoOutput(true);
					final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream ());
					outputStream.writeBytes("text=" + URLEncoder.encode(Throwables.getStackTraceAsString(throwable), "UTF-8") + "&title=" + URLEncoder.encode(throwable.getClass().getName(), "UTF-8") + "&name=" + URLEncoder.encode(HungerGames.config.BugsReport_Name, "UTF-8"));
					outputStream.flush();
					outputStream.close();
					final StringBuilder builder = new StringBuilder();
					final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					while((inputLine = reader.readLine()) != null) {
						builder.append(inputLine);
					}
					reader.close();
					connection.disconnect();
					HungerGames.logsManager.log("[ErrorSender] Done !");
					final String message = builder.toString();
					new ErrorSender(HungerGames.config.BugsReport_Name, HungerGames.config.BugsReport_Mail, message.startsWith("http") ? message : throwable.getMessage()).report();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}.start();
	}
	
	/**
	 * Send the email.
	 */
	
	public final void report() {
		new Thread() {
			
			@Override
			public void run() {
				try {
					HungerGames.logsManager.log("[ErrorSender] Sending the report from " + name + "<" + email + ">...");
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
			
		}.start();
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
	
}
