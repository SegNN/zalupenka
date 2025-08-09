package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.modules.render.Interface;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class PotionList
        extends Component {
    private float width = 0.0f;
    private float height = 0.0f;
    private float time = 0.0f;
    private final CheckboxOption hide = new CheckboxOption("Hide", true);

    public PotionList() {
        super("PotionList", new Vector2f(100.0f, 226.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
            effect.getAnimation().update(effect.getDuration() > 10);
        }
        boolean show = (!PotionList.mc.player.getActivePotionEffects().isEmpty() || PotionList.mc.currentScreen instanceof ChatScreen) && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("PotionList") || (Boolean)this.hide.getValue() == false;
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float staticWidth = 139.0f;
        float staticHeight = 34.0f;
        float staticTime = 10.0f;
        boolean showExamples = PotionList.mc.player.getActivePotionEffects().isEmpty() && PotionList.mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        /* 
        if (showExamples) {
            staticHeight += 36.0f;
        }
        */
        for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
            staticHeight += 18.0f * effect.getAnimation().getAnimationValue();
            staticWidth = Math.max(staticWidth, suisse_intl.getWidth(I18n.format(effect.getEffectName(), new Object[0]), 12.0f) + suisse_intl.getWidth(EffectUtils.getPotionDurationString(effect, 1.0f), 12.0f) + 30.0f);
            staticTime = Math.max(staticTime, sf_medium.getWidth(EffectUtils.getPotionDurationString(effect, 1.0f), 14.0f));
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.height = Animation.animate(this.height, staticHeight);
        this.time = Animation.animate(this.time, staticTime);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, PotionList.mc.getTimer().renderPartialTicks);
        float headerHeight = 28.0f;
        float extraHeight = 10.0f;
        float totalHeight = this.height + extraHeight;
        if ((double)this.getShowAnimation().getAnimationValue() > 0.1) {
            int glowColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 100.0f * this.getShowAnimation().getAnimationValue());
            VisualHelpers.drawShadow(x - 2, y - 2, this.width + 4, totalHeight + 4, 12, glowColor);
            float bindsHeight = totalHeight - headerHeight;
            int headerColor = ColorHelpers.rgba(0, 0, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(x, y, this.width, headerHeight + 1, new Vector4f(0.0f, 8.0f, 0.0f, 8.0f), headerColor);
            int bindsColor = ColorHelpers.rgba(7, 7, 7, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            VisualHelpers.drawRoundedRect(x, y + headerHeight - 1, this.width, bindsHeight + 1, new Vector4f(8.0f, 0.0f, 8.0f, 0.0f), bindsColor);
            float time = (float)(System.currentTimeMillis() % 2000) / 2000.0f;
            String title = "Potions";
            float titleWidth = suisse_intl.getWidth(title, 13.0f);
            float currentX = x + (this.width - titleWidth) / 2;
            for (int i = 0; i < title.length(); i++) {
                float wave = (float)Math.sin(time * Math.PI * 2 + i * 0.5f) * 0.5f + 0.5f;
                int charColor = ColorHelpers.rgba(
                        (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                        (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                        255
                );
                suisse_intl.drawText(matrixStack, String.valueOf(title.charAt(i)), currentX, y + 7, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 13.0f);
                currentX += suisse_intl.getWidth(String.valueOf(title.charAt(i)), 13.0f);
            }
            int textColor = ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
            int accentColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 255.0f * this.getShowAnimation().getAnimationValue());
            float i = headerHeight + 8.0f;
            /* 
            if (showExamples) {
                String speedName = "Speed";
                String speedLevel = " II";
                String speedDuration = "5:23";
                suisse_intl.drawText(matrixStack, speedName, x + 8, y + i, textColor, 12.0f);
                float speedLevelX = x + 8 + suisse_intl.getWidth(speedName, 12.0f);
                int speedLevelColor = ColorHelpers.rgba(255, 85, 85, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
                suisse_intl.drawText(matrixStack, speedLevel, speedLevelX, y + i, speedLevelColor, 12.0f);
                suisse_intl.drawText(matrixStack, speedDuration, x + this.width - suisse_intl.getWidth(speedDuration, 12.0f) - 8, y + i + 2, accentColor, 12.0f);
                i += 18.0f;
                String poisonName = "Poison";
                String poisonDuration = "0:15";
                int poisonColor = ColorHelpers.rgba(255, 85, 85, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
                suisse_intl.drawText(matrixStack, poisonName, x + 8, y + i, poisonColor, 12.0f);
                suisse_intl.drawText(matrixStack, poisonDuration, x + this.width - suisse_intl.getWidth(poisonDuration, 12.0f) - 8, y + i + 2, accentColor, 12.0f);
                i += 18.0f;
            }
            */
            for (EffectInstance effect : PotionList.mc.player.getActivePotionEffects()) {
                effect.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.NONE, PotionList.mc.getTimer().renderPartialTicks);
                if (!((double)effect.getAnimation().getAnimationValue() > 0.1)) continue;
                String effectName = I18n.format(effect.getEffectName(), new Object[0]);
                suisse_intl.drawText(matrixStack, effectName, x + 8, y + i, textColor, 12.0f);
                if (effect.getAmplifier() > 0) {
                    String levelText = " " + (effect.getAmplifier() + 1);
                    float levelX = x + 8 + suisse_intl.getWidth(effectName, 12.0f);
                    int levelColor = (effect.getAmplifier() + 1) >= 2 ?
                            ColorHelpers.rgba(255, 85, 85, (int)(255.0f * this.getShowAnimation().getAnimationValue())) :
                            textColor;
                    suisse_intl.drawText(matrixStack, levelText, levelX, y + i, levelColor, 12.0f);
                }
                String duration = EffectUtils.getPotionDurationString(effect, 1.0f);
                suisse_intl.drawText(matrixStack, duration, x + this.width - suisse_intl.getWidth(duration, 12.0f) - 8, y + i + 2, accentColor, 12.0f);
                i += 18.0f * effect.getAnimation().getAnimationValue();
            }
            this.getDraggableOption().setWidth(this.width);
            this.getDraggableOption().setHeight(totalHeight);
        }
    }
}