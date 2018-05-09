/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

/**
 *
 * @author jupiter
 */
public class MyBranch {

    String label;
    String id;

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        return label + " " + id;
    }
    
}
