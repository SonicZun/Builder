import com.oocourse.elevator2.PersonRequest;

public class Tagrequest {
    private PersonRequest req;
    private boolean isOver;

    public Tagrequest(PersonRequest req) {
        this.req = req;
        this.isOver = false;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public PersonRequest getReq() {
        return req;
    }
}
