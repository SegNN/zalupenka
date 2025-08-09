package fun.kubik.helpers.module.interfaces;

import com.mojang.blaze3d.matrix.MatrixStack;
import fun.kubik.Load;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.helpers.visual.StencilHelpers;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.modules.render.Interface;
import fun.kubik.utils.client.KeyUtils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class KeyBinds
        extends Component {
    private final CheckboxOption hide = new CheckboxOption("Hide", true);
    private float width = 0.0f;
    private float key = 0.0f;
    private final Animation animation = new Animation();

    public KeyBinds() {
        super("KeyBinds", new Vector2f(10.0f, 46.0f), 145.0f, 66.0f);
        this.getDraggableOption().settings(this.getDesign(), this.getCompression(), this.hide);
    }

    @Override
    public void update(EventUpdate event) {
        boolean checkBox = false;
        for (Module module : Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList()) {
            module.getAnimation().update(module.isToggled());
        }
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            for (Option<?> option : module.getSettingList()) {
                CheckboxOption checkboxOption;
                if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                    checkboxOption.getAnimation().update((Boolean)checkboxOption.getValue());
                    if (!checkBox) {
                        checkBox = (Boolean)checkboxOption.getValue();
                    }
                }
                if (!(option instanceof MultiOption)) continue;
                MultiOption multiOption = (MultiOption)option;
                for (MultiOptionValue value : multiOption.getValues()) {
                    value.getAnim().update(value.isToggle() && value.getKey() >= 0);
                    if (value.getKey() < 0 || checkBox) continue;
                    checkBox = value.isToggle();
                }
            }
        }
        boolean show = (!Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::isToggled).filter(Module::hasBind).toList().isEmpty() || checkBox || KeyBinds.mc.currentScreen instanceof ChatScreen) && ((Interface)Load.getInstance().getHooks().getModuleManagers().findClass(Interface.class)).getElements().getSelected("KeyBinds") || (Boolean)this.hide.getValue() == false;
        this.animation.update(true);
        this.getShowAnimation().update(show);
    }

    @Override
    public void render(EventRender2D.Pre event) {
        float x = ((Vector2f)this.getDraggableOption().getValue()).x;
        float y = ((Vector2f)this.getDraggableOption().getValue()).y;
        MatrixStack matrixStack = event.getMatrixStack();
        float height = 34.0f;
        float staticWidth = 139.0f;
        float keyWidth = 10.0f;
        matrixStack.push();
        var modulesWithBinds = Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList();
        boolean showExamples = modulesWithBinds.isEmpty() && KeyBinds.mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        /* 
        if (showExamples) {
            String[] exampleNames = {"Fly", "Speed", "KillAura"};
            String[] exampleKeys = {"[F]", "[G]", "[R]"};
            for (int j = 0; j < exampleNames.length; j++) {
                height += 18.0f;
                float totalWidth = suisse_intl.getWidth(exampleNames[j], 12.0f) + suisse_intl.getWidth(exampleKeys[j], 12.0f) + 30.0f;
                staticWidth = Math.max(staticWidth, totalWidth);
                keyWidth = Math.max(keyWidth, suisse_intl.getWidth(exampleKeys[j], 12.0f));
            }
        }
        */
        for (Module module : modulesWithBinds) {
            if (!((double)module.getAnimation().getAnimationValue() > 0.1)) continue;
            height += 18.0f * module.getAnimation().getAnimationValue();
            float totalWidth = suisse_intl.getWidth(module.getName(), 12.0f) + suisse_intl.getWidth("[" + KeyUtils.getKey(module.getCurrentKey()) + "]", 12.0f) + 30.0f;
            staticWidth = Math.max(staticWidth, totalWidth);
            keyWidth = Math.max(keyWidth, suisse_intl.getWidth("[" + KeyUtils.getKey(module.getCurrentKey()) + "]", 12.0f));
        }
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            for (Option<?> option : module.getSettingList()) {
                CheckboxOption checkboxOption;
                if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                    height += 18.0f * checkboxOption.getAnimation().getAnimationValue();
                    float totalWidth = suisse_intl.getWidth(checkboxOption.getVisualName(), 12.0f) + suisse_intl.getWidth("[" + KeyUtils.getKey(checkboxOption.getKey()) + "]", 12.0f) + 30.0f;
                    staticWidth = Math.max(staticWidth, totalWidth);
                    keyWidth = Math.max(keyWidth, suisse_intl.getWidth("[" + KeyUtils.getKey(checkboxOption.getKey()) + "]", 12.0f));
                }
                if (!(option instanceof MultiOption)) continue;
                MultiOption multiOption = (MultiOption)option;
                for (MultiOptionValue value : multiOption.getValues()) {
                    if (value.getKey() < 0) continue;
                    height += 18.0f * value.getAnim().getAnimationValue();
                    float totalWidth = suisse_intl.getWidth(value.getVisualName(), 12.0f) + suisse_intl.getWidth("[" + KeyUtils.getKey(value.getKey()) + "]", 12.0f) + 30.0f;
                    staticWidth = Math.max(staticWidth, totalWidth);
                    keyWidth = Math.max(keyWidth, suisse_intl.getWidth("[" + KeyUtils.getKey(value.getKey()) + "]", 12.0f));
                }
            }
        }
        this.width = Animation.animate(this.width, staticWidth);
        this.key = Animation.animate(this.key, keyWidth);
        this.getShowAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
        int textColor = ColorHelpers.rgba(255, 255, 255, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        int accentColor = ColorHelpers.rgb(255,255,255);
        float headerHeight = 28.0f;
        float extraHeight = 10.0f;
        float totalHeight = height + extraHeight;
        if (this.getShowAnimation().getAnimationValue() > 0.1) {
            int glowColor = ColorHelpers.getColorWithAlpha(ColorHelpers.getThemeColor(1), 100.0f * this.getShowAnimation().getAnimationValue());
            VisualHelpers.drawShadow(x - 2, y - 2, width + 4, totalHeight + 4, 12, glowColor);
        }
        float bindsHeight = totalHeight - headerHeight;
        int headerColor = ColorHelpers.rgba(0, 0, 0, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        VisualHelpers.drawRoundedRect(x, y, width, headerHeight + 1, new Vector4f(0.0f, 8.0f, 0.0f, 8.0f), headerColor);
        int bindsColor = ColorHelpers.rgba(7, 7, 7, (int)(255.0f * this.getShowAnimation().getAnimationValue()));
        VisualHelpers.drawRoundedRect(x, y + headerHeight - 1, width, bindsHeight + 1, new Vector4f(8.0f, 0.0f, 8.0f, 0.0f), bindsColor);
        float time = (float)(System.currentTimeMillis() % 2000) / 2000.0f;
        String title = "KeyBinds";
        float titleWidth = suisse_intl.getWidth(title, 13.0f);
        float currentX = x + (width - titleWidth) / 2;
        for (int j = 0; j < title.length(); j++) {
            float wave = (float)Math.sin(time * Math.PI * 2 + j * 0.5f) * 0.5f + 0.5f;
            int charColor = ColorHelpers.rgba(
                    (int)(ColorHelpers.getRed(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getRed(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getGreen(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getGreen(ColorHelpers.getThemeColor(2)) * wave),
                    (int)(ColorHelpers.getBlue(ColorHelpers.getThemeColor(1)) * (1 - wave) + ColorHelpers.getBlue(ColorHelpers.getThemeColor(2)) * wave),
                    255
            );
            suisse_intl.drawText(matrixStack, String.valueOf(title.charAt(j)), currentX, y + 7, ColorHelpers.setAlpha(charColor, (int)(255.0f * this.getShowAnimation().getAnimationValue())), 13.0f);
            currentX += suisse_intl.getWidth(String.valueOf(title.charAt(j)), 13.0f);
        }
        float i = 34.0f;
        /* 
        if (showExamples) {
            String[] exampleNames = {"Fly", "Speed", "KillAura"};
            String[] exampleKeys = {"[F]", "[G]", "[R]"};
            for (int j = 0; j < exampleNames.length; j++) {
                suisse_intl.drawText(matrixStack, exampleNames[j], x + 8, y + i, textColor, 12.0f);
                suisse_intl.drawText(matrixStack, exampleKeys[j], x + this.width - this.key - 8.0f, y + i, accentColor, 12.0f);
                i += 18.0f;
            }
        }
        */
        for (Module module : Load.getInstance().getHooks().getModuleManagers().stream().filter(Module::hasBind).toList()) {
            module.getAnimation().animate(0.0f, 1.0f, 0.3f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
            if (!((double)module.getAnimation().getAnimationValue() > 0.1)) continue;
            suisse_intl.drawText(matrixStack, module.getName(), x + 8, y + i, textColor, 12.0f);
            suisse_intl.drawText(matrixStack, "[" + KeyUtils.getKey(module.getCurrentKey()) + "]", x + this.width - this.key - 8.0f, y + i, accentColor, 12.0f);
            i += 18.0f * module.getAnimation().getAnimationValue();
        }
        for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
            for (Option<?> option : module.getSettingList()) {
                CheckboxOption checkboxOption;
                if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() >= 0) {
                    checkboxOption.getAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
                    if ((double)checkboxOption.getAnimation().getAnimationValue() > 0.1) {
                        suisse_intl.drawText(matrixStack, checkboxOption.getVisualName(), x + 8, y + i, textColor, 12.0f);
                        suisse_intl.drawText(matrixStack, "[" + KeyUtils.getKey(checkboxOption.getKey()) + "]", x + this.width - this.key - 8.0f, y + i, accentColor, 12.0f);
                        i += 18.0f * checkboxOption.getAnimation().getAnimationValue();
                    }
                }
                if (!(option instanceof MultiOption)) continue;
                MultiOption multiOption = (MultiOption)option;
                for (MultiOptionValue value : multiOption.getValues()) {
                    value.getAnim().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, KeyBinds.mc.getTimer().renderPartialTicks);
                    if (!((double)value.getAnim().getAnimationValue() > 0.1)) continue;
                    suisse_intl.drawText(matrixStack, value.getVisualName(), x + 8, y + i, textColor, 12.0f);
                    suisse_intl.drawText(matrixStack, "[" + KeyUtils.getKey(value.getKey()) + "]", x + this.width - this.key - 8.0f, y + i, accentColor, 12.0f);
                    i += 18.0f * value.getAnim().getAnimationValue();
                }
            }
        }
        StencilHelpers.uninit();
        matrixStack.pop();
        this.getDraggableOption().setWidth(this.width);
        this.getDraggableOption().setHeight(height);
    }
}