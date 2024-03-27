package btw.community.arminias.rpg;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RPGPointsAllocation {
    public static final int NUM_STATS = 5;
    public static final int DEFAULT_POINTS = NUM_STATS * 5 + 5 + 5;
    private static final String[] statNames = new String[] {
            "Health",
            "Shanks",
            "Movement",
            "Hunger Rate",
            "Block Break Speed"
    };
    private static final int[] statMinValues = new int[] {
            RPGAddon.MIN_HP_POINTS,
            RPGAddon.MIN_SHANKS_POINTS,
            RPGAddon.MIN_MOVEMENT_SPEED_POINTS,
            RPGAddon.MIN_HUNGER_DROP_RATE_POINTS,
            RPGAddon.MIN_BLOCK_BREAK_SPEED_POINTS
    };
    private static final int[] defaultPoints = new int[] {5, 5, 5, 10, 10};

    private int totalPoints;
    private int[] points;


    private static final int healthIndex = 0;
    private static final int shanksIndex = 1;
    private static final int movementIndex = 2;
    private static final int hungerRateIndex = 3;
    private static final int blockBreakSpeedIndex = 4;

    public RPGPointsAllocation(int totalPoints, int... points) {
        this.totalPoints = totalPoints;
        this.points = points;
    }

    public RPGPointsAllocation(DataInputStream data) {
        try {
            totalPoints = data.readInt();
            points = new int[NUM_STATS];
            for (int i = 0; i < NUM_STATS; i++) {
                points[i] = data.readInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getHealthPoints() {
        return points[healthIndex];
    }

    public int getShanksPoints() {
        return points[shanksIndex];
    }

    public int getMovementPoints() {
        return points[movementIndex];
    }

    public int getHungerRatePoints() {
        return points[hungerRateIndex];
    }

    public int getBlockBreakSpeedPoints() {
        return points[blockBreakSpeedIndex];
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void allocatePoints(int stat, int points) {
        RPGPointsAllocation.allocatePoints(this, stat, points);
    }

    public static void allocatePoints(RPGPointsAllocation oldAllocation, int stat, int points) {
        int totalPoints = oldAllocation.getTotalPoints();

        int leftoverPoints = totalPoints - oldAllocation.getSpentPoints();

        if (points > leftoverPoints) {
            //System.out.println("Not enough points to allocate!");
            points = leftoverPoints;
        }

        if (oldAllocation.points[stat] + points < statMinValues[stat]) {
            points = statMinValues[stat] - oldAllocation.points[stat];
        }

        oldAllocation.points[stat] += points;
    }

    public static RPGPointsAllocation emptyAllocation(int totalPoints) {
        return new RPGPointsAllocation(totalPoints, new int[NUM_STATS]);
    }

    public static RPGPointsAllocation defaultAllocation(int totalPoints) {
        RPGPointsAllocation allocation = emptyAllocation(totalPoints);
        System.arraycopy(defaultPoints, 0, allocation.points, 0, NUM_STATS);
        return allocation;
    }

    public String getStatName(int k) {
        return statNames[k];
    }

    public int getStatValue(int k) {
        return points[k];
    }

    public int getMaxPoints() {
        return totalPoints;
    }

    public int getSpentPoints() {
        int i = 0;
        for (int j = 0; j < NUM_STATS; j++) {
            i += points[j];
        }
        return i;
    }

    public void incrementStat(int i) {
        allocatePoints(i, 1);
    }

    public void decrementStat(int i) {
        allocatePoints(i, -1);
    }

    public void writeToOutputStream(DataOutputStream data) {
        try {
            data.writeInt(totalPoints);
            for (int i = 0; i < NUM_STATS; i++) {
                data.writeInt(points[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMinStatValue(int stat) {
        return statMinValues[stat];
    }

    public void set(int index, int value) {
        points[index] = value;
    }
}
