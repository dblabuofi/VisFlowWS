/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.gordian;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jupiter
 */
public class GordianNode {
        List<GordianCell> cells;
        Boolean shared;

        public void setShared(Boolean shared) {
                this.shared = shared;
        }
        
        @Override
        public String toString() {
                String res = "shared " + shared + " isLeaf " + isLeaf() + " ";
                for (GordianCell cell : cells) {
//                        res += cell.getValue() + " " + cell.getCount() + " " + cell.getChild().getSize() + ", ";
                        res += cell.getValue() + " " + cell.getCount() + " " + ", ";
                };
                return res;
        }
        
        public GordianNode() {//Default is not shared
                this.cells = new ArrayList<>();
                this.shared = Boolean.FALSE;
        }

        public GordianNode(Boolean shared) {
                this.cells = new ArrayList<>();
                this.shared = shared;
        }
        
        public Boolean isOneEntity() {
                if ( cells.size() != 1 ) {
                        return false;
                }
                GordianNode child = cells.get(0).getChild();
                Boolean res = true;
                while (child.getSize() > 0) {
                        if (child.getSize() > 1) {
                                res = false;
                                break;
                        }
                        child = child.getCells().get(0).getChild();
                }
                
                return res;
        }

        public Boolean getShared() {
                return shared;
        }
        
        public Integer getSize() {
                return cells.size();
        }
        public List<GordianNode> getChildren() {
                return cells.stream().filter(  t-> !t.getChild().isEmpty() ).map(  t -> t.getChild() ).collect(Collectors.toList());
        }
        
        public List<GordianCell> getCells() {
                return cells;
        }

        public void setCells(List<GordianCell> cells) {
                this.cells = cells;
        }
  
        public  Boolean isContain(String value) {
                return cells.stream().anyMatch(t -> t.getValue().equals(value));
        }
        
        public GordianCell contains(String value) {
                return cells.stream().filter( t -> t.getValue().equals(value)).findFirst().get();
        }
        
        public void addCell(GordianCell cell) {
                cells.add(cell);
        }
        
        public Boolean isLeaf() {
                Boolean res = true;
                for (GordianCell cell : cells) {
                        res &= cell.getIsLeaf() && cell.getChild().isEmpty(); 
                };
                return cells.get(0).getIsLeaf();
        }
        public Boolean isEmpty() {
                return cells.isEmpty();
        }
}
