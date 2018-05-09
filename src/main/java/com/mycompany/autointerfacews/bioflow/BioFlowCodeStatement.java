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
public class BioFlowCodeStatement {
        String location;
        String codeName;
        String codeType;
        String code;
        
        @Override
        public String toString() {
                return "call " + codeType + "(" + location + codeName + ")";
        }

        public BioFlowCodeStatement(String location, String codeName, String codeType, String code) {
                this.location = location;
                this.codeName = codeName;
                this.codeType = codeType;
                this.code = code;
        }

        public String getCode() {
                return code;
        }

        public String getLocation() {
                return location;
        }

        public String getCodeName() {
                return codeName;
        }

        public String getCodeType() {
                return codeType;
        }
        
        
        
}
