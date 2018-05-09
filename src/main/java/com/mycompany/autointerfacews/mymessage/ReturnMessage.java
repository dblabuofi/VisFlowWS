/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mymessage;

import com.mycompany.autointerfacews.dataIcon.ColFunction;
import com.mycompany.autointerfacews.dataIcon.TerminalFormation;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class ReturnMessage {
        String lastNode;
        String printType;
        String resourceName;
        List<List<String>> tableContent;
        String id;
        TerminalFormation submit;
        Integer numOfWins;
        String location;
        List<ColFunction> colFuns;
        
        public String toString() {
            String res = lastNode + " " + printType + " " + resourceName;
            res += tableContent.toString();
            return res;
        }
        
        
        public ReturnMessage(String lastNode, String printType, String resourceName, String id) {
                this.lastNode = lastNode;
                this.printType = printType;
                this.resourceName = resourceName;
                this.id = id;
        }

        public ReturnMessage(String lastNode, String printType, String resourceName, List<List<String>> tableContent) {
                this.lastNode = lastNode;
                this.printType = printType;
                this.resourceName = resourceName;
                this.tableContent = tableContent;
        }

        public ReturnMessage(String lastNode, String printType, String resourceName, List<List<String>> tableContent, String id, TerminalFormation submit, Integer numOfWins, String location, List<ColFunction> colFuns) {
                this.lastNode = lastNode;
                this.printType = printType;
                this.resourceName = resourceName;
                this.tableContent = tableContent;
                this.id = id;
                this.submit = submit;
                this.numOfWins = numOfWins;
                this.location = location;
                this.colFuns = colFuns;
                
        }
        
        
        
        
        public void setTableContent(List<List<String>> tableContent) {
                this.tableContent = tableContent;
        }
        
        
}
