/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

/**
 *
 * @author jupiter
 */
public class MyStatus {

    String id;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public MyStatus() {
        this.message = "";
        this.id = "";
    }

    public synchronized void addMessage(String message) {
        if (message.endsWith("\n")) {
            this.message += message;
        } else {
            this.message += message + System.getProperty("line.separator");
        }
    }

}
