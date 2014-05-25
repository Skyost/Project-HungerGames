package fr.skyost.hungergames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import fr.skyost.hungergames.utils.ErrorSender;

public class BungeeMessageListener implements PluginMessageListener {
	
	public static final String CHANNEL = "ProjectHungerGames";
	
	@Override
	public final void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
		if(!channel.equals("BungeeCord")) {
            return;
        }
		try {
			final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			if(in.readUTF().equals(CHANNEL)) {
				sendRequest("Forward", in.readUTF(), "ALL", null, HungerGames.config.bungeeServerName + " " + HungerGames.currentStep, Bukkit.getServer());
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.createReport(ex).report();
		}
	}
	
	public final void sendRequest(final String type, final String subChannel, final String server, final String destPlayer, final String arg, final PluginMessageRecipient sender) throws IOException {
		final ByteArrayOutputStream b = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(b);
		out.writeUTF(type);
		switch(type) {
		case "Connect":
		case "PlayerCount":
		case "PlayerList":
			out.writeUTF(server);
			break;
		case "ConnectOther":
			out.writeUTF(destPlayer);
			out.writeUTF(server);
			break;
		case "Message":
			out.writeUTF(destPlayer);
			out.writeUTF(arg);
			break;
		case "Forward":
			final byte[] data = arg.getBytes();
			out.writeUTF(server);
			out.writeUTF(subChannel);
			out.writeShort(data.length);
			out.write(data);
			break;
		case "UUIDOther":
			out.writeUTF(destPlayer);
			break;
		default:
			return;
		}
		sender.sendPluginMessage(HungerGames.instance, "BungeeCord", b.toByteArray());
	}
	
}
