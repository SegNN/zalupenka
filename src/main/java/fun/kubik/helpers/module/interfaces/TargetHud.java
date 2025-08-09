/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.modules.combat.Aura;
import fun.kubik.modules.player.FixHP;
import fun.kubik.modules.render.Interface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

import org.lwjgl.opengl.GL11;

public class TargetHud
        extends Component {
    private float healthBar = 10.0f;
    private float animatedHP = 10.0f;

    private LivingEntity target = null;
    private final Animation absorption = new Animation();

    public TargetHud() {
        super("TargetHud", new Vector2f(10.0f, 226.0f), 220.0f, 85.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression());
    }

    @Override
    public void update(EventUpdate event) {
        boolean update;
        if (((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget() != null && ((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget() instanceof PlayerEntity) {
            this.target = ((Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class)).getTarget();
            update = true;
        } else if (TargetHud.mc.currentScreen instanceof ChatScreen) {
            this.target = TargetHud.mc.player;
            update = true;
        } else {
            update = false;
        }
        if (this.target != null) {
            boolean updateAbsorption = this.target.getAbsorptionAmount() > 0.1f;
            this.absorption.update(updateAbsorption);
        }
        this.getShowAnimation().update(update && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("TargetHud"));
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float width = 199.0f;
        float height = 75.0f;

        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, TargetHud.mc.getTimer().renderPartialTicks);
        if (this.getShowAnimation().getAnimationValue() == 0.0f) {
            this.target = null;
        }
        if (this.target == null) {
            return;
        }
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            StencilHelpers.uninit();
            float xHp = x + 74.0f;
            float yHp = y + 51.0f;
            float widthHp = 115.0f;
            float heightHp = 15.0f;
            float hp = this.target.getHealth();
            float maxHp = this.target.getMaxHealth();
            if (TargetHud.mc.world != null && TargetHud.mc.world.getScoreboard() != null) {
                Score score = TargetHud.mc.world.getScoreboard().getOrCreateScore(this.target.getScoreboardName(), TargetHud.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
                if (mc.getCurrentServerData() != null) {
                    if (((FixHP)Load.getInstance().getHooks().getModuleManagers().findClass(FixHP.class)).isToggled() && this.target instanceof PlayerEntity && score != null) {
                        hp = score.getScorePoints();
                        maxHp = 20.0f;
                    }
                }
            }
            VisualHelpers.drawShadow(x - 2,y - 3,width + 4,height + 6,15,ColorHelpers.getThemeColor(1));
            VisualHelpers.drawOutlineGradient(x - 2,y - 2,width + 4,height + 4,6,6,6,6,2,ColorHelpers.getThemeColor(1),ColorHelpers.getThemeColor(2),ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2));

            VisualHelpers.drawRoundedRect(x,y,width,height,6,ColorHelpers.rgb(0,0,0));
            // Проверка на получение урона для красной головы
            int headAlpha = this.target.hurtTime > 0 ? 100 : 255; // Красноватый оттенок при уроне
            VisualHelpers.drawRoundedHead(matrixStack,x + 4,y + 4,65,65,6,headAlpha, (AbstractClientPlayerEntity)this.target);
            float finalHp = Math.min(hp, this.target.getMaxHealth());
            this.healthBar = Animation.animate(this.healthBar, finalHp / maxHp);
            this.healthBar = MathHelper.clamp(this.healthBar, 0.0f, 1.0f);

            // Серый фон полоски здоровья (постоянного размера)
            VisualHelpers.drawRoundedRect(matrixStack, xHp, yHp, widthHp, heightHp, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f), ColorHelpers.setAlpha(ColorHelpers.rgb(60,60,60), (int)(255.0f * this.getShowAnimation().getAnimationValue())));

            // Цветная полоска здоровья (изменяемого размера) - цвета темы
            VisualHelpers.drawRoundedGradientRect(matrixStack, xHp, yHp, widthHp * this.healthBar, heightHp, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f), ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), ColorHelpers.setAlpha(ColorHelpers.getThemeColor(2), (int)(255.0f * this.getShowAnimation().getAnimationValue())), ColorHelpers.setAlpha(ColorHelpers.getThemeColor(1), (int)(255.0f * this.getShowAnimation().getAnimationValue())), ColorHelpers.setAlpha(ColorHelpers.getThemeColor(2), (int)(255.0f * this.getShowAnimation().getAnimationValue())));
            String targetName = this.target.getName().getString();
            String substring = targetName.substring(0, Math.min(targetName.length(), 12));
            suisse_intl.drawText(matrixStack, substring, x + 75, y + 8, ColorHelpers.rgba(255,255,255,255), 16.0f);

            StencilHelpers.init();
            StencilHelpers.read(1);
            StencilHelpers.uninit();
            this.animatedHP = Animation.animate(this.animatedHP, hp);
            String targetHP = this.animatedHP > 900.0f ? "???" : String.format("%.1f", this.animatedHP).replace(",", ".");
            String хпешки = ("HP: " + targetHP);
            sf_semibold.drawText(matrixStack,хпешки,x + 75,y + 30, ColorHelpers.rgba(255,255,255,255),11);
            
            // Проверка на эффект поглощения 4 уровня (чарка)
            EffectInstance absorptionEffect = this.target.getActivePotionEffect(Effects.ABSORPTION);
            if (absorptionEffect != null && absorptionEffect.getAmplifier() >= 3) { // amplifier 3 = уровень 4
                String charkaText = "чарка";
                float charkaX = x + width - suisse_intl.getWidth(charkaText, 12) - 5;
                float charkaY = y + -20;
                suisse_intl.drawText(matrixStack, charkaText, charkaX, charkaY, ColorHelpers.rgba(255, 215, 0, 255), 12);
            }

            this.drawItemStack(x + 5.0f, y + -25.0f, 20.0f, this.getShowAnimation().getAnimationValue());
            this.getDraggableOption().setWidth(width);
            this.getDraggableOption().setHeight(height);
        }
    }

    private void drawItemStack(float x, float y, float offset, float scaleValue) {
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>(Arrays.asList(this.target.getHeldItemMainhand(), this.target.getHeldItemOffhand()));
        stackList.addAll((Collection)this.target.getArmorInventoryList());
        AtomicReference<Float> posX = new AtomicReference<Float>(Float.valueOf(x));
        stackList.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> this.drawItemStack((ItemStack)stack, posX.getAndAccumulate(Float.valueOf(offset), Float::sum).floatValue(), y, scaleValue));
    }

    private void drawItemStack(ItemStack stack, float x, float y, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        float itemScale = scaleValue * 1.2f; // Увеличиваем размер предметов брони
        GL11.glScaled(itemScale, itemScale, itemScale);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        mc.getItemRenderer().renderItemOverlays(TargetHud.mc.fontRenderer, stack, 0, 0);
        RenderSystem.popMatrix();
    }
}