/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import com.google.gson.reflect.TypeToken;
import com.mycompany.autointerfacews.helper.MyFileReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class MyTable implements Serializable {
        List<MyAttribute> attrs;
        List<List<String>> data;

        public MyTable() {
        }
        //setter

        public void setAttrs(List<MyAttribute> attrs) {
                this.attrs = attrs;
        }

        public void setData(List<List<String>> data) {
                this.data = data;
        }
        
        //geter
        public List<MyAttribute> getAttrs() {
                return attrs;
        }

        public List<List<String>> getData() {
                return data;
        }

        public void printData() {
                data.stream().forEach(t -> System.out.println(StringUtils.join(t.toArray(), " \t ")));
        }

        public List<List<String>> generateTable() {//add col names
//                data.add(0, colNames);
                return data;
        }

}
