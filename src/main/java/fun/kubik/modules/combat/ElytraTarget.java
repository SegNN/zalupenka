/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.combat;

import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class ElytraTarget
extends Module {
    private final MultiOption targetOptions = new MultiOption("Options", new MultiOptionValue("Elytra Predict", true), new MultiOptionValue("Always Hit", true));
    private final SelectOption predictOption = new SelectOption("Predict Option", 0, new SelectOptionValue("Auto Distance"), new SelectOptionValue("Custom Distance")).visible(() -> this.targetOptions.getSelected("Elytra Predict"));
    private final SelectOption bypass = new SelectOption("Bypass", 0, new SelectOptionValue("Default"), new SelectOptionValue("Snap"));
    private final SliderOption predictDistance = new SliderOption("Predict Distance", 4.7f, 1.0f, 10.0f).increment(0.1f).visible(() -> this.targetOptions.getSelected("Elytra Predict") && this.predictOption.getSelected("Custom Distance"));
    private final SliderOption distance = new SliderOption("Distance", 3.0f, 1.0f, 5.0f).increment(0.1f);
    private final SliderOption preDistance = new SliderOption("Pre Distance", 15.0f, 1.0f, 30.0f).increment(1.0f);
    private final SliderOption delay = new SliderOption("Delay", 200.0f, 100.0f, 400.0f).increment(50.0f).visible(() -> this.bypass.getSelected("Snap"));

    public ElytraTarget() {
        super("ElytraTarget", Category.COMBAT);
        this.settings(this.targetOptions, this.predictOption, this.bypass, this.predictDistance, this.distance, this.preDistance, this.delay);
    }

    @Generated
    public MultiOption getTargetOptions() {
        return this.targetOptions;
    }

    @Generated
    public SelectOption getPredictOption() {
        return this.predictOption;
    }

    @Generated
    public SelectOption getBypass() {
        return this.bypass;
    }

    @Generated
    public SliderOption getPredictDistance() {
        return this.predictDistance;
    }

    @Generated
    public SliderOption getDistance() {
        return this.distance;
    }

    @Generated
    public SliderOption getPreDistance() {
        return this.preDistance;
    }

    @Generated
    public SliderOption getDelay() {
        return this.delay;
    }
}

