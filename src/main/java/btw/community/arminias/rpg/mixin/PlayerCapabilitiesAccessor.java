package btw.community.arminias.rpg.mixin;

import net.minecraft.src.PlayerCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerCapabilities.class)
public interface PlayerCapabilitiesAccessor {
    @Accessor
    void setWalkSpeed(float walkSpeed);
}
