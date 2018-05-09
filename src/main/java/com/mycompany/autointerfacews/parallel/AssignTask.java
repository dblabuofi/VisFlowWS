/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author mou1609
 */
public class AssignTask implements Runnable {

    ExecutorService executorService;
    Queue<TaskName> tasks;
    List<Future<?>> submittedTask;
    ScheduledFuture<?> future;

    public AssignTask(ExecutorService executorService, Queue<TaskName> tasks) {
        this.executorService = executorService;
        this.tasks = tasks;
        submittedTask = Collections.synchronizedList(new ArrayList<>());

    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    
    
    public void run() {

        System.out.println("*********** Run ************");
        System.out.println(tasks.size());
        boolean flag = false;
        synchronized (submittedTask) {
            for (Future<?> t : submittedTask) {
                if (!t.isDone()) {
                    flag = true;
                }
            }
        }
        System.out.println(((ThreadPoolExecutor) executorService).getActiveCount());
        System.out.println(((ThreadPoolExecutor) executorService).getCorePoolSize());
        System.out.println("***********************");

        //make sure the queue has no running task
        while (((ThreadPoolExecutor) executorService).getActiveCount() < ((ThreadPoolExecutor) executorService).getCorePoolSize()
                && ((ThreadPoolExecutor) executorService).getQueue().size() == 0 && !tasks.isEmpty()) {
//            System.out.println("while ");
//            System.out.println(((ThreadPoolExecutor) executorService).getActiveCount());
//            System.out.println(((ThreadPoolExecutor) executorService).getCorePoolSize());
//            System.out.println("while done");
            TaskName t = tasks.remove();

            Future<?> f = executorService.submit(newCallable(t.getName(), t.getDelay()));
            submittedTask.add(f);
        }

        System.out.println(tasks.size());
        System.out.println(flag);
        if (tasks.isEmpty() && flag == false) {
//            executorService.shutdown();
            future.cancel(true);
        }
    }

    private Callable<Integer> newCallable(String name, int delay) {
        return new TaskRun(name, delay);
    }

    private class TaskRun implements Callable<Integer> {

        String name;
        Integer delay;

        public TaskRun(String name, Integer delay) {
            this.name = name;
            this.delay = delay;
        }

        public String getName() {
            return name;
        }

        public Integer getDelay() {
            return delay;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println(name);
            sleep(delay);
            System.out.println(name + " Done");
            return 10;
        }

        private void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }
}
