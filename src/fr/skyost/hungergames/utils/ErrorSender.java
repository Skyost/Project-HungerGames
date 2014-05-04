package fr.skyost.hungergames.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.google.common.base.Throwables;

import fr.skyost.hungergames.HungerGames;

/**
 * Used to send reports.
 */

public class ErrorSender {
	
	private String name;
	private String email;
	private String message;
	private String subject;
	
	/**
	 * Create a new error report.
	 * 
	 * @param name The sender's name.
	 * @param email The sender's email.
	 * @param message The sender's message.
	 * @param subject The subject. Please note that it must be already encoded.
	 */
	
	public ErrorSender(final String name, final String email, final String message, final String subject) {
		this.name = name;
		this.email = email;
		this.message = message;
		this.subject = subject;
	}
	
	public static final ErrorSender createReport(final Throwable throwable) {
		final String separator = System.lineSeparator();
		final StringBuilder builder = new StringBuilder();
		builder.append(Throwables.getStackTraceAsString(throwable));
		builder.append(separator);
		builder.append("Plugin version : '" + HungerGames.instance.getDescription().getVersion() + "'.");
		builder.append(separator);
		builder.append("Bukkit version : '" + Bukkit.getVersion() + "'.");
		builder.append(separator);
		builder.append("Java version : '" + System.getProperty("java.version") + "'.");
		return new ErrorSender(HungerGames.config.BugsReport_Name, HungerGames.config.BugsReport_Mail, builder.toString(), throwable.getClass().getName());
	}
	
	/**
	 * Upload the Throwable on paste.skyost.eu and send it to me.
	 * 
	 * @param throwable The Throwable.
	 * @deprecated You should use createReport(...) instead.
	 */
	
	public static final void uploadAndSend(final Throwable throwable) {
		new Thread() {
			
			@Override
			public void run() {
				try {
					if(HungerGames.config.BugsReport_Enable) {
						HungerGames.logsManager.log("[ErrorSender] Uploading your error on paste.skyost.eu...");
						final HttpURLConnection connection = (HttpURLConnection)new URL("http", "paste.skyost.eu", "/api/create").openConnection();
						connection.setRequestMethod("POST");
						connection.setRequestProperty("User-Agent", "Project HungerGames");
						connection.setDoOutput(true);
						final String separator = System.lineSeparator();
						final StringBuilder builder = new StringBuilder();
						builder.append(Throwables.getStackTraceAsString(throwable));
						builder.append(separator);
						builder.append("Plugin version : '" + HungerGames.instance.getDescription().getVersion() + "'.");
						builder.append(separator);
						builder.append("Bukkit version : '" + Bukkit.getVersion() + "'.");
						builder.append(separator);
						builder.append("Java version : '" + System.getProperty("java.version") + "'.");
						final String subject = URLEncoder.encode(throwable.getClass().getName(), "UTF-8");
						final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
						outputStream.writeBytes("text=" + URLEncoder.encode(builder.toString(), "UTF-8") + "&title=" + subject + "&name=" + URLEncoder.encode(HungerGames.config.BugsReport_Name, "UTF-8"));
						outputStream.flush();
						outputStream.close();
						builder.setLength(0);
						final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String inputLine;
						while((inputLine = reader.readLine()) != null) {
							builder.append(inputLine);
						}
						reader.close();
						connection.disconnect();
						HungerGames.logsManager.log("[ErrorSender] Done !");
						final String message = builder.toString();
						new ErrorSender(HungerGames.config.BugsReport_Name, HungerGames.config.BugsReport_Mail, message.startsWith("http") ? message : throwable.getMessage(), subject).report();
					}
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
					final HttpURLConnection connection = (HttpURLConnection)new URL("http", "www.project-hungergames.ml.eu", "/bug.php?name=" + encodedName + "&email=" + encodedEmail + "&message=" + encodedMessage + "&subject=" + subject).openConnection();
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
	 * @return The message.
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
	
	/**
	 * Get the subject.
	 * 
	 * @return The subject.
	 */
	
	public final String getSubject() {
		return subject;
	}
	
	/**
	 * Set the subject.
	 * 
	 * @param subject The subject to set.
	 */
	
	public final void setSubject(final String subject) {
		this.subject = subject;
	}
	
}
