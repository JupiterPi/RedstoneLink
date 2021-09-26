package de.jupiterpi.mc.redstonelink.storage;

import de.jupiterpi.mc.redstonelink.RedstoneLinkPlugin;
import de.jupiterpi.mc.redstonelink.pins.GPIOControlUnit;
import de.jupiterpi.mc.redstonelink.pins.GPIOPin;
import jupiterpi.tools.files.Path;
import jupiterpi.tools.files.csv.CSVObjectsFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PinsStorage {
    private final Path storageFile = Path.getRunningDirectory().file("rdl-pins.csv");

    private GPIOControlUnit controlUnit = RedstoneLinkPlugin.controlUnit;

    public void reload() {
        controlUnit.removeAllPins();
        CSVObjectsFile<GPIOPinDTO> file = new CSVObjectsFile<>(storageFile, GPIOPinDTO.class, true);
        for (GPIOPinDTO dto : file.getObjects()) {
            Location location = new Location(Bukkit.getWorld("world"), dto.getX(), dto.getY(), dto.getZ());
            int number = dto.getNumber();
            if (dto.getDirection().equals("out")) {
                controlUnit.addOutputPin(number, location);
            } else {
                controlUnit.addInputPin(number, location);
            }
        }
    }

    public void save() {
        List<GPIOPinDTO> dtos = new ArrayList<>();
        for (GPIOPin pin : controlUnit.getPins()) {
            GPIOPinDTO dto = new GPIOPinDTO(pin);
            dtos.add(dto);
        }
        CSVObjectsFile<GPIOPinDTO> file = new CSVObjectsFile<>(storageFile, GPIOPinDTO.class, true);
        file.writeObjects(dtos);
    }
}