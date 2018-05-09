/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import com.mycompany.autointerfacews.mydata.MyAttribute;
import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class Function implements ISchema {

        String libraryName;
        String libraryId;
        String organization;
        String id;
        String functionName;
        String functionType;
        String location;
        String method;
        String urlFormat;
        String urlExample;
        String description;
        String urlReturnFileType;
        List<MyAttribute> attributes;
        String schema;
        String inputSeparator;
        String outputSeparator;
        String comandLine;
        String aggregateName;
        
        //for jstree
        String text;
        String type;
        
           
        @Override
         public QueryTree getSchema(QueryTree originalTree,  IContextMapping<INode> result){
                 return null;
         }
         
        public boolean equals(Object b) {
                if (!(b instanceof Function)) {
                        return false;
                } 
                Function obj = (Function) b;
                return id.equals(obj.id) && libraryId.equals(obj.libraryId);
        }
        public int hashCode() {
                return id.hashCode();
        }  
//        public String toString() {
//                return functionName + " " + libraryName + " " + organization + " " + functionType + " " + StringUtils.join(inputAttributes, " ") 
//                        + " " + StringUtils.join(outputAttributes, " ")  +" "+ description + " " + method;
//         }

        public String getSchema() {
                return schema;
        }

        public String getComandLine() {
                return comandLine;
        }

        public String getText() {
                return text;
        }

        public String getType() {
                return type;
        }

        public void setText(String text) {
                this.text = text;
        }

        public String getUrlReturnFileType() {
                return urlReturnFileType;
        }

        public List<MyAttribute> getAttributes() {
                return attributes;
        }

        
        
        public String getFunctionName() {
                return functionName;
        }

        public String getLibraryName() {
                return libraryName;
        }

        public String getLibraryId() {
                return libraryId;
        }

        public String getOrganization() {
                return organization;
        }

        public String getId() {
                return id;
        }

        public String getFunctionType() {
                return functionType;
        }

        public String getLocation() {
                return location;
        }


        public String getInputSeparator() {
                return inputSeparator;
        }


        public String getOutputSeparator() {
                return outputSeparator;
        }

        public String getUrlFormat() {
                return urlFormat;
        }

        public String getUrlExample() {
                return urlExample;
        }

        public String getMethod() {
                return method;
        }

        public String getDescription() {
                return description;
        }
        
        
        
}
