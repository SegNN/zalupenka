//package fun.kubik.utils.tps;
//
//import lombok.Getter;
//
//@Getter
//public class TPSCalc {
//    private float tps = 20;
//
//    public float getTPS() {
//    //    System.out.println("[DEBUG] TPSCalc getTPS() called, returning: " + tps);
//        return tps;
//    }
//
//    public void updateTPS() {
//        long currentTime = System.currentTimeMillis();
//     //   System.out.println("[DEBUG] TPSCalc update called at: " + currentTime);
//
//        if (currentTime % 2 == 0) {
//            tps = 18.5f;
//        } else {
//            tps = 19.8f;
//        }
//    //    System.out.println("[DEBUG] New TPSCalc value: " + tps);
//    }
//}