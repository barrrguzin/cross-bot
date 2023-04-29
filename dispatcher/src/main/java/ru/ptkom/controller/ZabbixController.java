package ru.ptkom.controller;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ptkom.service.ZabbixAlertProducer;



@RestController
public class ZabbixController {

    private final ZabbixAlertProducer zabbixAlertProducer;

    public ZabbixController(ZabbixAlertProducer zabbixAlertProducer) {
        this.zabbixAlertProducer = zabbixAlertProducer;
    }

    @PostMapping("/zabbix")
    public void getAlertFromZabbix(HttpEntity<String> httpEntity) {
        zabbixAlertProducer.produce(httpEntity.getBody());
    }
}
