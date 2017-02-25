package com.qubuss.test.sqlsynstest;

/**
 * Created by qubuss on 25.02.2017.
 */

public class Contact {

    private String name;
    private int sync_status;

    public Contact(String name, int sync_status) {
        this.name = name;
        this.sync_status = sync_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
