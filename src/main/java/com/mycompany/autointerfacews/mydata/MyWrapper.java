/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class MyWrapper {

    String wrapperName;
    String separator;
    List<String> headers;
    String resultContainHeaderInfo;
    List<String> attrs;
    int tableIndex;

    public List<String> getAttrs() {
        return attrs;
    }

    public int getTableIndex() {
        return tableIndex;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public String getSeparator() {
        return separator;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getResultContainHeaderInfo() {
        return resultContainHeaderInfo;
    }

}
