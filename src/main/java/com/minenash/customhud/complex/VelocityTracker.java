package com.minenash.customhud.complex;

import com.minenash.customhud.conditionals.Operation;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

import static com.minenash.customhud.CustomHud.CLIENT;

public class VelocityTracker {

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
    private final int axes;

    public double velocity;

    public VelocityTracker(Operation smoothing, boolean trackX, boolean trackY, boolean trackZ) {
        this.smoothing = smoothing;
        this.trackX = trackX;
        this.trackY = trackY;
        this.trackZ = trackZ;
        this.axes = trackX && trackY && trackZ ? 3 : trackX && trackY || trackX && trackZ || trackY && trackZ ? 2 : 1;
    }

    public void tick() {
        var p = CLIENT.player;

        int smooth = MathHelper.clamp((int) smoothing.getValue(), 0, 19);
        int pastIndex = past(smooth+1);

        double changeX = trackX ? Math.abs(p.getX() - pastXs[pastIndex]) : 0;
        double changeY = trackY ? Math.abs(p.getY() - pastYs[pastIndex]) : 0;
        double changeZ = trackZ ? Math.abs(p.getZ() - pastZs[pastIndex]) : 0;

        velocity = (20D / (smooth+1)) * (
              axes == 3 ? Math.cbrt(changeX*changeX+changeY*changeY+changeZ*changeZ)
            : axes == 2 ? Math.sqrt(changeX*changeX+changeY*changeY+changeZ*changeZ)
            : changeX+changeY+changeZ);
    }

    private int past(int amount) {
        int i = index - amount;
        return i < 0 ? i + 21 : i;
    }

}
