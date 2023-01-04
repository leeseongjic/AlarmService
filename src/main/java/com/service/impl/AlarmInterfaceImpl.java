package com.service.impl;

import com.service.AlarmInterface;
import com.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmInterfaceImpl implements AlarmInterface {

    private final RestTemplate restTemplate;
    private final TelegramService telegramService;

    @Override
    public void alarmService() {
        try {
            boolean chk = false;
            HashMap<String, Integer> failMap = new HashMap<>();
            String[][] runningChk = {
                    {"http://10.251.1.236:8080/crm/manualManager/healthCheck", "crm_api_236", "crmError"}
            };
            telegramService.sendTestAlarm("Test");
//            failCount(runningChk, failMap, 1);
//            Thread.sleep(15000);
//            failCount(runningChk, failMap, 2);
//            Thread.sleep(15000);
//            failCount(runningChk, failMap, 3);
        } catch (Exception e) {
            log.error("RebuildService serviceHealthCheck {}", e);
        }
    }

    private void failCount(String[][] runningChk, HashMap<String, Integer> failMap, int count) {
        for (String[] row : runningChk) {
            try {
                String response = restTemplate.getForObject(row[0], String.class);
                if (!response.equals("running")) {
                    failMap.merge(row[1], 1, Integer::sum);
                }
            } catch (Exception e) {
                log.info("{} error - {}", row[1], e);
                failMap.merge(row[1], 1, Integer::sum);
            }
            if (count == 3) {
                int getCount = failMap.get(row[1]) == null ? 0 : failMap.get(row[1]);
                if (getCount == 3) {
                    telegramService.sendHealthCheck(row);
                }
            }
        }
    }
}
