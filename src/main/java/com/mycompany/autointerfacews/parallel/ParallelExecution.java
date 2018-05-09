/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.mycompany.autointerfacews.bioflow.BioFlowService;
import com.mycompany.autointerfacews.dao.EXist;
import com.mycompany.autointerfacews.dataIcon.AttrMatch;
import com.mycompany.autointerfacews.dataIcon.Edge;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.generator.InputGenerator;
import com.mycompany.autointerfacews.helper.WebResourceImageDownloader;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.http.client.HttpClient;

/**
 *
 * @author mou1609
 */
public class ParallelExecution implements Runnable {

    ExecutorService executorService;
    BioFlowService bioFlowService;
    EXist eXist;
    InputGenerator inputGenerator;
    Gson gson;
    WebResourceImageDownloader webResourceImageDownloader;

    Map<String, Future<?>> submittedTask;//concurent
    Map<String, Set<String>> nodeParents;//concurent
    List<String> tasks;//concurent
    Map<String, Node> nodes;
    List<Edge> edges;
    Queue<MyStatus> statuses;
    ScheduledFuture<?> future;
    HttpClient client;
    Boolean subClass;
    List<AttrMatch> globalmatch;
    Map<String, Map<String, Set<String>>> mappedRepeated;

    public ParallelExecution(ExecutorService executorService, BioFlowService bioFlowService, EXist eXist, InputGenerator inputGenerator, Gson gson, WebResourceImageDownloader webResourceImageDownloader,
            Map<String, Set<String>> nodeParents, List<String> tasks, Map<String, Node> nodes, List<Edge> edges, Queue<MyStatus> statuses, HttpClient client, List<AttrMatch> globalmatch,
            Map<String, Map<String, Set<String>>> mappedRepeated) {
        this.executorService = executorService;
        this.bioFlowService = bioFlowService;
        this.eXist = eXist;
        this.inputGenerator = inputGenerator;
        this.gson = gson;
        this.webResourceImageDownloader = webResourceImageDownloader;
        this.nodeParents = nodeParents;
        this.tasks = tasks;
        this.nodes = nodes;
        this.edges = edges;
        this.statuses = statuses;
        this.client = client;
        this.globalmatch = globalmatch;
        this.mappedRepeated = mappedRepeated;
        submittedTask = new ConcurrentHashMap<>();
        subClass = false;
    }

    public ParallelExecution(Map<String, Set<String>> nodeParents, List<String> tasks, Map<String, Node> nodes, List<Edge> edges, Queue<MyStatus> statuses) {
        submittedTask = new ConcurrentHashMap<>();
        this.nodeParents = nodeParents;
        this.tasks = tasks;
        this.nodes = nodes;
        this.edges = edges;
        this.statuses = statuses;
    }

    public void setSubClass(Boolean subClass) {
        this.subClass = subClass;
    }

    public void addNodeParents(Map<String, Set<String>> parents) {
        this.nodeParents.putAll(parents);
    }

    public void addTasks(List<String> newTs) {
        this.tasks.addAll(newTs);
    }

    public void addNodes(Map<String, Node> nodes) {
        this.nodes.putAll(nodes);
    }

