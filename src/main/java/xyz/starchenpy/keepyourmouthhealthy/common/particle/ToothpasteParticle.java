package xyz.starchenpy.keepyourmouthhealthy.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class ToothpasteParticle extends TextureSheetParticle {
    private final float uo;
    private final float vo;
    private final boolean turnOver;

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

    /**
     * 刷牙时的牙膏颗粒
     */
    protected ToothpasteParticle(ClientLevel level, double x, double y, double z, ItemStack itemStack) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        var model = Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, null, 0);
        BakedModel bakedModel = model.getOverrides().resolve(model, itemStack, level, null, 0);
        if (bakedModel != null) {
            this.setSprite(bakedModel.getParticleIcon(ModelData.EMPTY));
        }
        this.gravity = 1.0F;
        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() / 10;
        this.vo = this.random.nextFloat() / 10;
        this.turnOver = this.random.nextBoolean();
    }

    @Override
    protected float getU0() {
        return this.turnOver ? this.sprite.getU(0.8f + uo) : this.sprite.getU(1);
    }

    @Override
    protected float getU1() {
        return this.turnOver ? this.sprite.getU(1) : this.sprite.getU(0.8f + uo);
    }

    @Override
    protected float getV0() {
        return this.turnOver ? this.sprite.getV(0) : this.sprite.getV(0.3f + vo);
    }

    @Override
    protected float getV1() {
        return this.turnOver ? this.sprite.getV(0.3f + vo) : this.sprite.getV(0);
    }

    @OnlyIn(Dist.CLIENT)
    @ParametersAreNonnullByDefault
    public static class Provider implements ParticleProvider<ToothpasteParticleOption> {
        public Particle createParticle(ToothpasteParticleOption type, ClientLevel level, double x, double y, double z, double speedX, double speedY, double speedZ) {
            return new ToothpasteParticle(level, x, y, z, speedX, speedY, speedZ, type.getItem());
        }
    }
}
