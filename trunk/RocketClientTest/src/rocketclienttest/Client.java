package rocketclienttest;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Richard Hawkes
 */
public class Client implements SimpleClientListener {

    public static final String MESSAGE_CHARSET = "UTF-8";
    Random random = new Random();

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

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {
            }
            System.out.println("sending message");

            try {
                sc.send(encodeString("test string " + i));
            } catch (Exception ex) {
                System.out.println("message send failed: " + ex);
            }
        }

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void receivedMessage(ByteBuffer arg0) {
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

    protected static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }

    protected static String decodeString(ByteBuffer buf) {
        try {
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET + " not found", e);
        }
    }
}
