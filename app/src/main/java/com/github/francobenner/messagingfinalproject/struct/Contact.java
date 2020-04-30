package com.github.francobenner.messagingfinalproject.struct;

public class Contact {

    private String name;
    private String ip;

    public Contact(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }
}
