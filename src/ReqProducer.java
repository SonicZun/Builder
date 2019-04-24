import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;

public class ReqProducer implements Runnable {
    private Scheduler scheduler;

    public ReqProducer() {
        this.scheduler = Scheduler.getInstance();
    }

    public void run() {
        try {
            ElevatorInput elevatorInput = new ElevatorInput(System.in);
            while (true) {
                PersonRequest request = elevatorInput.nextPersonRequest();
                Tagrequest tagreq = new Tagrequest(request);
                // when request == null
                // it means there are no more lines in stdin
                if (request == null) {
                    tagreq.setOver(true);
                    scheduler.put(tagreq);
                    break;
                } else {
                    // a new valid request
                    tagreq.setOver(false);
                    scheduler.put(tagreq);
                }
            }
            elevatorInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
