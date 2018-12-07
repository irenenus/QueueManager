package victor.pacheco.queuemanager;

public class User {
    private String usr_id;
    private int waiting_time;

    public User(String usr_id, int waiting_time) {
        this.usr_id = usr_id;
        this.waiting_time = waiting_time;
    }

    public int getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(int waiting_time) {
        this.waiting_time = waiting_time;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

}
