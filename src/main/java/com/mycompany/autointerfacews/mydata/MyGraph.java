/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mycompany.autointerfacews.bioflow.BioFlowParallelStatement;
import com.mycompany.autointerfacews.dataIcon.Edge;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jupiter
 */
public class MyGraph {

    //current node, to    
    Map<String, List<String>> graph;
    Map<String, List<String>> graph1;

    public MyGraph() {
        graph = new HashMap<>();
        graph1 = new HashMap<>();
    }

    public void setGraph(List<Edge> edges) {
        System.out.println("setgraph");
        graph.clear();
        for (Edge edge : edges) {
            if (!graph.containsKey(edge.getFrom())) {
                graph.put(edge.getFrom(), new ArrayList<>());
            }
            if (!graph.containsKey(edge.getTo())) {
                graph.put(edge.getTo(), new ArrayList<>());
            }
            List<String> tos = graph.get(edge.getFrom());
            if (!tos.contains(edge.getTo())) {
                tos.add(edge.getTo());
                graph.put(edge.getFrom(), tos);
            }
        }
    }

    public void printGraph() {
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            System.out.println(entry.getKey() + ": " + StringUtils.join(entry.getValue().toArray(), " , "));
        }
    }

    public List<String> topsort() {
        Map<String, Integer> indegrees = new HashMap<>();
        Set<String> res = new LinkedHashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        for (String u : graph.keySet()) {
            indegrees.put(u, 0);
        }
        graph.forEach((u, v) -> {
            v.stream().forEach(e -> {
                indegrees.put(e, indegrees.get(e) + 1);
            });
        });

        indegrees.forEach((u, v) -> {
            if (v == 0) {
                queue.add(u);
                res.add(u);
            }
        });

//        indegrees.forEach((u, v) -> System.out.println(u + " num " + v));
        while (!queue.isEmpty()) {
            String cur = queue.remove();

            for (String v : graph.get(cur)) {
                indegrees.put(v, indegrees.get(v) - 1);
                if (indegrees.get(v) == 0) {
                    queue.add(v);
                    res.add(v);
                }
            }
        }

        return new ArrayList<>(res);
    }

    public Set<String> getNodeParent(String NodeID) {
        Set<String> res = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            if (entry.getValue().contains(NodeID)) {
                res.add(entry.getKey());
            }
        }

        return res;
    }

    public Set<String> getNodeTo(String NodeID) {
        Set<String> res = new HashSet<>(graph.get(NodeID));

        return res;
    }

    public Map<String, Set<String>> getNodeParents() {
        Map<String, Set<String>> res = new ConcurrentHashMap<>();
        BiMap<String, Integer> nodeIndexMap = HashBiMap.create();
        int index = 0;
        for (String s : graph.keySet()) {
            nodeIndexMap.put(s, index++);
        }

        for (List<String> to : graph.values()) {
            for (String t : to) {
                if (!nodeIndexMap.containsKey(t)) {
                    nodeIndexMap.put(t, index++);
                }
            }
        }

        int[][] map = new int[nodeIndexMap.size()][];

        for (int i = 0; i < nodeIndexMap.size(); ++i) {
            map[i] = new int[nodeIndexMap.size()];
        }

        for (String s : graph.keySet()) {
            int v = nodeIndexMap.get(s);
            for (String t : graph.get(s)) {
                int u = nodeIndexMap.get(t);
                map[v][u] = 1;
            }
        }

        //get where it can go
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[i].length; ++j) {
                if (map[i][j] == 1) {
                    for (int k = 0; k < map[i].length; ++k) {
                        if (map[j][k] == 1) {
                            map[i][k] = 1;
                        }
                    }
                }
            }
        }

        for (String s : graph.keySet()) {
            int source = nodeIndexMap.get(s);

            Set<String> parent = new HashSet<>();
            for (int i = 0; i < map.length; ++i) {
                if (map[i][source] == 1) {
                    parent.add(nodeIndexMap.inverse().get(i));
                }
            }
            res.put(s, parent);
        }

        return res;
    }

    public Map<String, Set<String>> getNodeParents(List<BioFlowParallelStatement> statements) {
        graph1.clear();
        Map<String, List<String>> graph = graph1;
        for (BioFlowParallelStatement statement : statements) {
            String nodeID = statement.getAfters().get(0);
            List<String> tos = statement.getParallels();
            if (!graph.containsKey(nodeID)) {
                graph.put(nodeID, tos);
            } else {
                List<String> to = graph.get(nodeID);
                to.addAll(tos);
            }
        }
        System.out.println("graph1");
        for (Map.Entry<String, List<String>> entity : graph.entrySet()) {
            System.out.println(entity.getKey() + " -> " + org.apache.commons.lang3.StringUtils.join(entity.getValue(), ","));
        }
        Map<String, Set<String>> res = new ConcurrentHashMap<>();
        BiMap<String, Integer> nodeIndexMap = HashBiMap.create();
        int index = 0;
        for (String s : graph.keySet()) {
            nodeIndexMap.put(s, index++);
        }
        for (List<String> to : graph.values()) {
            for (String t : to) {
                if (!nodeIndexMap.containsKey(t)) {
                    nodeIndexMap.put(t, index++);
                }
            }
        }
        int[][] map = new int[nodeIndexMap.size()][];
        for (int i = 0; i < nodeIndexMap.size(); ++i) {
            map[i] = new int[nodeIndexMap.size()];
        }
        for (String s : graph.keySet()) {
            int v = nodeIndexMap.get(s);
            for (String t : graph.get(s)) {
                int u = nodeIndexMap.get(t);
                map[v][u] = 1;
            }
        }
        //get where it can go
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[i].length; ++j) {
                if (map[i][j] == 1) {
                    for (int k = 0; k < map[i].length; ++k) {
                        if (map[j][k] == 1) {
                            map[i][k] = 1;
                        }
                    }
                }
            }
        }
        for (String s : nodeIndexMap.keySet()) {
            int source = nodeIndexMap.get(s);

            Set<String> parent = new HashSet<>();
            for (int i = 0; i < map.length; ++i) {
                if (map[i][source] == 1) {
                    parent.add(nodeIndexMap.inverse().get(i));
                }
            }
            res.put(s, parent);
        }
        return res;
    }

    public Map<String, Set<String>> getNodeParents2(List<Edge> edges) {
        Map<String, Set<String>> res = new ConcurrentHashMap<>();
        for (Edge e : edges) {
            res.put(e.getFrom(), new HashSet<>());
            res.put(e.getTo(), new HashSet<>());
        }

        for (Edge e : edges) {
            Set<String> val = res.get(e.getTo());
            val.add(e.getFrom());
            res.put(e.getTo(), val);
        }
        return res;
    }

    public Map<String, Map<String, Set<String>>> getmappedRepeated(List<String[]> repeatPair, List<Edge> edges, Map<String, Set<String>> nodeParents) {
        Map<String, Map<String, Set<String>>> res = new ConcurrentHashMap<>();
        Map<String, Set<String>> graph = new HashMap<>();
        //generat graph
        for (Edge e : edges) {
            graph.putIfAbsent(e.getFrom(), new HashSet<>());
            graph.putIfAbsent(e.getTo(), new HashSet<>());
            graph.get(e.getFrom()).add(e.getTo());
        }
        //
        for (String[] pair : repeatPair) {
            res.put(pair[0], generateSubGraphForRepeat(pair[1], pair[0], graph, nodeParents));
        }
        return res;
    }

    public Map<String, Set<String>> generateSubGraphForRepeat(String start, String end, Map<String, Set<String>> graph, Map<String, Set<String>> nodeParents) {
        //node parent
        Map<String, Set<String>> res = new ConcurrentHashMap<>();
        Set<String> nodes = new HashSet<>();

        dfs(start, end, graph, new HashSet<>(), nodes);
        nodes.add(start);
        nodes.add(end);
        System.out.println("matched nodesss");
        System.out.println(nodes);
        //after we have all the nodes, remove the result
        for (Map.Entry<String, Set<String>> entry : nodeParents.entrySet()) {
            if (nodes.contains(entry.getKey())) {
                Set<String> edges = new HashSet<>(nodes);
                edges.retainAll(entry.getValue());
                res.put(entry.getKey(), edges);
            }
        }
        
        return res;
    }

    static void dfs(String u, String end, Map<String, Set<String>> graph, Set<String> path, Set<String> res) {
        if (u.equals(end)) {
            res.addAll(new HashSet<>(path));
            return;
        }
        for (String v : graph.get(u)) {
            if (!path.contains(v)) {
                path.add(v);
                dfs(v, end, graph, path, res);
                path.remove(v);
            }
        }
    }

    public static <K1, K2> Map<K1, Set<K2>> deepCopy(Map<K1, Set<K2>> original) {
        Map<K1, Set<K2>> copy = new ConcurrentHashMap<K1, Set<K2>>();
        for (Entry<K1, Set<K2>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashSet<K2>(entry.getValue()));
        }
        return copy;
    }

}
