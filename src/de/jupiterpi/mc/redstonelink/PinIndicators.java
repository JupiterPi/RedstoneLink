package de.jupiterpi.mc.redstonelink;

import de.jupiterpi.mc.redstonelink.pins.GPIOControlUnit;
import de.jupiterpi.mc.redstonelink.pins.GPIOOutputPin;
import de.jupiterpi.mc.redstonelink.pins.GPIOPin;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PinIndicators {
    private GPIOControlUnit controlUnit;

    private final World world = Bukkit.getWorld("world");

    /**
     * To be started in separate thread!
     */
    public PinIndicators() {
        controlUnit = RedstoneLinkPlugin.controlUnit;

        try {
            recalculatePinDetails();

            while (true) {
                recalculatePinDetails();

                spawnParticles();

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("pin indicators started 2");
    }

    // pin details

    public void recalculatePinDetails() {
        List<GPIOPin> pins = controlUnit.getPins();
        pinDetails = calculatePinDetails(pins);
        recalculateParticleDetails();
    }

    private static class PinDetails {
        public double x;
        public double y;
        public double z;
        public boolean isOut;

        public PinDetails(double x, double y, double z, boolean isOut) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.isOut = isOut;
        }
    }

    private List<PinDetails> calculatePinDetails(List<GPIOPin> pins) {
        List<PinDetails> pinDetails = new ArrayList<>();
        for (GPIOPin pin : pins) {
            Location l = pin.getBlock();
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            boolean isOut = (pin instanceof GPIOOutputPin);
            pinDetails.add(new PinDetails(x, y, z, isOut));
        }
        return pinDetails;
    }

    private List<PinDetails> pinDetails;

    // particles

    private static class ParticleDetails {
        public double x;
        public double y;
        public double z;
        public Color color;

        public ParticleDetails(double x, double y, double z, Color color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }

    private void recalculateParticleDetails() {
        particleDetails = calculateParticleDetails(pinDetails);
    }

    private List<ParticleDetails> calculateParticleDetails(List<PinDetails> pinDetails) {
        List<ParticleDetails> particleDetails = new ArrayList<>();
        for (PinDetails details : pinDetails) {
            int[] o = new int[]{5, 5, 5};
            for (int i = 0; i < o.length; i++) {
                for (int o2 = -5; o2 <= 5; o2 += 10) {
                    int[] oo = new int[]{o[0], o[1], o[2]};
                    oo[i] += o2;

                    double x = details.x + oo[0]/10f;
                    double y = details.y + oo[1]/10f;
                    double z = details.z + oo[2]/10f;

                    particleDetails.add(new ParticleDetails(x, y, z, (details.isOut ? Color.BLUE : Color.GREEN)));
                }
            }
        }
        return particleDetails;
    }

    private List<ParticleDetails> particleDetails;

    private void spawnParticles() {
        for (ParticleDetails details : particleDetails) {
            world.spawnParticle(Particle.REDSTONE, details.x, details.y, details.z, 5, 0, 0, 0, 0, new Particle.DustOptions(details.color, 1.6f));
        }
    }
}