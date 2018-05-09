/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.mycompany.autointerfacews.dataIcon.Edge;
import com.mycompany.autointerfacews.dataIcon.Node;
import com.mycompany.autointerfacews.dataIcon.RunData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author jupiter
 */
public class MyGraphHelper {

    public static RunData filterNodesAndEdges(RunData data) {
        List<Node> nodes = data.getNodes();
        List<Edge> edges = data.getEdges();

        Map<String, Node> idToNode = new HashMap<>();
        Set<Node> allNodes = new HashSet<>();

        nodes.stream().forEach(t -> idToNode.put(t.getId(), t));
        //remove circle here
        List<String[]> repeatPair = new ArrayList<>();

        Queue<Node> queue = new LinkedList<>();
        for (Node node : nodes) {
            if (node.getStop() != null && (node.getStop().equals("input") || node.getStop().equals("output"))) {
                allNodes.add(node);
                queue.add(node);
                break;
            }
        }
        for (Node node : nodes) {
            if (node.getType().equals("repeat")) {
                repeatPair.add(new String[]{node.getId(), node.getActions().get(0).getRepeatNode().getId()});
            }
        }

        for (Iterator<Edge> it = edges.listIterator(); it.hasNext();) {
            Edge cur = it.next();
            for (String[] pair : repeatPair) { 
                if (cur.getFrom().equals(pair[0]) && cur.getTo().equals(pair[1])) {
                    it.remove();
                    break;
                }
            }
        }
        //get all possible nodes
        while (!queue.isEmpty()) {
            Node cur = queue.remove();
            for (Edge edge : edges) {
                if (edge.getTo().equals(cur.getId())) {
                    allNodes.add(idToNode.get(edge.getFrom()));
                    queue.add(idToNode.get(edge.getFrom())); 
                }
            }
        }

        //remove edges
        for (Iterator<Edge> it = edges.listIterator(); it.hasNext();) {
            Edge cur = it.next();
            if (!allNodes.contains(idToNode.get(cur.getFrom())) || !allNodes.contains(idToNode.get(cur.getTo()))) {
                it.remove();
            }
        }
        data.setNodes(new ArrayList<>(allNodes));
        data.setEdges(edges);
        return data;
    }
}
