package fun.kubik.utils;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventReceivePacket;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;

public class ServerTPS {
    private static final float MAX_TPS = 20.0F;
    private static final float NANOSECONDS_PER_TICK = 50_000_000F; // 50ms per tick
    private static final long TIMEOUT_NANOS = 5_000_000_000L; // 5 seconds
    private static final long TIMEOUT_MS = 3000; // 3 секунды без пакетов = сервер лагает
    private static final ServerTPS INSTANCE = new ServerTPS();

    private float tps = 20.0F;
    private long lastTime = System.nanoTime();
    private long tickCount = 0;
    private long lastPacketTime = System.currentTimeMillis();
    private boolean gotUpdateTimePacket = false;

    public ServerTPS() {
        System.out.println("[ServerTPS] Instance created");
    }

    public static ServerTPS getInstance() {
        System.out.println("[ServerTPS] getInstance called");
        return INSTANCE;
    }

    private void update() {
        long now = System.nanoTime();
        long elapsed = now - lastTime;
        if (elapsed > TIMEOUT_NANOS) {
            tps = 20.0F;
            tickCount = 0;
            lastTime = now;
            return;
        }
        if (elapsed > 0) {
            float rawTPS = (tickCount * NANOSECONDS_PER_TICK) / (elapsed / 1_000_000_000F);
            this.tps = (float) round(MathHelper.clamp(rawTPS, 0.0F, MAX_TPS));
            System.out.println("[ServerTPS] New TPS calculated: " + tps);
        }
        tickCount = 0;
        lastTime = now;
    }

    @EventHook
    public void onPacket(EventReceivePacket event) {
        IPacket<?> packet = event.getPacket();
        if (packet == null) return;
        System.out.println("[ServerTPS] Packet received: " + packet.getClass().getName());
        if (packet instanceof SUpdateTimePacket) {
            gotUpdateTimePacket = true;
            tickCount++;
            lastPacketTime = System.currentTimeMillis();
            System.out.println("[ServerTPS] SUpdateTimePacket detected, tickCount: " + tickCount);
            if (tickCount >= 20) {
                System.out.println("[ServerTPS] Updating TPS (20 ticks reached)");
                update();
            }
        } else if (!gotUpdateTimePacket) {
            // Fallback: если сервер не шлёт SUpdateTimePacket, считаем TPS по любому пакету
            tickCount++;
            lastPacketTime = System.currentTimeMillis();
            if (tickCount >= 20) {
                System.out.println("[ServerTPS] Fallback TPS update (20 packets, no SUpdateTimePacket)");
                update();
            }
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static float getTPS() {
        long now = System.currentTimeMillis();
        if (now - INSTANCE.lastPacketTime > TIMEOUT_MS) {
          //  System.out.println("[ServerTPS] Timeout detected, returning -1");
            return -1.0f; // Для HUD: показывать '???'
        }
        System.out.println("[ServerTPS] Returning TPS: " + INSTANCE.tps);
        return INSTANCE.tps;
    }
}