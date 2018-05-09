/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.bioflow;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class BioFlowIFStatement {
        List <String> attributes;
        List<String> conditions;
        List<String> groupOP;
        List<String> resources;
        List<String> logic;
        List<String> values;
        
        String trueBranchID;
        String falseBranchID;
        String trueBranchLabel;
        String falseBranchLabel;
        
         @Override
        public String toString() {
                String con = "";
                
                for (int i = 0; i < attributes.size(); ++i) {
                        if (logic.get(i).isEmpty()) {
                                con += resources.get(i) + " " + attributes.get(i) + " " + conditions.get(i) + " " + groupOP.get(i) + " " + values.get(i) + "\n";
                        } else {
                                con += logic.get(i) + "\n";
                        }
                }
                
                return "if " + con + "\n"
                        + "then go to " + trueBranchLabel + " " + trueBranchID + "\n"
                        + "else go to" + falseBranchLabel + " " + falseBranchID;
        }

        public BioFlowIFStatement(List<String> attributes, List<String> conditions, List<String> groupOP, List<String> resources, List<String> logic, List<String> values, String trueBranchID, String falseBranchID, String trueBranchLabel, String falseBranchLabel) {
                this.attributes = attributes;
                this.conditions = conditions;
                this.groupOP = groupOP;
                this.resources = resources;
                this.logic = logic;
                this.values = values;
                this.trueBranchID = trueBranchID;
                this.falseBranchID = falseBranchID;
                this.trueBranchLabel = trueBranchLabel;
                this.falseBranchLabel = falseBranchLabel;
        }

       

        public List<String> getAttributes() {
                return attributes;
        }

        public List<String> getConditions() {
                return conditions;
        }

        public List<String> getGroupOP() {
                return groupOP;
        }

        public List<String> getResources() {
                return resources;
        }

        public List<String> getLogic() {
                return logic;
        }

        public List<String> getValues() {
                return values;
        }

        public String getTrueBranchID() {
                return trueBranchID;
        }

        public String getFalseBranchID() {
                return falseBranchID;
        }

        public String getTrueBranchLabel() {
                return trueBranchLabel;
        }

        public String getFalseBranchLabel() {
                return falseBranchLabel;
        }
        
        
}
