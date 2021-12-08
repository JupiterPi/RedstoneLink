package de.jupiterpi.mc.redstonelink.pins;

import com.jcraft.jsch.JSchException;
import de.jupiterpi.mc.redstonelink.ConfigFile;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GPIOControlUnit {
    private static final String HOST = ConfigFile.getProperty("host");
    private static final String USERNAME = ConfigFile.getProperty("username");
    private static final String PASSWORD = ConfigFile.getProperty("password");

    List<GPIOPin> pins = new ArrayList<>();

    private SSLCommunication comm;
    private Handler handler;

    // setup

    public GPIOControlUnit() throws JSchException {
        comm = new SSLCommunication(HOST, USERNAME, PASSWORD);
        comm.connectInputChannel((str) -> {
            if (str.contains("%")) {
                try {
                    int start = str.indexOf("%");
                    int end = str.indexOf("&");
                    String s = str.substring(start+1, end);
                    String[] p = s.split(",");

                    int pin = Integer.parseInt(p[0]);
                    boolean state = p[1].equals("1");

                    for (GPIOPin gpioPin : pins) {
                        if (gpioPin.getNumber() == pin) {
                            if (gpioPin instanceof GPIOInputPin) {
                                gpioPin.cacheState(state);
                            } else return;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    // thrown on "boolean state = p[1].equals('1');" when there can't be any value read (in the local bash script) because the out.sh script already unexported the pin => can be ignored
                }
            }
        });
        handler = new Handler();
        comm.connectOutputChannel(handler);
    }

    private static class Handler implements SSLCommunication.OutputChannelHandler {
        private int i = 0;

        private Consumer<String> send;

        private List<String> commandsQueue = new ArrayList<>();
        private boolean commandsQueueReady = false;

        private void print(String str) {
            send.accept(str);
        }

        @Override
        public void onInput(String str, Consumer<String> out) {
            send = out;
            if (str.contains("$")) {
                print("sudo " + ConfigFile.getProperty("rpi-rdl-home") + "/out.sh");
            } else if (str.contains("[sudo]")) {
                print(PASSWORD);
                commandsQueueReady = true;
            } else {
                if (commandsQueueReady) {
                    sendCommand(commandsQueue.get(0));
                    commandsQueue.remove(0);
                    if (commandsQueue.isEmpty()) {
                        commandsQueue = null;
                        commandsQueueReady = false;
                    }
                }
            }
        }

        public void send(String str) {
            if (commandsQueue == null) {
                sendCommand(str);
            } else {
                commandsQueue.add(str);
            }
        }

        private void sendCommand(String cmd) {
            System.out.println("Sending command: " + cmd);
            print(cmd);
        }
    }

    // public api

    public GPIOInputPin addInputPin(int number, Location block) {
        GPIOInputPin pin = new GPIOInputPin(number, block);
        exportPin(number);
        setupPin(number, "in");
        pins.add(pin);

        return pin;
    }

    public GPIOOutputPin addOutputPin(int number, Location block) {
        GPIOOutputPin pin = new GPIOOutputPin(number, block, this::outputPin);
        exportPin(number);
        setupPin(number, "out");
        pins.add(pin);

        return pin;
    }

    public void output(int pin, boolean state) {
        for (GPIOPin gpioPin : pins) {
            if (gpioPin.getNumber() == pin) {
                if (gpioPin instanceof GPIOOutputPin) {
                    gpioPin.cacheState(state);
                } else return;
            }
        }
    }

    public List<GPIOPin> getPins() {
        return pins;
    }

    public void applyAllStates() {
        for (GPIOPin gpioPin : pins) {
            gpioPin.applyState();
        }
    }

    public void removePin(int number) {
        List<GPIOPin> toRemove = new ArrayList<>();
        for (GPIOPin gpioPin : pins) {
            if (gpioPin.getNumber() == number) {
                unexportPin(number);
                toRemove.add(gpioPin);
            }
        }
        for (GPIOPin gpioPin : toRemove) {
            pins.remove(gpioPin);
        }
    }

    public void removeAllPins() {
        for (GPIOPin gpioPin : new ArrayList<>(pins)) {
            removePin(gpioPin.getNumber());
        }
    }

    // native api

    private void exportPin(int pin) {
        handler.send("export " + pin);
    }

    private void setupPin(int pin, String direction) {
        handler.send("setup " + pin + " " + direction);
    }

    private void outputPin(int pin, boolean state) {
        handler.send("out " + pin + " " + (state ? "1" : "0"));
    }

    private void unexportPin(int pin) {
        handler.send("unexport " + pin);
    }
}