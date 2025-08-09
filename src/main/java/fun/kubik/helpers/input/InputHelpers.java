/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.helpers.input;

import fun.kubik.Load;
import fun.kubik.events.api.EventManager;
import fun.kubik.events.main.input.EventInput;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.notification.api.Notification;
import fun.kubik.managers.notification.api.Pattern;
import fun.kubik.utils.client.SoundUtils;

public class InputHelpers
implements IFastAccess {
    public InputHelpers(int key) {
        if (key >= 0) {
            boolean flag = InputHelpers.mc.currentScreen == null || key != 341;
            for (Module m : Load.getInstance().getHooks().getModuleManagers()) {
                if (m.getCurrentKey() == key) {
                    m.toggle();
                }
                for (Option<?> option : m.getSettingList()) {
                    CheckboxOption checkboxOption;
                    if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() == key && checkboxOption.getVisible().getAsBoolean()) {
                        checkboxOption.setValue((Boolean)checkboxOption.getValue() == false);
                        if (((Boolean)checkboxOption.getValue()).booleanValue()) {
                            SoundUtils.playSound("enable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(checkboxOption.getVisualName() + " has been enabled", 1500L, checkboxOption.getModule()).setPattern(Pattern.ENABLE));
                        } else {
                            SoundUtils.playSound("disable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(checkboxOption.getVisualName() + " has been disabled", 1500L, checkboxOption.getModule()).setPattern(Pattern.DISABLE));
                        }
                    }
                    if (!(option instanceof MultiOption)) continue;
                    MultiOption multiOption = (MultiOption)option;
                    for (MultiOptionValue value : multiOption.getValues()) {
                        if (value.getKey() != key) continue;
                        value.setToggle(!value.isToggle());
                        if (value.isToggle()) {
                            SoundUtils.playSound("enable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(value.getVisualName() + " has been enabled", 1500L, multiOption.getModule()).setPattern(Pattern.ENABLE));
                            continue;
                        }
                        SoundUtils.playSound("disable");
                        Load.getInstance().getHooks().getNotificationManagers().register(new Notification(value.getVisualName() + " has been disabled", 1500L, multiOption.getModule()).setPattern(Pattern.DISABLE));
                    }
                }
            }
            EventInput eventInput = new EventInput(key);
            EventManager.call(eventInput);
            Load.getInstance().getHooks().getMacroManagers().press(key);
        }
    }
}

