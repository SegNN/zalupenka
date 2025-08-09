/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.notification.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.modules.render.Interface;
import lombok.Generated;

public class Notification
implements IFastAccess {
    private final String text;
    private final long time;
    private final Animation animation = new Animation();
    private Module module;
    private Pattern pattern = Pattern.NONE;
    private float x;
    private float y;
    private float width;
    private float height;
    private float alpha;
    private final long oldTime;

    public Notification(String text, long time) {
        this.text = text;
        this.time = time;
        this.oldTime = System.currentTimeMillis();
    }

    public Notification(String text, long time, Module module) {
        this.text = text;
        this.time = time;
        this.module = module;
        this.oldTime = System.currentTimeMillis();
    }

    public Notification setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public void render(EventRender2D.Pre event) {
        int selfcode;
        int image;
        int text;
        int glow;
        int indicator;
        MatrixStack matrixStack = event.getMatrixStack();
        float imageWidth = this.module != null ? 20.0f : 0.0f;
        this.width = suisse_intl.getWidth(this.getText(), 12.0f) + 24.0f + imageWidth;
        this.height = 38.0f;
        int back = ColorHelpers.rgba(15, 15, 15, 255.0f * this.alpha);
        int outline = ColorHelpers.rgba(190, 190, 190, 10.200000000000001 * (double)this.alpha);
        if (this.pattern == Pattern.DISABLE || this.pattern == Pattern.ERROR) {
            indicator = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 * (double)this.alpha);
            glow = ColorHelpers.rgba(255, 255, 255, 61.199999999999996 * (double)this.alpha);
            text = ColorHelpers.rgba(255, 255, 255, 183.6 * (double)this.alpha);
            image = ColorHelpers.rgba(255, 255, 255, 122.39999999999999 * (double)this.alpha);
            selfcode = ColorHelpers.getColorWithAlpha(ColorHelpers.rgba(255, 255, 255, 255), 40.800000000000004 * (double)this.alpha);
        } else {
            indicator = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), this.alpha * 255.0f);
            glow = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 61.199999999999996 * (double)this.alpha);
            text = ColorHelpers.rgba(255, 255, 255, 255.0f * this.alpha);
            image = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), this.alpha * 255.0f);
            selfcode = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 40.800000000000004 * (double)this.alpha);
        }
        if ((double)this.alpha > 0.1) {
            Interface interfaces = (Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class);
            if (interfaces.getNotifDesign().getSelected("Transparent")) {
                BLUR_RUNNABLES.add(() -> VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 12.0f, back));
                this.blurSetting(Notification.mc.getTimer().renderPartialTicks, 4.0f, ((Float)interfaces.getCompression().getValue()).floatValue());
                VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 6.0f, selfcode);
            } else if (interfaces.getNotifDesign().getSelected("Standard")) {
                VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 6.0f, back);
            }
            VisualHelpers.drawRoundedOutline(matrixStack, this.x, this.y, this.width, this.height, 6.0f, 1.0f, outline);
            StencilHelpers.init();
            VisualHelpers.drawRoundedRect(matrixStack, this.x, this.y, this.width, this.height, 6.0f, -1);
            StencilHelpers.read(1);
            if (interfaces.getNotifDesign().getSelected("Standard")) {
                VisualHelpers.drawGlow(matrixStack, this.x, this.y, this.width, 48.0f, 100.0f, glow);
            }
            StencilHelpers.uninit();
            if (this.module != null) {
                VisualHelpers.drawImage(matrixStack, this.module.getCategory().getPath(), this.x + 12.0f, this.y + this.height / 2.0f - 6.0f, 12.0f, 12.0f, image);
                suisse_intl.drawText(matrixStack, this.getText(), this.x + 30.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, text, 12.0f);
            } else {
                suisse_intl.drawText(matrixStack, this.getText(), this.x + 12.0f, this.y + this.height / 2.0f - suisse_intl.getHeight(12.0f) / 2.0f, text, 12.0f);
            }
        }
    }

    @Generated
    public String getText() {
        return this.text;
    }

    @Generated
    public long getTime() {
        return this.time;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public Pattern getPattern() {
        return this.pattern;
    }

    @Generated
    public float getX() {
        return this.x;
    }

    @Generated
    public float getY() {
        return this.y;
    }

    @Generated
    public float getWidth() {
        return this.width;
    }

    @Generated
    public float getHeight() {
        return this.height;
    }

    @Generated
    public float getAlpha() {
        return this.alpha;
    }

    @Generated
    public long getOldTime() {
        return this.oldTime;
    }

    @Generated
    public void setModule(Module module) {
        this.module = module;
    }

    @Generated
    public void setX(float x) {
        this.x = x;
    }

    @Generated
    public void setY(float y) {
        this.y = y;
    }

    @Generated
    public void setWidth(float width) {
        this.width = width;
    }

    @Generated
    public void setHeight(float height) {
        this.height = height;
    }

    @Generated
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}

