package de.jupiterpi.mc.redstonelink.pins;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import de.jupiterpi.mc.redstonelink.ConfigFile;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.function.Consumer;

public class SSLCommunication {
    private JSch jsch;
    private Session session;

    private String password;

    public SSLCommunication(String host, String username, String password) throws JSchException {
        this.password = password;

        jsch = new JSch();
        jsch.setKnownHosts(ConfigFile.getProperty("known_hosts")); // probably redundant

        // session

        session = jsch.getSession(username, host);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
    }

    // input channel

    public void connectInputChannel(Consumer<String> onInput) {
        try {
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.connect();

            new Thread(() -> {
                try {
                    PrintStream stream = new PrintStream(channel.getOutputStream(), true);
                    String command = "echo " + password + " | sudo -S " + ConfigFile.getProperty("rpi-rdl-home") + "/in.sh";
                    stream.print(command);
                    stream.print("\n");

                    InputStream in = channel.getInputStream();
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);

                            String str = new String(tmp, 0, i);
                            for (String s : str.split("\n")) {
                                onInput.accept(s);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    // output channel

    private ChannelShell outputChannel;
    private PrintStream stream;

    private OutputChannelHandler handler;

    public static interface OutputChannelHandler {
        void onInput(String str, Consumer<String> send);
    }

    public void connectOutputChannel(OutputChannelHandler handler) {
        try {
            outputChannel = (ChannelShell) session.openChannel("shell");
            outputChannel.connect();

            stream = new PrintStream(outputChannel.getOutputStream(), true);

            this.handler = handler;

            new Thread(() -> {
                try {
                    InputStream in = outputChannel.getInputStream();
                    byte[] tmp = new byte[1024];
                    while (true) {
                        while (in.available() > 0) {
                            int i = in.read(tmp, 0, 1024);

                            String str = new String(tmp, 0, i);
                            for (String s : str.split("\n")) {
                                handler.onInput(s, this::send);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(String str) {
        stream.print(str);
        stream.print("\n");
    }
}