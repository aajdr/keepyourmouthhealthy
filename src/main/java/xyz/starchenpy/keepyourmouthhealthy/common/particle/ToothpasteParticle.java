package xyz.starchenpy.keepyourmouthhealthy.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ToothpasteParticle extends TextureSheetParticle {
    private final float uo;
    private final float vo;

    ToothpasteParticle(ClientLevel level, double x, double y, double z, double x1, double y1, double z1, ItemStack itemStack) {
        this(level, x, y, z, itemStack);
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += x1;
        this.yd += y1;
        this.zd += z1;
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    protected ToothpasteParticle(ClientLevel level, double x, double y, double z, ItemStack itemStack) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        var model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, null, 0);
        BakedModel bakedModel = model.getOverrides().resolve(model, itemStack, level, null, 0);
        if (bakedModel != null) {
            this.setSprite(bakedModel.getParticleIcon(ModelData.EMPTY));
        }
        this.gravity = 1.0F;
        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @ParametersAreNonnullByDefault
    public static class Provider implements ParticleProvider<ToothpasteParticleOption> {
        public Particle createParticle(ToothpasteParticleOption type, ClientLevel level, double x, double y, double z, double speedX, double speedY, double speedZ) {
            return new ToothpasteParticle(level, x, y, z, speedX, speedY, speedZ, type.getItem());
        }
    }
}
