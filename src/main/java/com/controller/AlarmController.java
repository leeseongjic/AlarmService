package com.controller;

import com.queue.WorkProcess;
import com.service.AlarmInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmInterface alarmInterface;
    private final WorkProcess workProcess;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() {
        workProcess.getServiceLogic(alarmInterface);
        workProcess.execute(new String[]{"service"});
    }
}