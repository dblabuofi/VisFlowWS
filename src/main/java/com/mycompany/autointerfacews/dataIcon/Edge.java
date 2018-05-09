/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class Edge {

    String from;
    String to;
    String id;
    String arrow;
    List<MyResource> resources;
    List<Function> libraries;
    String label;
    
    public List<MyResource> getResource() {
        return resources;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getId() {
        return id;
    }

    public String getArrow() {
        return arrow;
    }

    @Override
    public String toString() {
        return from + " TO " + to + " ; ";
    }
}
