//package fun.kubik.utils.tps;
//
//import fun.kubik.events.api.EventHook;
//import fun.kubik.events.main.EventPacket;
//import net.minecraft.util.math.MathHelper;
//import java.util.Arrays;
//
//public class ServerTPS {
//    protected final float[] ticks = new float[20];
//    protected int index;
//    protected long lastPacketTime;
//
//    public ServerTPS() {
//        this.index = 0;
//        this.lastPacketTime = -1L;
//        Arrays.fill(ticks, 0.0F);
//      //  System.out.println("[DEBUG] ServerTPS backup instance initialized");
//    }
//
//    public float getTPS() {
//        float numTicks = 0.0F;
//        float sumTickRates = 0.0F;
//        for (float tickRate : ticks) {
//            if (tickRate > 0.0F) {
//                sumTickRates += tickRate;
//                numTicks++;
//            }
//        }
//        float calculatedTPS = MathHelper.clamp(0.0F, 20.0F, sumTickRates / numTicks);
//      //  System.out.println("[DEBUG] Backup TPS calculated: " + calculatedTPS);
//        return calculatedTPS;
//    }
//
//    private void update() {
//     //   System.out.println("[DEBUG] Backup update called");
//        if (this.lastPacketTime != -1L) {
//            float timeElapsed = (float) (System.currentTimeMillis() - this.lastPacketTime) / 1000.0F;
//         //   System.out.println("[DEBUG] Time elapsed since last packet: " + timeElapsed + "s");
//
//            float newTickValue = MathHelper.clamp(0.0F, 20.0F, 20.0F / timeElapsed);
//            ticks[this.index % ticks.length] = newTickValue;
//        //    System.out.println("[DEBUG] New tick value [" + (this.index % ticks.length) + "]: " + newTickValue);
//
//            this.index++;
//        } else {
//          //  System.out.println("[DEBUG] First packet received, skipping calculation");
//        }
//        this.lastPacketTime = System.currentTimeMillis();
//    }
//
//    @EventHook
//    public void onPacket(EventPacket e) {
//       // System.out.println("[DEBUG] Backup packet handler received packet");
//        update();
//    }
//}