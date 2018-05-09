/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jupiter
 */
public class RunData {

    List<Node> nodes;
    List<Edge> edges;
    List<AttrMatch> globalmatch;

    public List<AttrMatch> getGlobalmatch() {
        return globalmatch;
    }
    
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return StringUtils.join(nodes.toArray(), "  ,  ") + "     " + StringUtils.join(edges.toArray(), "  ,  ");
    }

}
