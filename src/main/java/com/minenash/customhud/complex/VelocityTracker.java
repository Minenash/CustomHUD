package com.minenash.customhud.complex;

import com.minenash.customhud.conditionals.Operation;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

import static com.minenash.customhud.CustomHud.CLIENT;

public class VelocityTracker {

    public static final double BPS_TO_MPH = 3600 / 1609.344;
    private static int index = 0;
    private static double[] pastXs = new double[21];
    private static double[] pastYs = new double[21];
    private static double[] pastZs = new double[21];

    static {
        Arrays.fill(pastXs, Double.NaN);
        Arrays.fill(pastYs, Double.NaN);
        Arrays.fill(pastZs, Double.NaN);
    }

    public static void recordCords() {
        var p = CLIENT.player;
        pastXs[index] = p.getX();
        pastYs[index] = p.getY();
        pastZs[index] = p.getZ();
        index = (index + 1) % 21;
    }

    private final Operation smoothing;
    private final boolean trackX;
    private final boolean trackY;
    private final boolean trackZ;

    public double velocity;

    public VelocityTracker(Operation smoothing, boolean trackX, boolean trackY, boolean trackZ) {
        boolean allEnabled = !trackX && !trackY && !trackZ;

        this.smoothing = smoothing;
        this.trackX = trackX || allEnabled;
        this.trackY = trackY || allEnabled;
        this.trackZ = trackZ || allEnabled;
    }

    public void tick() {
        var p = CLIENT.player;

        int smooth = MathHelper.clamp((int) smoothing.getValue(), 0, 19);
        int pastIndex = past(smooth+1);

        double changeX = trackX ? Math.abs(p.getX() - pastXs[pastIndex]) : 0;
        double changeY = trackY ? Math.abs(p.getY() - pastYs[pastIndex]) : 0;
        double changeZ = trackZ ? Math.abs(p.getZ() - pastZs[pastIndex]) : 0;

        velocity = (20D / (smooth+1)) * Math.sqrt(changeX*changeX+changeY*changeY+changeZ*changeZ);
    }

    private int past(int amount) {
        int i = index - amount;
        return i < 0 ? i + 21 : i;
    }

}
