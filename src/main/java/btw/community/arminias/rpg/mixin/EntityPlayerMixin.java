package btw.community.arminias.rpg.mixin;

import btw.community.arminias.rpg.FoodStatsExtension;
import btw.community.arminias.rpg.RPGAddon;
import btw.community.arminias.rpg.RPGPointsAllocation;
import btw.community.arminias.rpg.RPGStats;
import btw.util.status.StatusEffect;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLiving implements RPGStats {
    @Shadow public PlayerCapabilities capabilities;
    @Shadow public FoodStats foodStats;
    private boolean statsDirty = false;

    @Shadow public abstract int getMaxHealth();

    private int cachedHealth = 0;
    private int cachedShanks = 0;

    private RPGPointsAllocation pointsAllocation = null;

    private EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void doReinit(RPGPointsAllocation points) {
        if ((Object) this instanceof EntityPlayerMP) {
            statsDirty = true;
        }
        this.pointsAllocation = points;
        this.health = Math.max(Math.min((int) getModifiedMaxHealth(), this.health), 0);
        ((PlayerCapabilitiesAccessor)capabilities).setWalkSpeed(getMovementSpeedModifier());
        ((FoodStatsExtension) foodStats).setPlayer((EntityPlayer) (Object) this);
        foodStats.setFoodLevel((int) (
                Math.max(Math.min(getModifiedMaxShanks(), foodStats.getFoodLevel()), 0)
        ));
    }

    @Override
    public RPGPointsAllocation getAllocation() {
        return pointsAllocation;
    }

    /*@Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        pointsAllocation = new RPGPointsAllocation(0, 0, 0, 0);
        this.health = Math.min((int) getModifiedMaxHealth(), this.health);
        ((PlayerCapabilitiesAccessor) capabilities).setWalkSpeed(getMovementSpeedModifier());
        ((FoodStatsExtension) foodStats).setPlayer((EntityPlayer) (Object) this);
        foodStats.setFoodLevel((int) (getModifiedMaxShanks()));
    }*/

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdate(CallbackInfo ci) {
        if (statsDirty && pointsAllocation != null) {
            statsDirty = false;
            for (int i = 0; i < RPGPointsAllocation.NUM_STATS; i++) {
                int points = pointsAllocation.getStatValue(i);
                if (points < RPGPointsAllocation.getMinStatValue(i)) {
                    pointsAllocation = RPGPointsAllocation.defaultAllocation(pointsAllocation.getTotalPoints());
                    if ((Object) this instanceof EntityPlayerMP) {
                        RPGAddon.sendStatAllocationScreenToPlayer((EntityPlayerMP) (Object) this);
                    }
                    break;
                }
            }
        }
    }

    @Inject(method = "getMaxHealth", at = @At("RETURN"), cancellable = true)
    private void getMaxHealth(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) (getModifiedMaxHealth()));
    }

    @Inject(method = "getStatusForCategory", at = @At("HEAD"))
    private void getAllActiveStatusEffects(CallbackInfoReturnable<ArrayList<StatusEffect>> cir) {
        this.cachedHealth = this.health;
        this.cachedShanks = this.foodStats.getFoodLevel();
        this.health = (int) getNormalizedHealth();
        this.foodStats.setFoodLevel((int) ((60F / Math.min(getModifiedMaxShanks(), 60F)) * this.foodStats.getFoodLevel()));
    }

    @Inject(method = "getStatusForCategory", at = @At("RETURN"))
    private void getAllActiveStatusEffectsReturn(CallbackInfoReturnable<ArrayList<StatusEffect>> cir) {
        this.health = this.cachedHealth;
        this.foodStats.setFoodLevel(this.cachedShanks);
    }

    @Inject(method = "writeModDataToNBT", at = @At("RETURN"))
    private void writeModDataToNBT(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        if (pointsAllocation != null) {
            par1NBTTagCompound.setInteger("rpgPointsTotal", pointsAllocation.getTotalPoints());
            for (int i = 0; i < RPGPointsAllocation.NUM_STATS; i++) {
                par1NBTTagCompound.setInteger("rpgPoints_" + pointsAllocation.getStatName(i), pointsAllocation.getStatValue(i));
            }
        }
    }

    @Inject(method = "readModDataFromNBT", at = @At("RETURN"))
    private void readModDataFromNBT(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        readPointAllocationFromNBT(par1NBTTagCompound);
    }

    private void readPointAllocationFromNBT(NBTTagCompound par1NBTTagCompound) {
        if (par1NBTTagCompound.hasKey("rpgPointsTotal")) {
            pointsAllocation = new RPGPointsAllocation(par1NBTTagCompound.getInteger("rpgPointsTotal"), new int[RPGPointsAllocation.NUM_STATS]);

            for (int i = 0; i < RPGPointsAllocation.NUM_STATS; i++) {
                if (par1NBTTagCompound.hasKey("rpgPoints_" + pointsAllocation.getStatName(i))) {
                    pointsAllocation.set(i, par1NBTTagCompound.getInteger("rpgPoints_" + pointsAllocation.getStatName(i)));
                }
            }
            this.doReinit(pointsAllocation);
        }
    }

    @Inject(method = "clonePlayer", at = @At("RETURN"))
    private void clonePlayer(EntityPlayer otherPlayer, boolean playerLeavingTheEnd, CallbackInfo ci) {
        if (otherPlayer instanceof RPGStats) {
            this.pointsAllocation = ((RPGStats) otherPlayer).getAllocation();
            this.doReinit(pointsAllocation);
            if (playerLeavingTheEnd) {
                this.health = otherPlayer.health;
                this.foodStats = otherPlayer.foodStats;
            } else {
                this.health = (int) getModifiedMaxHealth();
                this.foodStats.setFoodLevel((int) getModifiedMaxShanks());
            }
        }
    }

    @Inject(method = "canSwim", at = @At("RETURN"), cancellable = true)
    private void canSwim(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && pointsAllocation != null) {
            cir.setReturnValue(
                    !isWeighted() && getNormalizedHealth() >= 4F
            );
        }
    }

    @Inject(method = "getCurrentPlayerStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void getCurrentPlayerStrVsBlock(Block par1Block, int i, int j, int k, CallbackInfoReturnable<Float> cir) {
        if (pointsAllocation != null) {
            cir.setReturnValue(
                    cir.getReturnValue() * this.getBlockBreakModifier()
            );
        }
    }

    // Gets health normalized to 0-20
    private float getNormalizedHealth() {
        return (20F / Math.min(getMaxHealth(), 20F)) * this.health;
    }
}
