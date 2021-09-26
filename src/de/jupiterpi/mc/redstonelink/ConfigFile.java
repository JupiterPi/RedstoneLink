package de.jupiterpi.mc.redstonelink;

import jupiterpi.tools.files.Path;
import jupiterpi.tools.files.TextFile;

import java.util.HashMap;
import java.util.Map;

public class ConfigFile {
    private static final Path configFilePath = Path.getRunningDirectory().file("redstonelink-config");

    private static Map<String, String> properties = new HashMap<>();

    static {
        for (String line : new TextFile(configFilePath).getFile()) {
            if (line.isEmpty()) continue;
            String[] parts = line.split(": ");
            properties.put(parts[0], parts[1]);
        }
    }

    public static String getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}
