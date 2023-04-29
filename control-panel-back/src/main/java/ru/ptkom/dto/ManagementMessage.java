package ru.ptkom.dto;

public class ManagementMessage {

    String action;

    String parameter;


    public ManagementMessage(String action, String parameter) {
        this.action = action;
        this.parameter = parameter;
    }

    public ManagementMessage(String action) {
        this.action = action;
    }

    public ManagementMessage() {}

    @Override
    public String toString() {
        return "ManagementMessage{" +
                "action='" + action + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
