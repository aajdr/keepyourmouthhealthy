package xyz.starchenpy.keepyourmouthhealthy.common.item.toothpaste;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import xyz.starchenpy.keepyourmouthhealthy.KeepYourMouthHealthy;
import xyz.starchenpy.keepyourmouthhealthy.common.effect.ModEffects;
import xyz.starchenpy.keepyourmouthhealthy.common.util.EffectUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public abstract class AbstractToothpaste extends Item {
    protected int maxLevel;
    protected int color;

    /**
     * 牙膏的抽象类
     * @param item 物品属性
     * @param maxLevel 能刷掉的蛀牙的最大等级
     */
    public AbstractToothpaste(Properties item, int maxLevel, int color) {
        super(item);
        this.maxLevel = maxLevel;
        this.color = color;
    }

    /**
     * 降低一级蛀牙效果
     * @param entity 玩家实体
     */
    protected void cleanTooth(LivingEntity entity) {
        MobEffectInstance effectToothDecay = entity.getEffect(ModEffects.TOOTH_DECAY.get());

        if (effectToothDecay == null) {
            return;
        }

        // 除蛀
        int amplifier = effectToothDecay.getAmplifier();
        if (amplifier < this.maxLevel) {
            EffectUtil.updateEffect(entity, effectToothDecay, amplifier - 1);
        }
    }

    public int getColor() {
        return this.color;
    }

    public abstract void effect(LivingEntity entity);
}
