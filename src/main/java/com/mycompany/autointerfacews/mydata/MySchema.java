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
public class MySchema {
        String text;
        String type;
        List<MySchema> children;

        public String getText() {
                return text;
        }

        public List<MySchema> getChildren() {
                return children;
        }

        public String getType() {
                return type;
        }
        
}
