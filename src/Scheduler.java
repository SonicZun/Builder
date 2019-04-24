import java.util.ArrayList;

public class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler(19);
    private ArrayList<Floor> floors;
    private boolean isbuildingEmpty;
    private int maxFloor;
    private String direction;
    private boolean inputOver;

    private Scheduler(int n) {
        this.floors = new ArrayList<Floor>();
        for (int i = 0; i < n; i++) {
            Floor floor = new Floor();
            floors.add(floor);
        }
        this.isbuildingEmpty = true;
        this.maxFloor = n - 1;
        this.direction = "stop";
        this.inputOver = false;
    }

    public static Scheduler getInstance() {
        return INSTANCE;
    }

    public synchronized void setDirection(String direction) {
        this.direction = direction;
    }

    private int map(int n) {
        if (n < 0) {
            return n + 3;
        }
        else {
            return n + 2;
        }
    }

    public synchronized ArrayList<Tagrequest>
        get(int vfloor, String direction) {
        ArrayList<Tagrequest> tmp = floors.get(map(vfloor)).get(direction);
        if (isbuildingEmpty()) {
            this.isbuildingEmpty = true;
        }
        return tmp;
    }

    public synchronized void put(Tagrequest req) {
        if (req.isOver()) {
            this.inputOver = true;
            notifyAll();
            return;
        }
        int pfloor = map(req.getReq().getFromFloor());
        floors.get(pfloor).put(req);
        this.isbuildingEmpty = false;
        notifyAll();
    }

    private synchronized boolean isbuildingEmpty() {
        boolean tmp = true;
        for (Floor floor : this.floors) {
            tmp = tmp && floor.isEmpty();
        }
        return tmp;
    }

    public synchronized boolean elevatorWait() throws InterruptedException {
        if (this.inputOver && isbuildingEmpty) {
            return true;
        }
        wait();
        if (this.inputOver && isbuildingEmpty) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized String getDirection() {
        return direction;
    }

    public synchronized int highestRequest() {
        int i;
        for (i = maxFloor; i >= 0; i--) {
            if (!this.floors.get(i).isEmpty()) {
                break;
            }
        }
        return i;
    }

    public synchronized int lowestRequest() {
        int i;
        for (i = 0; i <= maxFloor; i++) {
            if (!this.floors.get(i).isEmpty()) {
                return i;
            }
        }
        return i;
    }

    public synchronized void reset() {
        this.direction = "stop";
    }

    public synchronized boolean inquire(int vfloor, String direction) {
        int pfloor = map(vfloor);
        if (direction.equals("up")) {
            return floors.get(pfloor).hasUp();
        }
        else {
            return floors.get(pfloor).hasDown();
        }
    }

    public synchronized void scheduleDirection(int vfloor) {
        int pfloor = map(vfloor);
        if (this.isbuildingEmpty) {
            this.direction = "stop";
        } else if (pfloor == maxFloor || pfloor == 0) {
            if (pfloor == maxFloor) {
                this.direction = "down";
            } else {
                this.direction = "up";
            }
        } else if (this.direction.equals("up") && pfloor < highestRequest()
            || this.direction.equals("down") && pfloor > lowestRequest()) {
            return;
        } else if (this.direction.equals("up") && pfloor >= highestRequest()) {
            if (pfloor > highestRequest()) {
                this.direction = "down";
            } else { //涉及到上下的选择时：如果当前楼层高，就算上也上不了几层，
                if (floors.get(pfloor).hasUp()) {
                    this.direction = "up";
                } else {
                    this.direction = "down";
                }
            }
        } else if (this.direction.equals("down") && pfloor <= lowestRequest()) {
            if (pfloor < lowestRequest()) {
                this.direction = "up";
            } else {
                if (floors.get(pfloor).hasDown()
                    /*&& pfloor > this.maxFloor / 2*/) {
                    this.direction = "down";
                } else {
                    this.direction = "up";
                }
            }
        } else if (this.direction.equals("stop")) {
            if (pfloor < lowestRequest()) {
                this.direction = "up";
            } else if (pfloor == lowestRequest()) {
                if (floors.get(pfloor).hasDown()) {
                    this.direction = "down";
                } else {
                    this.direction = "up";
                }
            } else if (lowestRequest() < pfloor && pfloor < highestRequest()) {
                if (pfloor - lowestRequest() < highestRequest() - pfloor) {
                    this.direction = "down";
                } else {
                    this.direction = "up";
                }
            } else if (pfloor == highestRequest()) {
                if (floors.get(pfloor).hasUp()) {
                    this.direction = "up";
                } else {
                    this.direction = "down";
                }
            } else {
                this.direction = "down";
            }
        }
    }
}
