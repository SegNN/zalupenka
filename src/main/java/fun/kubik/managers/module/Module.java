/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.module;

import com.google.gson.JsonObject;
import fun.kubik.Load;
import fun.kubik.events.api.EventManager;
import fun.kubik.helpers.animation.Animation;
import fun.kubik.helpers.interfaces.IFastAccess;
import fun.kubik.managers.client.ClientManagers;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.BindOption;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.ColorOption;
import fun.kubik.managers.module.option.main.DraggableOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.managers.module.option.main.StringOption;
import fun.kubik.managers.notification.api.Notification;
import fun.kubik.managers.notification.api.Pattern;
import fun.kubik.utils.client.SoundUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.math.vector.Vector2f;

public class Module
        implements IFastAccess {
    private final ArrayList<Option<?>> options = new ArrayList();
    private final String name;
    private final Animation animation = new Animation();
    private final Animation toggleFade = new Animation();
    private final Category category;
    private String description = "none";
    private boolean toggled;
    private boolean opened;
    private int currentKey = -1;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public boolean hasBind() {
        return this.currentKey != -1;
    }

    public void onEnabled() {
    }

    public void onDisabled() {
    }

    public void toggle() {
        if (!ClientManagers.isUnHook()) {
            boolean bl = this.toggled = !this.toggled;
            if (this.toggled) {
                EventManager.register(this);
//                System.out.println("Enable " + this.name);
                SoundUtils.playSound("enable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification(this.name + " \u0432\u043a\u043b\u044e\u0447\u0435\u043d", 1500L, this).setPattern(Pattern.ENABLE));
                this.onEnabled();
            } else {
                EventManager.unregister(this);
//                System.out.println("Disable " + this.name);
                SoundUtils.playSound("disable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification(this.name + " \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d", 1500L, this).setPattern(Pattern.DISABLE));
                this.onDisabled();
            }
        }
    }

    public List<Option<?>> getSettingList() {
        return this.getOptions();
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("state", this.isToggled());
        if (this.hasBind()) {
            object.addProperty("keyIndex", this.currentKey);
        }
        JsonObject propertiesObject = new JsonObject();
        JsonObject multiObject = new JsonObject();
        for (Option<?> option : this.getSettingList()) {
            if (this.getSettingList() != null) {
                if (option instanceof CheckboxOption) {
                    CheckboxOption checkboxOption = (CheckboxOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Boolean)checkboxOption.getValue());
                    propertiesObject.addProperty("bind", checkboxOption.getKey());
                } else if (option instanceof SelectOption) {
                    SelectOption selectOption = (SelectOption)option;
                    propertiesObject.addProperty(option.getSettingName(), ((SelectOptionValue)selectOption.getValue()).getName());
                } else if (option instanceof BindOption) {
                    BindOption bindOption = (BindOption)option;
                    propertiesObject.addProperty(option.getSettingName(), bindOption.getKey());
                } else if (option instanceof SliderOption) {
                    SliderOption sliderOption = (SliderOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Number)sliderOption.getValue());
                } else if (option instanceof MultiOption) {
                    MultiOption multiOption = (MultiOption)option;
                    propertiesObject.addProperty(option.getSettingName(), multiOption.selected());
                    for (MultiOptionValue value : multiOption.getValues()) {
                        multiObject.addProperty(value.getName(), value.getKey());
                    }
                    propertiesObject.add("Binded", multiObject);
                } else if (option instanceof StringOption) {
                    StringOption stringOption = (StringOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (String)stringOption.getValue());
                } else if (option instanceof ColorOption) {
                    ColorOption colorOption = (ColorOption)option;
                    propertiesObject.addProperty(option.getSettingName(), (Number)colorOption.getValue());
                } else if (option instanceof DraggableOption) {
                    DraggableOption draggableOption = (DraggableOption)option;
                    propertiesObject.addProperty(option.getSettingName(), ((Vector2f)draggableOption.getValue()).x + ":" + ((Vector2f)draggableOption.getValue()).y);
                }
            }
            object.add("Options", propertiesObject);
        }
        return object;
    }

    public void load(JsonObject object) {
        if (object != null) {
            if (object.has("state") && object.get("state").getAsBoolean()) {
                this.toggle();
            }
            this.currentKey = -1;
            if (object.has("keyIndex")) {
                this.currentKey = object.get("keyIndex").getAsInt();
            }
            for (Option<?> option : this.getSettingList()) {
                JsonObject propertiesObject = object.getAsJsonObject("Options");
                if (option == null || propertiesObject == null || !propertiesObject.has(option.getSettingName())) continue;
                if (option instanceof CheckboxOption) {
                    ((CheckboxOption)option).setValue(propertiesObject.get(option.getSettingName()).getAsBoolean());
                    ((CheckboxOption)option).setKey(propertiesObject.get("bind").getAsInt());
                    continue;
                }
                if (option instanceof BindOption) {
                    ((BindOption)option).setKey(propertiesObject.get(option.getSettingName()).getAsInt());
                    continue;
                }
                if (option instanceof SelectOption) {
                    for (SelectOptionValue value : ((SelectOption)option).getValues()) {
                        if (!value.getName().equals(propertiesObject.get(option.getSettingName()).getAsString())) continue;
                        ((SelectOption)option).setValue(value);
                    }
                    continue;
                }
                if (option instanceof SliderOption) {
                    ((SliderOption)option).setValue(Float.valueOf(propertiesObject.get(option.getSettingName()).getAsFloat()));
                    continue;
                }
                if (option instanceof MultiOption) {
                    String[] multiOptions = propertiesObject.get(option.getSettingName()).getAsString().split(", ");
                    for (MultiOptionValue value : ((MultiOption)option).getValues()) {
                        value.setToggle(false);
                        for (int i = 0; i < multiOptions.length; ++i) {
                            if (!value.getName().equals(multiOptions[i])) continue;
                            value.setToggle(true);
                        }
                    }
                    JsonObject multiObject = propertiesObject.getAsJsonObject("Binded");
                    for (MultiOptionValue value : ((MultiOption)option).getValues()) {
                        value.setKey(multiObject.get(value.getName()).getAsInt());
                    }
                    continue;
                }
                if (option instanceof StringOption) {
                    ((StringOption)option).setValue(propertiesObject.get(option.getSettingName()).getAsString());
                    continue;
                }
                if (option instanceof ColorOption) {
                    ((ColorOption)option).setValue(propertiesObject.get(option.getSettingName()).getAsInt());
                    continue;
                }
                if (!(option instanceof DraggableOption)) continue;
                String[] coords = propertiesObject.get(option.getSettingName()).getAsString().split(":");
                Vector2f value = new Vector2f(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
                ((DraggableOption)option).setValue(value);
            }
        }
    }

    public void settings(Option<?> ... options) {
        this.options.addAll(Arrays.asList(options));
    }

    @Generated
    public ArrayList<Option<?>> getOptions() {
        return this.options;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public Animation getToggleFade() {
        return this.toggleFade;
    }

    @Generated
    public Category getCategory() {
        return this.category;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public boolean isToggled() {
        return this.toggled;
    }

    @Generated
    public boolean isOpened() {
        return this.opened;
    }

    @Generated
    public int getCurrentKey() {
        return this.currentKey;
    }

    @Generated
    public void setDescription(String description) {
        this.description = description;
    }

    @Generated
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @Generated
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    @Generated
    public void setCurrentKey(int currentKey) {
        this.currentKey = currentKey;
    }
}

