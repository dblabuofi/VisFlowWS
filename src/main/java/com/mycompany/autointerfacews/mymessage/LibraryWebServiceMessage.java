/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mymessage;

import com.mycompany.autointerfacews.dataIcon.Library;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class LibraryWebServiceMessage {
    List<Library> methods;
    String name;
    String id;
    
    public LibraryWebServiceMessage(String id, String name, List<Library> methods) {
        this.methods = methods;
        this.name = name;
        this.id = id;
    }
  
    
}
