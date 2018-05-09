/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

/**
 *
 * @author mou1609
 */
public class SelectAttr {
    String attribute;
    String action;
    String input;
    String name;
    String attrIndex;
    String totalNodes;
    String xpath;
    String resourceName;
    
    public SelectAttr(String attribute, String action, String input, String name, String attrIndex, String totalNodes, String xpath, String resourceName) {
        this.attribute = attribute;
        this.action = action;
        this.input = input;
        this.name = name;
        this.attrIndex = attrIndex;
        this.totalNodes = totalNodes;
        this.xpath = xpath;
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getXpath() {
        return xpath;
    }

    public String getTotalNodes() {
        return totalNodes;
    }

    public String getAttrIndex() {
        return attrIndex;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getAction() {
        return action;
    }

    public String getInput() {
        return input;
    }

    public String getName() {
        return name;
    }
    
    
    
}
