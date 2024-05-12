package xyz.starchenpy.keepyourmouthhealthy.common.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

public class ToothpasteParticleType extends ParticleType<ToothpasteParticleOption> {
    public ToothpasteParticleType() {
        super(false, ToothpasteParticleOption.DESERIALIZER);
    }

    @NotNull
    @Override
    public Codec<ToothpasteParticleOption> codec() {
        return ToothpasteParticleOption.codec();
    }
}
