/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.gordian;

import java.util.ArrayList;

/**
 *
 * @author jupiter
 */
public class GordianCell {
        int count;
        String value;
        String attr;//header name
        GordianNode child; 
        Boolean isLeaf;
        
        public void setCount(int count) {
                this.count = count;
        }

        public void setValue(String value) {
                this.value = value;
        }

        public void setAttr(String attr) {
                this.attr = attr;
        }

        public void setChild(GordianNode child) {
                this.child = child;
        }

        public int getCount() {
                return count;
        }

        public String getValue() {
                return value;
        }

        public String getAttr() {
                return attr;
        }

        public Boolean getIsLeaf() {
                return isLeaf;
        }

        public void setIsLeaf(Boolean isLeaf) {
                this.isLeaf = isLeaf;
        }
        
        public GordianNode getChild() {
                return child;
        }
        
        public GordianCell() {
                 this.count = 0;
                this.value = "";
                this.attr = "";
                this.child = new GordianNode();
                this.isLeaf = false;
        }

        public GordianCell(int count, String value, String attr, GordianNode child, Boolean isLeaf) {
                this.count = count;
                this.value = value;
                this.attr = attr;
                this.child = child;
                this.isLeaf = isLeaf;
        }

        void addOneToCount() {
                ++count;
        }
        
        
        void addCount(int counts) {
                count += counts;
        }
        
        @Override
        public String toString() {
                String res = "attr: " + attr + " value " + value + " count: " + count + " isLeaf " + isLeaf + " child size: " + child.getCells().size() +"\n";
                res += child;
                return res;
        }
        
}
