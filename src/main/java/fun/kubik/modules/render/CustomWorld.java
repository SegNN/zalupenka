/* Decompiler 17ms, total 667ms, lines 69 */
package fun.kubik.modules.render;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventTick;
import fun.kubik.events.main.packet.EventReceivePacket;
import fun.kubik.events.main.visual.EventFog;
import fun.kubik.helpers.render.ColorHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.api.Option;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.vector.Vector3d;

public class CustomWorld extends Module {
    private final MultiOption mode = new MultiOption("Mode", new MultiOptionValue[]{new MultiOptionValue("Time", true), new MultiOptionValue("Fog", true)});
    private final SelectOption time = (new SelectOption("Time", 0, new SelectOptionValue[]{new SelectOptionValue("Morning"), new SelectOptionValue("Day"), new SelectOptionValue("Evening"), new SelectOptionValue("Night")})).visible(() -> {
        return this.mode.getSelected("Time");
    });
    private final SliderOption distance = (new SliderOption("Distance Fog", 4.0F, 1.1F, 30.0F)).increment(0.1F).visible(() -> {
        return this.mode.getSelected("Fog");
    });

    public CustomWorld() {
        super("Custom World", Category.RENDER);
        this.settings(new Option[]{this.mode, this.time, this.distance});
    }

    @EventHook
    public void packet(EventReceivePacket event) {
        if (event.getPacket() instanceof SUpdateTimePacket && this.mode.getSelected("Time")) {
            event.setCancelled(true);
        }

    }

    @EventHook
    public void fog(EventFog event) {
        if (this.mode.getSelected("Fog")) {
            float[] color = ColorHelpers.getRGBAf(ColorHelpers.getThemeColor(2));
            Vector3d colors = new Vector3d((double)color[0], (double)color[1], (double)color[2]);
            event.setColor(colors);
            float fogDistance = 1.0F / (Float)this.distance.getValue();
            event.setDistance(fogDistance);
        }

    }

    @EventHook
    public void update(EventTick event) {
        if (this.mode.getSelected("Time")) {
            long targetTime = -1L;
            if (this.time.getSelected("Morning")) {
                targetTime = 23000L;
            } else if (this.time.getSelected("Day")) {
                targetTime = 6000L;
            } else if (this.time.getSelected("Evening")) {
                targetTime = 12000L;
            } else if (this.time.getSelected("Night")) {
                targetTime = 18000L;
            }
            if (targetTime != -1L) {
                mc.world.setDayTime(targetTime);
            }
        }
    }
}