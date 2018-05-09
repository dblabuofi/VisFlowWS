/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.gordian;

import com.mycompany.autointerfacews.exception.CodeException;
import com.mycompany.autointerfacews.helper.MyFileReader;
import com.mycompany.autointerfacews.utils.MyUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class GordianAlgorithm {
        public static List<Set<String>> getKeys(String fileURL) throws CodeException {
                List<String> headers = MyFileReader.readCSVHead(fileURL);
                List<List<String>> content = MyFileReader.readCSVContent(fileURL);
                
                GordianNode root = generatePrefixTree(content, headers);
//                System.out.println("************");
                //the prefix tree
//                print(root);

                GordianCurNonKey curNonKey = new GordianCurNonKey(headers);
                GordianNonKeySet nonKeySet = new GordianNonKeySet();
                nonKeyFinder(root, 0, curNonKey, nonKeySet);
//                System.out.println("******generatePrefixTree******");
//                List<Set<String>> finalKeys1 = nonKeySet.getNonKeySet();
//                finalKeys1.forEach(  t -> System.out.println( StringUtils.join(t, ", ")));
                
                List<Set<String>> finalKeys = nonKeySet.obtainKeys(new HashSet<>(headers));
//                System.out.println("*******generatePrefixTree*****");
//                finalKeys.forEach(  t -> System.out.println( StringUtils.join(t, ", ")));
                //we return permutation all are keys
                if (finalKeys.isEmpty()) {
                        finalKeys = new  ArrayList<> (MyUtils.powerSet(new HashSet<>(headers)));
//                        System.out.println("*******generatePrefixTree*****");
//                        finalKeys.forEach(  t -> System.out.println( StringUtils.join(t, ", ")));
//                        System.out.println("*******generatePrefixTree done*****");
                }
                
                //merge text
//                GordianNode node3 = root.getCells().get(0).getChild().getCells().get(0).getChild();
//                GordianNode node6 = root.getCells().get(0).getChild().getCells().get(1).getChild();
//                GordianNode node9 = root.getCells().get(1).getChild().getCells().get(0).getChild();
//                
//                List<GordianNode> nodes = new ArrayList<>();
//                nodes.add(node3);
//                nodes.add(node6);
//                nodes.add(node9);
//                setNodesSharedValue(nodes, true);
//                GordianNode newNode = merge(nodes);
//                System.out.println("*********3, 6, 9*************");
//                print(newNode);
//                nodes.clear();
//                nodes.add(newNode.getCells().get(0).getChild());
//                nodes.add(newNode.getCells().get(1).getChild());
//                nodes.add(newNode.getCells().get(2).getChild());
//                newNode = merge(nodes);
//                System.out.println("**********4, 5, 7************");
//                print(newNode);
//                List<GordianNode> nodes1 = new ArrayList<>();
//                nodes1.add(root.getCells().get(0).getChild().getCells().get(0).getChild());
//                nodes1.add(root.getCells().get(0).getChild().getCells().get(1).getChild());
//                System.out.println("***********3, 6***********");
//                newNode = merge(nodes1);
//                print(newNode);
//                nodes1.clear();
//                nodes1.add(root.getCells().get(0).getChild());
//                nodes1.add(root.getCells().get(1).getChild());
//                System.out.println("***********2, 8***********");
//                newNode = merge(nodes1);
//                print(newNode);
                //nonkeySet Text
//                Set<String> row1 = new HashSet<>();
//                row1.add("FirstName");
//                row1.add("LastName");
//                List<Set<String>> input = new ArrayList<>();
//                input.add(row1);
//                GordianNonKeySet g = new GordianNonKeySet(input);
//                Set<String> keys = new HashSet<>();
//                keys.add("Phone");
//                keys.add("LastName");
//                System.out.println(g.isFutile(keys));
//                g.insert(keys);
//                System.out.println("************");
//                System.out.println(g);
//
//                List<Set<String>> finalKeys = g.obtainKeys(new HashSet<>(headers));
//                
//                System.out.println("************");
//                finalKeys.forEach(  t -> System.out.println( StringUtils.join(t, ", ")));



                return finalKeys;
        }
        
        static void setNodesSharedValue(List<GordianNode> nodes, Boolean value) {
                for (GordianNode node : nodes) {
                        node.setShared(value);
                        Queue<GordianNode> queue = new LinkedList<>();
                        queue.addAll(node.getChildren());
                        while (!queue.isEmpty()) {
                                GordianNode cur = queue.remove();
                                cur.setShared(value);
                                queue.addAll(cur.getChildren());
                        }
                }
        }
        
        
        static void nonKeyFinder (GordianNode root, Integer attrNum, GordianCurNonKey curNonKey,  GordianNonKeySet nonKeySet) {
                curNonKey.addAttrNo(attrNum);
//                System.out.println("**********nonKeyFinder************** " + attrNum);
//                print(root);
//                System.out.println("^^^^^^^^^");
//                System.out.println(nonKeySet);
//                System.out.println("^^^^^^^^^");
//                System.out.println(curNonKey);
                
                if (root.isLeaf()) {
//                        System.out.println("root is leaf");
                        for (GordianCell cell : root.getCells()) {
                                if (cell.getCount() > 1) {
//                                        System.out.println("add curNonKey to nonKeySet " + curNonKey);
                                        nonKeySet.insert(curNonKey.getCurNonKey());
                                        break;
                                }
                        }
                        curNonKey.removeAttrNo(attrNum);
//                        System.out.println("^^^^remove attrNum^^^^^" + attrNum);
//                        System.out.println(curNonKey);
                        if (root.getSize() > 1 || (root.getSize() == 1 && root.getCells().get(0).getCount() > 1)) {
//                                System.out.println("add curNK to nonKeySet " + curNonKey);
                                nonKeySet.insert(curNonKey.getCurNonKey());
                        }
                } else {
//                        System.out.println("root is not leaf");
                        if (root.isOneEntity()) {
//                                System.out.println("root has one entity");
                                //*********************
                                curNonKey.removeAttrNo(attrNum);
                                return;
                        }
                        for (GordianCell cell : root.getCells()) {
                                if (cell.getChild().getShared() == false) {
                                        if (!cell.getChild().isEmpty()) {
                                                nonKeyFinder(cell.getChild(), attrNum + 1, curNonKey, nonKeySet);
//                                                System.out.println("Returned from nKF " + curNonKey);
                                        }
                                }
                        }
                        curNonKey.removeAttrNo(attrNum);
//                        System.out.println("^^^^remove attrNum^^^^^" + attrNum);
//                        System.out.println(curNonKey);
                        if (root.getSize() > 1) {
//                                System.out.println("root has more than one cell");
                                if (nonKeySet.isFutile(curNonKey.getCurNonKey()) == true) {
//                                        System.out.println("curNonKey is futile " + curNonKey);
                                        return;
                                }
                                List<GordianNode> children = root.getChildren();
                                setNodesSharedValue(children, true);
                                GordianNode mergeTree = merge(children);
                                nonKeyFinder(mergeTree, attrNum + 1, curNonKey, nonKeySet);
//                                System.out.println("Returned from nonKeyFinder " + curNonKey);
                                setNodesSharedValue(children, false);
                        }
                }
        }
        
        
        static GordianNode merge(List<GordianNode> nodes) {
                if (nodes.size() == 1) {
                        return nodes.get(0);
                }
                GordianNode res = new GordianNode();
                Map<String, GordianCell> mergeList = new HashMap<>();
                for (GordianNode node : nodes) {
                        for (GordianCell cell : node.getCells()) {
                                GordianCell newCell = null;
                                if (mergeList.containsKey(cell.getValue())) {
                                        newCell = mergeList.get(cell.getValue());
                                } else {
                                        newCell = new GordianCell();
                                        newCell.setValue(cell.getValue());
                                        res.addCell(newCell);
                                        mergeList.put(cell.getValue(), newCell);
                                }
                                if (cell.getIsLeaf() == true) {
                                         newCell.setIsLeaf(Boolean.TRUE);
                                         newCell.addCount(cell.getCount());
                                } else {
                                        List<GordianNode> next = new ArrayList<>();
                                        nodes.forEach((t) -> {
                                                t.getCells().stream().filter((c) -> (c.getValue().equals(cell.getValue()))).forEachOrdered((g) -> {
                                                        next.add(g.getChild());
                                                });
                                        });
                                        newCell.setChild(merge(next));
                                }
                        }
                }
                return res;
        }
        
        static GordianNode generatePrefixTree(List<List<String>> content, List<String> header) throws CodeException {
                GordianNode root = new GordianNode();
                GordianNode node = null;
                GordianCell cell = null;
                for (List<String> row : content) {
                        node = root;
//                        System.out.println("-----------------row----------------");
                        for (int i = 0; i < row.size(); ++i) {
                                String value = row.get(i);
//                                System.out.println(value);
                                if (node.isContain(value)) {
                                        cell = node.contains(value);
                                } else {
                                        cell = new GordianCell(0, value, header.get(i), new GordianNode(), Boolean.FALSE);
                                        node.addCell(cell);
                                }
                                
                                if (i == row.size() - 1) {
                                        cell.setIsLeaf(Boolean.TRUE);
                                        cell.addOneToCount();
                                        if (cell.getCount() > 1) {
                                                throw new CodeException("Generate Prefix Tree wrong, no keys");
                                        }
                                } else {
                                        node = cell.getChild();
                                }
                        }
                }
                
                return root;
        }
        
        static void print(GordianNode root) {
                Queue<GordianNode> queue = new LinkedList<>();
                queue.add(root);
                while(!queue.isEmpty()) {
                        GordianNode cur = queue.remove();
                        System.out.println(cur);
                        queue.addAll(cur.getChildren());
                }
        }
        
        
}
