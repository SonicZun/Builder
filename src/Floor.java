import java.util.ArrayList;

public class Floor {
    private ArrayList<Tagrequest> upGroup;
    private ArrayList<Tagrequest> downGroup;

    public Floor() {
        upGroup = new ArrayList<Tagrequest>();
        downGroup = new ArrayList<Tagrequest>();
    }

    public synchronized ArrayList<Tagrequest> get(String direction) {
        ArrayList<Tagrequest> tmp;
        if (direction.equals("up")) {
            tmp = upGroup;
        }
        else {
            tmp = downGroup;
        }
        ArrayList<Tagrequest> tray = new ArrayList<Tagrequest>();
        tray.addAll(tmp);
        tmp.clear();
        return tray;
    }

    public synchronized void put(Tagrequest req) {
        if (req.getReq().getFromFloor() < req.getReq().getToFloor()) {
            upGroup.add(req);
        }
        else {
            downGroup.add(req);
        }
    }

    public synchronized boolean isEmpty() {
        return upGroup.isEmpty() && downGroup.isEmpty();
    }

    public synchronized boolean hasUp() {
        return !upGroup.isEmpty();
    }

    public synchronized boolean hasDown() {
        return !downGroup.isEmpty();
    }
}
