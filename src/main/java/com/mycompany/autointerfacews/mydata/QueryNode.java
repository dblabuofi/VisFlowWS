/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

/**
 *
 * @author mou1609
 */
public class QueryNode {
    String value;
    String xpath;

    public QueryNode(String value, String xpath) {
        this.value = value;
        this.xpath = xpath;
    }

    public String getValue() {
        return value;
    }

    public String getXpath() {
        return xpath;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof QueryNode)) {
            return false;
        }
        QueryNode b = (QueryNode) o;
        return xpath.equals(b.xpath);
    }
    @Override
    public int hashCode() {
        return xpath.hashCode();
    }
    public String toString() {
        return value + " " + xpath;
    }
}
