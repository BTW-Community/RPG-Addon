package btw.community.arminias.rpg;
import java.util.function.Supplier;

public class RPGStatsSurrogate implements RPGStats {
    private final Supplier<Float>[] index2method = new Supplier[RPGPointsAllocation.NUM_STATS];
    {
        index2method[0] = this::getModifiedMaxHealth;
        index2method[1] = this::getModifiedMaxShanks;
        index2method[2] = this::getMovementSpeedModifier;
        index2method[3] = this::getHungerRateModifier;
        index2method[4] = this::getBlockBreakModifier;
    }
    RPGPointsAllocation points;

    public RPGStatsSurrogate(RPGPointsAllocation points) {
        this.points = points;
    }

    @Override
    public void doReinit(RPGPointsAllocation points) {
        this.points = points;
    }

    @Override
    public RPGPointsAllocation getAllocation() {
        return points;
    }


    public String getModifierString(int k) {
        switch (k) {
            case 0:
            case 1:
                // Print int values
                return String.valueOf(this.index2method[k].get().intValue());
            case 2:
                // Display 100x of movement speed, 1 decimal place
                return String.valueOf(Math.round(this.index2method[k].get() * 1000) / 10.0f);
            case 3:
            case 4:
                return String.valueOf(Math.round(this.index2method[k].get() * 100)) + "%";
            default:
                throw new IllegalArgumentException("Invalid index");
        }
    }
}