    public void addEdges(List<Edge> edges) {
        this.edges.addAll(edges);
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    boolean suspended = false;

    public void suspend() {
        try {
//            Thread.currentThread().interrupt();
            suspended = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        try {
//            Thread.currentThread().resume();
            suspended = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            future.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeTasks(List<String> delTasks) {
        System.out.println("removetasks");
        System.out.println(delTasks);
        tasks.removeAll(delTasks);
        for (String t : delTasks) {
            nodeParents.remove(t);
        }
    }

    public synchronized void generateRepeatedTask(Map<String, Set<String>> nodeP) {
        Set<String> newT = new HashSet<>();

        nodeP.entrySet().stream().forEach(t -> {
            newT.add(t.getKey());
            newT.addAll(t.getValue());
        });
        //we add all back
        tasks.addAll(newT);
        //remove submittedTask
        Iterator<Map.Entry<String, Future<?>>> it = submittedTask.entrySet().iterator();
        while (it.hasNext()) {
            if (newT.contains(it.next().getKey())) {
                it.remove();
            }
        }
        //add nodep back
        for (Map.Entry<String, Set<String>> entry : nodeP.entrySet()) {
            nodeParents.putIfAbsent(entry.getKey(), new HashSet<>());
            nodeParents.get(entry.getKey()).addAll(entry.getValue());
        }

    }

    synchronized List<String> getRunningTaskIDs() {
        List<String> res = Collections.synchronizedList(new ArrayList<>());
        for (Map.Entry<String, Future<?>> entry : submittedTask.entrySet()) {
            if (entry.getValue().isDone()) {
                String taskID = entry.getKey();
                for (Map.Entry<String, Set<String>> keyPair : nodeParents.entrySet()) {
                    Set<String> parents = keyPair.getValue();
                    if (parents.contains(taskID)) {
                        parents.remove(taskID);
                    }
                }
            }
        }
        Iterator<Entry<String, Set<String>>> it = nodeParents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<String>> entry = it.next();
            if (entry.getValue().isEmpty()) {
                res.add(entry.getKey());
                it.remove();
            }
        }

        return res;
    }

    public void run() {
        if (suspended == true) {
            return;
        }

        System.out.println("************ ParallelExecution *******************");
        boolean flag = false;
//        System.out.println(executorService == null);
//        System.out.println(bioFlowService == null);
//        System.out.println(eXist == null);
//        System.out.println(inputGenerator == null); 
//        System.out.println(gson == null);
//        System.out.println(webResourceImageDownloader == null);
        System.out.println(submittedTask.size());
        System.out.println(nodeParents.size());
        System.out.println(tasks.size());
        System.out.println(((ThreadPoolExecutor) executorService).getActiveCount());
        System.out.println(((ThreadPoolExecutor) executorService).getCorePoolSize());
        System.out.println("************ ParallelExecution *******************");

        while (((ThreadPoolExecutor) executorService).getActiveCount() < ((ThreadPoolExecutor) executorService).getCorePoolSize()
                && ((ThreadPoolExecutor) executorService).getQueue().size() == 0 && !tasks.isEmpty()) {
            synchronized (submittedTask) {
                for (Map.Entry<String, Future<?>> t : submittedTask.entrySet()) {
                    if (!t.getValue().isDone()) {
                        flag = true;
                    } else {
                        Iterator<MyStatus> it = statuses.iterator();
                        while (it.hasNext()) {
                            MyStatus cur = it.next();
                            if (cur.getId().equals(t.getKey())) {
                                it.remove();
                            }
                        }
                    }
                }
            }
            List<String> taskIDs = getRunningTaskIDs();
//            System.out.println(taskIDs);
//            System.out.println(((ThreadPoolExecutor) executorService).getQueue());
//            System.out.println("*************************");
//            System.out.println(nodeParents);

            for (String taskID : taskIDs) {
                tasks.remove(taskID);
                Node cur = nodes.get(taskID);
//                    System.out.println("here");
//                    System.out.println(taskID);
                //status
                MyStatus status = new MyStatus();
                statuses.add(status);
                status.setId(taskID);
//                    System.out.println("here1");

                //add all
                List<String> inputs = new ArrayList<>();
                for (Edge edge : edges) {
                    if (edge.getTo().equals(cur.getId())) {
                        Node from = nodes.get(edge.getFrom());
                        if (from.getType().equals("data")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("combine")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("fusion")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("adapter")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("analytics")) {
                            System.out.println(from.getOutputs());
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("terminal")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("printer")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("IO")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("if")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("repeat")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("connect")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("waiton")) {
                            inputs.addAll(from.getOutputs());
                        }
                        if (from.getType().equals("nested")) {
                            inputs.addAll(from.getOutputs());
                        }
                    }
                }
//                    System.out.println("herer23re");
//                System.out.println(inputs);
                cur.setInputs(inputs);
//                System.out.println("hererere");
                Callable<String> curThread = null;
                //deal with nodes      
                try {
                    if (cur.getType().equals("data")) {
                        status.addMessage("data icon id:" + cur.getId());
                        cur.setOutputs(cur.getInputs());
                        curThread = new ReadData(cur, globalmatch);
                    } else if (cur.getType().equals("adapter")) {
                        status.addMessage("adapter icon id:" + cur.getId());
                        curThread = new AdapterData(cur, status, bioFlowService, eXist, inputGenerator);
                    } else if (cur.getType().equals("analytics")) {
                        status.addMessage("analytics icon id:" + cur.getId());
                        curThread = new AnalyticsData(cur, status, bioFlowService, eXist, inputGenerator);
                    } else if (cur.getType().equals("combine")) {
                        status.addMessage("combine icon id:" + cur.getId());
//                                curThread = new CombineData(cur, bioFlowService, new InputGenerator());
                        curThread = new CombineData(cur, bioFlowService, eXist, inputGenerator);
                    } else if (cur.getType().equals("fusion")) {
                        status.addMessage("fusion icon id:" + cur.getId());
                        curThread = new FusionData(cur, bioFlowService, eXist, inputGenerator);
                    } else if (cur.getType().equals("if")) {
                        status.addMessage("if icon id:" + cur.getId());
                        curThread = new IfData(cur, status, bioFlowService, eXist);
                    } else if (cur.getType().equals("repeat")) {
                        status.addMessage("connect icon id:" + cur.getId());
                        curThread = new RepeatData(cur, status, mappedRepeated);
                    } else if (cur.getType().equals("printer")) {
                        status.addMessage("printer icon id:" + cur.getId());
                        curThread = new PrinterData(cur, gson);
                        System.out.println("printer");
                    } else if (cur.getType().equals("terminal")) {
                        status.addMessage("terminal icon id:" + cur.getId());
                        System.out.println("terminal icon id:" + cur.getId());
                        curThread = new PrintToTerminal(cur, status, gson, webResourceImageDownloader);
                    } else if (cur.getType().equals("library")) {
                        status.addMessage("library icon id:" + cur.getId());
                        curThread = new ParallelLibrary();
                    } else if (cur.getType().equals("nested")) {
                        status.addMessage("nested icon id:" + cur.getId());
                        curThread = new ProcedureData(cur, status, gson, client);
                    } else if (cur.getType().equals("IO")) {
                        status.addMessage("nested icon id:" + cur.getId());
                        curThread = new GeneralIOData(cur, status);
                    } else if (cur.getType().equals("connect")) {
                        status.addMessage("connect icon id:" + cur.getId());
                        curThread = new ConnectData(cur, status);
                    } else if (cur.getType().equals("waiton")) {
                        status.addMessage("connect icon id:" + cur.getId());
                        curThread = new WaitonData(cur, status);
                    } else {
                        System.out.println("***********something wrong*************************");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nodes.put(taskID, cur);
//                System.out.println(cur);
//                System.out.println("execution****************");
//                System.out.println(curThread);
                if (curThread != null) {
                    Future<?> f = executorService.submit(curThread);
                    submittedTask.put(taskID, f);
                }
            }
        }

        if (tasks.isEmpty() && flag == false) {
            future.cancel(true);
            if (!subClass) {
                statuses.clear();
            }

        }

    }
}
