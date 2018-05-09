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
public class BioFLowFunctionStatement {
        String location;
        String functionName;
        List<String> attrs; 
        String commandLineParams;
        String functionType;

        public BioFLowFunctionStatement(String location, String functionName, List<String> attrs, String commandLineParams, String functionType) {
                this.location = location;
                this.functionName = functionName;
                this.attrs = attrs;
                this.commandLineParams = commandLineParams;
                this.functionType = functionType;
        }
        
        @Override
        public String toString() {
                return "call local function " + functionName + "\n"
                        + "arguments " + StringUtils.join(attrs, ", ") + "\n"
                        + "command " + commandLineParams;
        }

        public String getLocation() {
                return location;
        }

        public String getFunctionName() {
                return functionName;
        }

        public List<String> getAttrs() {
                return attrs;
        }

        public String getCommandLineParams() {
                return commandLineParams;
        }

        public String getFunctionType() {
                return functionType;
        }
        
        
        
        
}
