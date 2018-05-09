/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.bioflow;

import com.mycompany.autointerfacews.dataIcon.Action;
import com.mycompany.autointerfacews.dataIcon.MyResource;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.MyGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mou1609
 */
public class BioFlowParallelStatements {
    MyGraph myGraph;
    Map<String, Node> nodes;
    List<BioFlowParallelStatement> statements;
    
    public BioFlowParallelStatements(MyGraph myGraph,  Map<String, Node> nodes) {
        this.myGraph = myGraph;
        this.nodes = nodes;
        this.statements = new ArrayList<>();
    }

    public List<BioFlowParallelStatement> getStatements() {
        return statements;
    }
    
    public List<BioFlowParallelStatement> generateParallelStatement() throws CodeException {
        Map<String, String> resFrom = new HashMap<>();//<resId, nodeId> nodeId generate resId
        Map<String, Set<String>> resTo = new HashMap<>();//<resId, nodeId> nodeId needs resId
        //dependenceMap
        Map<String, Set<String>> dependenceMap = new HashMap<>();
        //directGraph
        Map<String, Set<String>> directGraph = new HashMap<>();
        
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            //we use output file names or input file names
            Node node = entry.getValue();
            List<Action> actions = node.getActions();
            String nodeId = entry.getKey();
            if (actions == null) {//library and data icon
                directGraph.put(nodeId, myGraph.getNodeTo(nodeId));
                continue;
            }
            for (Action action : actions) {
                List<String> inputFileNames = action.getInputFileNames();
                List<String> outputFileNames = action.getOutputFileNames();
                if (inputFileNames != null && !inputFileNames.isEmpty()) {
                    for (String fileName : inputFileNames) {
                        if (resTo.containsKey(fileName)) {
                            Set<String> outputs = resTo.get(fileName);
                            outputs.add(nodeId);
                        } else {
                            Set<String> outputs = new HashSet<>();
                            outputs.add(nodeId);
                            resTo.put(fileName, outputs);
                        }
                    }
                } else {//inputFileName is empty but targetResource is not empty we need update this it's a bug
                    
                    if (node.getType().equals("analytics")) {
                        MyResource t = action.getTargetResource();
                        if (t != null) {
                            List<MyAttribute> attributes = t.getAttributes();
                            if (attributes != null) {
                                for (MyAttribute attr : attributes) {
                                    String from = attr.getFrom();
                                    if (!from.equals("default")) {
                                        String fileName = from;
                                        if (resTo.containsKey(fileName)) {
                                            Set<String> outputs = resTo.get(fileName);
                                            outputs.add(nodeId);
                                        } else {
                                            Set<String> outputs = new HashSet<>();
                                            outputs.add(nodeId);
                                            resTo.put(fileName, outputs);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (node.getType().equals("fusion") || node.getType().equals("combine") || node.getType().equals("adapter")) {
                        /*
                        need clean this code when you have time, action should include all the input output files 
                        */
                        List<MyResource> resIn = node.getResourcesIn();
                        
                        for (MyResource res : resIn) {
                            String fileName = res.getUrlReturnFileName();
                            if (resTo.containsKey(fileName)) {
                                Set<String> outputs = resTo.get(fileName);
                                outputs.add(nodeId);
                            } else {
                                Set<String> outputs = new HashSet<>();
                                outputs.add(nodeId);
                                resTo.put(fileName, outputs);
                            }
                        }
                    }
                }
                if (outputFileNames != null && !outputFileNames.isEmpty()) {
                    if (entry.getValue().getType().equals("terminal")) {//just for terminal
                        /*
                        need clean this code when you have time, action should include all the input output files 
                        */
                        for (String fileName : outputFileNames) {
                            if (resTo.containsKey(fileName)) {
                                Set<String> outputs = resTo.get(fileName);
                                outputs.add(nodeId);
                            } else {
                                Set<String> outputs = new HashSet<>();
                                outputs.add(nodeId);
                                resTo.put(fileName, outputs);
                            }
                        }
                    } else {
                        for (String fileName : outputFileNames) {
                            if (resFrom.containsKey(fileName)) {
                                System.out.println(fileName);
                                throw new CodeException("no two same file names in one workflow");
                            } else {
                                resFrom.put(fileName, nodeId);
                            }
                        }
                    }
                }
            }
            
        }
        
        for (Map.Entry<String, String> entry : resFrom.entrySet()) {
            String resId = entry.getKey();
            String nodeId = entry.getValue();
            Set<String> nodeTo = resTo.get(resId);
            if (nodeTo == null) {
                System.out.println(resId);
//                Set<String> parents = new HashSet<>();
//                dependenceMap.put(to, parents);
            } else {
                for (String to : nodeTo) {
                    if (dependenceMap.containsKey(to)) {
                        Set<String> parents = dependenceMap.get(to);
                        parents.add(nodeId);
                    } else {
                        Set<String> parents = new HashSet<>();
                        parents.add(nodeId);
                        dependenceMap.put(to, parents);
                    }
                }
            }
        }
        
//        for (Map.Entry<String, Set<String>> entity : dependenceMap.entrySet()) {
//            System.out.println(entity.getKey() + " -> " + StringUtils.join(entity.getValue(), ","));
//        }

       
        for (Map.Entry<String, Set<String>> entry : dependenceMap.entrySet()) {
            String node = entry.getKey();
            Set<String> fromSet = entry.getValue();
            if (fromSet == null) {
                System.out.println(node);
//                Set<String> parents = new HashSet<>();
//                dependenceMap.put(to, parents);
            } else {
                for (String from : fromSet) {
                    if (directGraph.containsKey(from)) {
                        Set<String> tos = directGraph.get(from);
                        tos.add(node);
                    } else {
                        Set<String> tos = new HashSet<>();
                        tos.add(node);
                        directGraph.put(from, tos);
                    }
                }
            }
        }
        
        System.out.println("direct Graph");
        for (Map.Entry<String, Set<String>> entity : directGraph.entrySet()) {
            System.out.println(entity.getKey() + " -> " + StringUtils.join(entity.getValue(), ","));
        }
        
        //generate statements
        for (Map.Entry<String, Set<String>> entry : directGraph.entrySet()) {
            List<String> parallelStrings = new ArrayList<>();
            parallelStrings.add(entry.getKey());
            statements.add(new BioFlowParallelStatement(new ArrayList(entry.getValue()), parallelStrings));
        }
        
        return statements;
    }
    
     
     
     
     
}
