import com.oocourse.TimableOutput;

public class Main extends Thread {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Thread treqProducer = new Thread(new ReqProducer());
        Thread televator = new Thread(new Elevator());
        treqProducer.start();
        televator.start();
    }
}
