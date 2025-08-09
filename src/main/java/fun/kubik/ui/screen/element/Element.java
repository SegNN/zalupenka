/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.ui.screen.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.helpers.interfaces.ITranslate;
import fun.kubik.managers.module.Module;
import lombok.Generated;

public abstract class Element<T>
implements IFastAccess,
ITranslate {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected final T option;
    private Module module;
    private Animation show = new Animation();

    public Element(float width, float height, T option) {
        this.width = width;
        this.height = height;
        this.option = option;
    }

    public Element<T> setModule(Module module) {
        this.module = module;
        return this;
    }

    public abstract void render(MatrixStack var1, int var2, int var3, float var4);

    public abstract void exit();

    public abstract void tick();

    public abstract void keyPressed(int var1, int var2, int var3);

    public abstract void keyReleased(int var1, int var2, int var3);

    public abstract void charTyped(char var1, int var2);

    public abstract void mouseClicked(double var1, double var3, int var5);

    public abstract void mouseScrolled(double var1, double var3, double var5);

    public abstract void mouseReleased(double var1, double var3, int var5);

    public abstract void translate();

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
    public T getOption() {
        return this.option;
    }

    @Generated
    public Module getModule() {
        return this.module;
    }

    @Generated
    public Animation getShow() {
        return this.show;
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
}

