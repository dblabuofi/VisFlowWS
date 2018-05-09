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
public class BioFlowTransformStatement {
        List<String> targetAttrs;
        List<String> targetResources;
        List<String> targetTypes;
        
        List<String> conResources;
        List<String> conTypes;
        
        String location;
        String targetFileName;
        String fromFileName;

        public BioFlowTransformStatement(List<String> targetAttrs, List<String> targetResources, List<String> targetTypes, List<String> conResources, List<String> conTypes, String location, String targetFileName, String fromFileName) {
                this.targetAttrs = targetAttrs;
                this.targetResources = targetResources;
                this.targetTypes = targetTypes;
                this.conResources = conResources;
                this.conTypes = conTypes;
                this.location = location;
                this.targetFileName = targetFileName;
                this.fromFileName = fromFileName;
        }
        
        @Override
        public String toString() {
                return "tramsform resource " + StringUtils.join(targetResources, ",") + "\n" 
                        + "attributes " + StringUtils.join(targetAttrs, ",") + "\n"
                        + "types " + StringUtils.join(targetTypes, ",") + "\n"
                        + "transform resources " + StringUtils.join(conResources, ",") + " to " + StringUtils.join(conTypes, ","); 
        }

        public String getFromFileName() {
                return fromFileName;
        }

        public List<String> getTargetAttrs() {
                return targetAttrs;
        }

        public List<String> getTargetResources() {
                return targetResources;
        }

        public List<String> getTargetTypes() {
                return targetTypes;
        }

        public List<String> getConResources() {
                return conResources;
        }

        public List<String> getConTypes() {
                return conTypes;
        }

        public String getLocation() {
                return location;
        }

        public String getTargetFileName() {
                return targetFileName;
        }
        
        
}
