package de.jupiterpi.mc.redstonelink.storage;

import de.jupiterpi.mc.redstonelink.pins.GPIOOutputPin;
import de.jupiterpi.mc.redstonelink.pins.GPIOPin;
import jupiterpi.tools.files.csv.CSVCastable;
import org.bukkit.Location;

public class GPIOPinDTO implements CSVCastable {
    private int number;
    private String direction;
    private double x;
    private double y;
    private double z;

    public GPIOPinDTO(GPIOPin pin) {
        number = pin.getNumber();
        direction = (pin instanceof GPIOOutputPin) ? "out" : "in";
        Location l = pin.getBlock();
        x = l.getX();
        y = l.getY();
        z = l.getZ();
    }

    /* getters */

    public int getNumber() {
        return number;
    }

    public String getDirection() {
        return direction;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "GPIOPinDTO{" +
                "number=" + number +
                ", direction='" + direction + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /* csv */

    public GPIOPinDTO(String[] f) {
        number = Integer.parseInt(f[0]);
        direction = f[1];
        x = Double.parseDouble(f[2]);
        y = Double.parseDouble(f[3]);
        z = Double.parseDouble(f[4]);
    }


    @Override
    public String[] toCSV() {
        return new String[]{
                Integer.toString(number),
                direction,
                Double.toString(x),
                Double.toString(y),
                Double.toString(z)
        };
    }
}