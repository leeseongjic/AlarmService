package com.queue;

import com.enums.HealthCheckEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.service.AlarmInterface;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkProcess {

    private AlarmInterface alarmInterface;

    private PoolWorker[] threads;
    private final LinkedList<String[]> queue;

    public WorkProcess() {
        this.queue = new LinkedList<>();
        bindThread();
    }

    public void execute(String[] healthInfo) {

        for (int i = 0; i < 30; i++) {
            try {
                if (!threads[i].isAlive()) {
                    saveThread(i);
                }
            } catch (Exception e) {
                saveThread(i);
                continue;
            }
        }

        synchronized (queue) {
            try {
                queue.addLast(healthInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            queue.notify();
        }
    }

    private class PoolWorker extends Thread {

        @Override
        public void run() {
            String[] healthInfo;
            try {
                while (true) {
                    synchronized (queue) {
                        while (queue.isEmpty()) {
                            queue.wait();
                        }
                        healthInfo = queue.removeFirst();
                    }
                    if (healthInfo[0].equals(HealthCheckEnum.SERVICE.getHealthCheckId())) {
                        alarmInterface.alarmService();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveThread(int index) {
        threads[index] = new PoolWorker();
        threads[index].setName(String.format("%s-%s", "healthCheck-thread-pool", index));
        threads[index].start();
    }

    private void bindThread() {
        this.threads = new PoolWorker[30];

        for (int i = 0; i < 30; i++) {
            threads[i] = new PoolWorker();
            threads[i].setName(String.format("%s-%s", "healthCheck-thread-pool", i));
            threads[i].start();
        }
    }

    public void getServiceLogic(AlarmInterface alarmInterface) {
        this.alarmInterface = alarmInterface;
    }
}
