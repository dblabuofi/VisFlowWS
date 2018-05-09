/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.smtch;


import com.google.inject.Inject;
import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.visflowsmatch.IMatchManager;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.mappings.IMappingElement;
import com.mycompany.visflowsmatch.data.trees.IContext;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jupiter
 */
public class SMatch {
        @Inject
        IMatchManager manager;

        public Map<String, String> matchLists(List<String> arrMatch, List<String> arrMatched) {
                Map<String, String> res = new HashMap<>();
                try {
                        IContext s = manager.createContext();
                        INode sroot = s.createRoot("");
                        for (String word : arrMatch) {
                                sroot.createChild(word);
                        }
                        IContext t = manager.createContext();
                        INode troot = t.createRoot("");
                        for (String word : arrMatched) {
                                troot.createChild(word);
                        }
                        IContextMapping<INode> result = manager.match(s, t);
                        System.out.println("Printing matches:");
                        for (IMappingElement<INode> e : result) {
                                if (!e.getSource().nodeData().getName().isEmpty() && !e.getTarget().nodeData().getName().isEmpty()) {
                                        res.put(e.getSource().nodeData().getName(), e.getTarget().nodeData().getName());
                                }
                        }
                        
                } catch (Exception e) {
                        e.printStackTrace();
                }

                return res;
        }
        
         public String getMatchAttribute(QueryTree queryTree, MyAttribute attribute) {
                String res = "";
                try {
                        IContext s = manager.createContext();
                        
                        queryTree.generateIContext(s);
                        System.out.println("getMatchAttribute");
                        queryTree.print(queryTree);
                        System.out.println("getMatchAttribute");
                        
                        IContext t = manager.createContext();
//                        INode root = t.createRoot(queryTree.getText());
//                        root.createChild(attribute.getLabel());
                        INode root = t.createRoot(attribute.getLabel());
                       
                        
                        System.out.println(attribute.getLabel());
                        
                        IContextMapping<INode> result = manager.match(s, t);
                        System.out.println("Printing matches:" + result.size());
                        //step one check if we have the same words
                        for (IMappingElement<INode> e : result) {
                                if (!e.getSource().nodeData().getName().isEmpty() && !e.getTarget().nodeData().getName().isEmpty()) {
                                        if (!e.getSource().nodeData().getName().equals(queryTree.getText())) {
                                                if (e.getSource().nodeData().getName().equals(e.getTarget().nodeData().getName())) {
                                                        res = e.getSource().nodeData().getName();
                                                        return res;
                                                }
                                        }
                                }
                        }
                        //step two check if we have not the root node
                        for (IMappingElement<INode> e : result) {
                                if (!e.getSource().nodeData().getName().isEmpty() && !e.getTarget().nodeData().getName().isEmpty()) {
                                        System.out.println(e.getSource().nodeData().getName() + " " + e.getRelation() + " " + e.getTarget().nodeData().getName());
                                        if (!e.getSource().nodeData().getName().equals(queryTree.getText())) {
                                                res = e.getSource().nodeData().getName();
                                                break;
                                        }
                                }
                        }
                        
                } catch (Exception e) {
                        e.printStackTrace();
                }

                return res;
         }
        
        
}
