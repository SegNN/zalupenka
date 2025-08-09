/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.animation;

import fun.kubik.utils.math.MathUtils;
import fun.kubik.utils.time.TimerUtils;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class Animation {
    private float value;
    private float prevValue;
    private float animationSpeed;
    private float fromValue;
    private float toValue;
    private float animationValue;
    public static double delta;

    public void update(boolean update) {
        this.prevValue = this.value;
        this.value = MathUtils.clamp(this.value + (update ? this.animationSpeed : -this.animationSpeed), this.fromValue, this.toValue);
    }

    public void animate(float fromValue, float toValue, float animationSpeed, EasingList.Easing easing, float partialTicks) {
        this.animationSpeed = animationSpeed;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.animationValue = easing.ease(MathUtils.interpolate(this.prevValue, this.value, partialTicks));
    }

    public static float animate(float animation, float target, float speedTarget) {
        float dif = (target - animation) / Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 15.0f;
        if (dif > 0.0f) {
            dif = Math.max(speedTarget, dif);
            dif = Math.min(target - animation, dif);
        } else if (dif < 0.0f) {
            dif = Math.min(-speedTarget, dif);
            dif = Math.max(target - animation, dif);
        }
        return animation + dif;
    }

    public static float animate(float animation, float target) {
        float speedTarget = TimerUtils.deltaTime();
        return Animation.animate(animation, target, speedTarget);
    }

    public static float animationSpeed(float animation, float target, float speedTarget) {
        float dif = (target - animation) / Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 15.0f;
        if (dif > 0.0f) {
            dif = speedTarget;
            dif = Math.min(target - animation, dif);
        } else if (dif < 0.0f) {
            dif = -speedTarget;
            dif = Math.max(target - animation, dif);
        }
        return animation + dif;
    }

    public static float getAnimationState(float animation, float finalState, float speed) {
        float add = (float)(delta * (double)(speed / 1000.0f));
        animation = animation < finalState ? (animation + add < finalState ? (animation += add) : finalState) : (animation - add > finalState ? (animation -= add) : finalState);
        return animation;
    }

    public static double interpolateAnimation(double start, double end, double step) {
        return start + (end - start) * step;
    }

    public static float move(float from, float to, float minstep, float maxstep, float factor) {
        float f = (to - from) * MathHelper.clamp(factor, 0.0f, 1.0f);
        f = f < 0.0f ? MathHelper.clamp(f, -maxstep, -minstep) : MathHelper.clamp(f, minstep, maxstep);
        if (Math.abs(f) > Math.abs(to - from)) {
            return to;
        }
        return from + f;
    }

    @Generated
    public void setValue(float value) {
        this.value = value;
    }

    @Generated
    public void setPrevValue(float prevValue) {
        this.prevValue = prevValue;
    }

    @Generated
    public float getValue() {
        return this.value;
    }

    @Generated
    public float getPrevValue() {
        return this.prevValue;
    }

    @Generated
    public float getAnimationValue() {
        return this.animationValue;
    }

    @Generated
    public void setAnimationValue(float animationValue) {
        this.animationValue = animationValue;
    }
}

