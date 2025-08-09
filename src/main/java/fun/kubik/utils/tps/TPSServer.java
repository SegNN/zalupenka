//package fun.kubik.utils.tps;
//
//import lombok.Getter;
//import java.util.Locale;
//
//@Getter
//public class TPSServer {
//    private final TPSCalc tpsCalc;
//
//    public TPSServer() {
//        this.tpsCalc = new TPSCalc();
//       // System.out.println("[DEBUG] TPSServer initialized");
//    }
//
//    public String getFormattedTPS() {
//        float tps = tpsCalc.getTPS();
//        String formatted = String.format(Locale.US, "%.2f", tps);
//      //  System.out.println("[DEBUG] Formatted TPS: " + formatted);
//        return formatted;
//    }
//}