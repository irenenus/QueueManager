package victor.pacheco.queuemanager;

public class Queue {

    private String queue_name;
    private Integer slot_time, hour, min;

    Queue() {}

    public Queue(String queue_name, Integer slot_time, Integer hour, Integer min) {
        this.queue_name = queue_name;
        this.slot_time = slot_time;
        this.hour = hour;
        this.min = min;
    }

    public String getQueue_name() {
        return queue_name;
    }

    public void setQueue_name(String queue_name) {
        this.queue_name = queue_name;
    }

    public Integer getSlot_time() {
        return slot_time;
    }

    public void setSlot_time(Integer slot_time) {
        this.slot_time = slot_time;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

}
