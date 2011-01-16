package rocketclienttest;

import com.lonedev.gtroot.shared.ClientServerMessageInteractor;
import com.lonedev.gtroot.shared.Utils;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Richard Hawkes
 */
public class Client implements SimpleClientListener, ClientChannelListener {
    Random random = new Random();
    ClientChannel tableChannel;

    public static void main(String[] args) {
        Client c = new Client();
        SimpleClient sc = new SimpleClient(c);

        Properties connectProps = new Properties();
        connectProps.put("host", "localhost");
        connectProps.put("port", "1139");

        try {
            System.out.println("Connecting.");
            sc.login(connectProps);
            System.out.println("Connected.");
        } catch (IOException e) {
            e.printStackTrace();
            // ... try again, or try a different hostname
            System.exit(1);
        }

        for (int i = 0; i < 3; i++) {
        try { Thread.sleep(5000); } catch (Exception ex) { }
        System.out.println("sending message");

        try {
            sc.send(Utils.encodeString(ClientServerMessageInteractor.createJoinTableRequestMessage()));
        } catch (Exception ex) {
            System.out.println("message send failed: " + ex);
        }
        }

        try { Thread.sleep(5000); } catch (Exception ex) { }
        sc.logout(false);
    }

    public PasswordAuthentication getPasswordAuthentication() {
        String player = "guest-" + random.nextInt(1000);
        String password = "guest";
        return new PasswordAuthentication(player, password.toCharArray());
    }

    public void loggedIn() {
        System.out.println("Yay! Logged in !");
    }

    public void loginFailed(String message) {
        System.err.println("Failed to login: " + message);
    }

    public ClientChannelListener joinedChannel(ClientChannel channel) {
        System.out.println("Sweet, I have joined the table channel: " + channel.getName());
        tableChannel = channel;
        return this;
    }

    public void receivedMessage(ByteBuffer message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reconnecting() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reconnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnected(boolean graceful, String arg1) {
        System.out.println("Disconected, graceful=" + graceful + ": " + arg1);
    }

    public void receivedMessage(ClientChannel channel, ByteBuffer message) {
        System.out.println("aha, message received from the table channel " + channel.getName() + ": " + Utils.decodeString(message));
    }

    public void leftChannel(ClientChannel channel) {
        System.out.println("I've been dumped from channel: " + channel.getName());
        tableChannel = null;
    }
}
