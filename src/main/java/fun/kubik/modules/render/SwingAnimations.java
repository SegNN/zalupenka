/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.visual.EventHand;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.modules.combat.Aura;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SwingAnimations
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Default"), new SelectOptionValue("Drop"), new SelectOptionValue("Glide"), new SelectOptionValue("Swipe"), new SelectOptionValue("Smooth"), new SelectOptionValue("360"), new SelectOptionValue("Hit"), new SelectOptionValue("Old"));
    private final CheckboxOption onlyAura = new CheckboxOption("Only Aura", false);
    private final SliderOption scale = new SliderOption("Scale", 1.0f, 0.1f, 2.0f).increment(0.1f);
    private final SliderOption power = new SliderOption("Power", 6.0f, 1.0f, 10.0f).increment(1.0f);
    private final SliderOption speed = new SliderOption("Speed", 6.0f, 1.0f, 20.0f).increment(1.0f);
    private final SliderOption leftX = new SliderOption("Left X", 0.0f, -2.0f, 2.0f).increment(0.1f);
    private final SliderOption leftY = new SliderOption("Left Y", 0.0f, -2.0f, 2.0f).increment(0.1f);
    private final SliderOption leftZ = new SliderOption("Left Z", 0.0f, -2.0f, 2.0f).increment(0.1f);
    private final SliderOption rightX = new SliderOption("Right X", 0.0f, -2.0f, 2.0f).increment(0.1f);
    private final SliderOption rightY = new SliderOption("Right Y", 0.0f, -2.0f, 2.0f).increment(0.1f);
    private final SliderOption rightZ = new SliderOption("Right Z", 0.0f, -2.0f, 2.0f).increment(0.1f);

    public SwingAnimations() {
        super("SwingAnimations", Category.RENDER);
        this.settings(this.mode, this.onlyAura, this.power, this.scale, this.speed, this.leftX, this.leftY, this.leftZ, this.rightX, this.rightY, this.rightZ);
    }

    @EventHook
    public void anim(EventHand.Animation event) {
        boolean aura;
        float swingProgress = event.getSwingProgress();
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        float anim = (float)Math.sin((double)swingProgress * 1.5707963267948966 * 2.0);
        float sin1 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float sin2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        Aura auraModule = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        boolean bl = aura = (Boolean)this.onlyAura.getValue() == false || auraModule.isToggled() && auraModule.getTarget() != null;
        if (aura) {
            if (this.mode.getSelected("Default")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(sin2 * -(((Float)this.power.getValue()).floatValue() * 10.0f)));
                event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(sin2 * 45.0f));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(sin2 * 15.0f));
            }
            if (this.mode.getSelected("Drop")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(sin2 * -(((Float)this.power.getValue()).floatValue() * 10.0f) - 5.0f));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(sin2 * -20.0f));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(50.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-90.0f));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(50.0f));
            }
            if (this.mode.getSelected("Glide")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(80.0f));
                event.getMatrixStack().rotate(Vector3f.ZN.rotationDegrees(45.0f));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(-55.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(sin2 * sin1 * -(((Float)this.power.getValue()).floatValue() * 10.0f)));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-80.0f));
            }
            if (this.mode.getSelected("Swipe")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(90.0f));
                event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(-70.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-90.0f + 80.0f * anim));
            }
            if (this.mode.getSelected("Old")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(90.0f));
                event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(-30.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-60.0f - ((Float)this.power.getValue()).floatValue() * 10.0f * anim));
            }
            if (this.mode.getSelected("Smooth")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(sin2 * (45.0f + f * -20.0f)));
                f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                event.getMatrixStack().rotate(Vector3f.ZP.rotationDegrees(sin2 * f1 * -20.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(f1 * -Float.valueOf(((Float)this.power.getValue()).floatValue() * 10.0f).floatValue()));
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(sin2 * -45.0f));
            }
            if (this.mode.getSelected("360")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-360.0f * swingProgress));
            }
            if (this.mode.getSelected("Hit")) {
                event.getMatrixStack().scale(((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue(), ((Float)this.scale.getValue()).floatValue());
                float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                event.getMatrixStack().translate(0.56f, -0.2f, -0.7f);
                event.getMatrixStack().rotate(Vector3f.YP.rotationDegrees(45.0f));
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(g * -85.0f));
                event.getMatrixStack().translate(-0.1f, 0.28f, 0.2f);
                event.getMatrixStack().rotate(Vector3f.XP.rotationDegrees(-85.0f));
            }
        }
    }

    @EventHook
    public void speed(EventHand.Speed event) {
        boolean aura;
        Aura auraModule = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        boolean bl = aura = (Boolean)this.onlyAura.getValue() == false || auraModule.isToggled() && auraModule.getTarget() != null;
        if (aura) {
            event.setSpeed(((Float)this.speed.getValue()).floatValue());
        }
    }

    @EventHook
    public void position(EventHand.Position event) {
        boolean aura;
        Aura auraModule = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        boolean bl = aura = (Boolean)this.onlyAura.getValue() == false || auraModule.isToggled() && auraModule.getTarget() != null;
        if (aura) {
            event.setLeftX(((Float)this.leftX.getValue()).floatValue());
            event.setLeftY(((Float)this.leftY.getValue()).floatValue());
            event.setLeftZ(((Float)this.leftZ.getValue()).floatValue());
            event.setRightX(((Float)this.rightX.getValue()).floatValue());
            event.setRightY(((Float)this.rightY.getValue()).floatValue());
            event.setRightZ(((Float)this.rightZ.getValue()).floatValue());
        }
    }

    @EventHook
    public void upd(EventHand.Update eventHand) {
        boolean aura;
        Aura auraModule = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        boolean bl = aura = (Boolean)this.onlyAura.getValue() == false || auraModule.isToggled() && auraModule.getTarget() != null;
        if (aura) {
            eventHand.setUpdate(false);
        }
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }
}

