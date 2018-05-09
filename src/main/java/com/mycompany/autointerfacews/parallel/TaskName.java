/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.parallel;

/**
 *
 * @author mou1609
 */
public class TaskName {

    String name;
    Integer delay;

    public TaskName(String name, Integer delay) {
        this.name = name;
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public Integer getDelay() {
        return delay;
    }
}
