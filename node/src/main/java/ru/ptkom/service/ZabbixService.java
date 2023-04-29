package ru.ptkom.service;

public interface ZabbixService {

    public String getUnavailableHosts();

    public String getHostOutagesStatisticById(String hostId);
    public String getListOfHostGraphsById(String hostId);
    public String getFullAlertInfo(String eventId);
    public byte[] getGraphById(String graphId);

}
