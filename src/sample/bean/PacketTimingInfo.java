package sample.bean;

import java.util.Vector;

public class PacketTimingInfo {
    private double produce_time;
    private int packet_id;
    private int flow_id;
    private double consume_time;
    private Vector<Integer> flit_ids=new Vector<>();


    public double getProduce_time() {
        return produce_time;
    }

    public void setProduce_time(double produce_time) {
        this.produce_time = produce_time;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int paket_id) {
        this.packet_id = paket_id;
    }

    public int getFlow_id() {
        return flow_id;
    }

    public void setFlow_id(int flow_id) {
        this.flow_id = flow_id;
    }

    public Vector<Integer> getFlit_ids() {
        return flit_ids;
    }

    public void setFlit_ids(Vector<Integer> flit_ids) {
        this.flit_ids = flit_ids;
    }

    public double getConsume_time() {
        return consume_time;
    }

    public void setConsume_time(double consume_time) {
        this.consume_time = consume_time;
    }
}
