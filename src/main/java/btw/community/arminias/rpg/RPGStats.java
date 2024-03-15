package btw.community.arminias.rpg;

public interface RPGStats {
    default float getModifiedMaxHealth() {
        return getAllocation() != null ? RPGAddon.BASE_HP + getAllocation().getHealthPoints() * RPGAddon.HP_PER_POINT : 20F;
    }
    default float getModifiedMaxShanks() {
        return getAllocation() != null ? RPGAddon.BASE_SHANKS + getAllocation().getShanksPoints() * RPGAddon.SHANKS_PER_POINT : 60F;
    }
    default float getMovementSpeedModifier() {
        return getAllocation() != null ? RPGAddon.BASE_MOVEMENT_SPEED + getAllocation().getMovementPoints() * RPGAddon.MOVEMENT_SPEED_PER_POINT : 0.1F;
    }
    default float getHungerRateModifier() {
        return getAllocation() != null ? (float) (RPGAddon.BASE_HUNGER_DROP_RATE * Math.pow((1 - RPGAddon.HUNGER_DROP_RATE_PER_POINT), getAllocation().getHungerRatePoints())) : 1.0F;
    }
    default float getBlockBreakModifier() {
        return getAllocation() != null ? (float) (RPGAddon.BASE_BLOCK_BREAK_SPEED * Math.pow((1 + RPGAddon.BLOCK_BREAK_SPEED_PER_POINT), getAllocation().getBlockBreakSpeedPoints())) : 1.0F;
    }

    void doReinit(RPGPointsAllocation points);

    RPGPointsAllocation getAllocation();
}
