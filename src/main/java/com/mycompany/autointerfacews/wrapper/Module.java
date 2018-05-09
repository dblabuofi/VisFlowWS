/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.wrapper;

import com.mycompany.autointerfacews.dataIcon.RunData;

/**
 *
 * @author mou1609
 */
public class Module {
    String fileName;
    String startNode;
    String endNode;
    String description;
    RunData graph;

    public Module(String fileName, String startNode, String endNode, String description, RunData graph) {
        this.fileName = fileName;
        this.startNode = startNode;
        this.endNode = endNode;
        this.description = description;
        this.graph = graph;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStartNode() {
        return startNode;
    }

    public String getEndNode() {
        return endNode;
    }

    public String getDescription() {
        return description;
    }

    public RunData getGraph() {
        return graph;
    }
    
    
    
}
