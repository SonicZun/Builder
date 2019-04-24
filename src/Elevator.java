import com.oocourse.TimableOutput;

import java.util.ArrayList;

public class Elevator implements Runnable {
    private ArrayList<Tagrequest> upGroup;
    private ArrayList<Tagrequest> downGroup;
    private ArrayList<Tagrequest> currentGroup;
    private Scheduler scheduler;
    private int floor;
    private boolean isOpen;

    public Elevator() {
        upGroup = new ArrayList<Tagrequest>();
        downGroup = new ArrayList<Tagrequest>();
        scheduler = Scheduler.getInstance();
        currentGroup = upGroup;
        floor = 1;
        isOpen = false;
    }

    private void print(String status) throws InterruptedException {
        if (status.equals("open")) {
            TimableOutput.println(String.format("OPEN-%d\n", floor));
        } else if (status.equals("close")) {
            TimableOutput.println(String.format("CLOSE-%d\n", floor));
        } else if (status.equals("arrive")) {
            TimableOutput.println(String.format("ARRIVE-%d\n", floor));
        }
    }

    private void print(String status, int id) throws InterruptedException {
        if (status.equals("in")) {
            TimableOutput.println(String.format("IN-%d-%d\n", id, floor));
        } else if (status.equals("out")) {
            TimableOutput.println(String.format("OUT-%d-%d\n", id, floor));
        }
    }

    private void release() throws InterruptedException {
        for (int i = 0; i < currentGroup.size(); i++) {
            if (currentGroup.get(i).getReq().getToFloor() == floor) {
                print("out", currentGroup.get(i).getReq().getPersonId());
                currentGroup.remove(i);
                i--;
            }
        }
    }

    private void absorb() throws InterruptedException {
        ArrayList<Tagrequest> tmp;
        tmp = scheduler.get(floor, "up");
        for (Tagrequest tagreq : tmp) {
            print("in", tagreq.getReq().getPersonId());
        }
        this.upGroup.addAll(tmp);
        tmp = scheduler.get(floor, "down");
        for (Tagrequest tagreq : tmp) {
            print("in", tagreq.getReq().getPersonId());
        }
        this.downGroup.addAll(tmp);
    }

    private void move(String direction) throws InterruptedException {
        Thread.sleep(400);
        if (direction.equals("up")) {
            if (this.floor == -1) {
                this.floor += 2;
            }
            else {
                this.floor += 1;
            }
        }
        else {
            if (this.floor == 1) {
                this.floor -= 2;
            }
            else {
                this.floor -= 1;
            }
        }
    }

    private boolean somebodyArrive() {
        boolean tmp = false;
        for (Tagrequest request : currentGroup) {
            if (request.getReq().getToFloor() == this.floor) {
                tmp = true;
                break;
            }
        }
        return tmp;
    }

    private void open() throws InterruptedException {
        print("open");
        this.isOpen = true;
    }

    private void close() throws InterruptedException {
        print("close");
        this.isOpen = false;
    }

    public void run() {
        try {
            while (true) {
                if (somebodyArrive()) {
                    open();
                    release();
                    if (currentGroup.isEmpty()) {
                        scheduler.reset();
                    }
                }
                if (currentGroup.isEmpty()) {
                    scheduler.scheduleDirection(floor);
                }
                if (scheduler.getDirection().equals("stop")) {
                    if (!upGroup.isEmpty() || !downGroup.isEmpty()) {
                        if (!upGroup.isEmpty()) {
                            currentGroup = upGroup;
                            scheduler.setDirection("up");
                        }
                        if (!downGroup.isEmpty()) {
                            currentGroup = downGroup;
                            scheduler.setDirection("down");
                        }
                    } else {
                        if (this.isOpen) {
                            //actually this situation it must be opening
                            Thread.sleep(400);
                            close();
                        }
                        Boolean over = scheduler.elevatorWait();
                        if (over) {
                            break;
                        }
                        scheduler.scheduleDirection(floor);
                    }

                }
                if (scheduler.getDirection().equals("up")) {
                    currentGroup = upGroup;
                } else {
                    currentGroup = downGroup;
                }
                if (scheduler.inquire(floor, scheduler.getDirection())) {
                    if (!this.isOpen) {
                        open();
                    }
                    Thread.sleep(400);
                    absorb();
                    close();
                } else {
                    if (this.isOpen) {
                        Thread.sleep(400);
                        close();
                    }
                }
                move(scheduler.getDirection());
                print("arrive");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
