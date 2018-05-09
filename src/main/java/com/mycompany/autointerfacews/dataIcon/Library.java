/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import com.mycompany.autointerfacews.mydata.QueryTree;
import com.mycompany.visflowsmatch.data.mappings.IContextMapping;
import com.mycompany.visflowsmatch.data.trees.INode;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class Library implements ISchema {

        String id;
        String libraryName;
        String libraryType;
        String organization;
        String location;
        String alias;
        String description;
        String schema;
        List<Function> function;
        
        
        
        
        //for jstree
        List<Function> children;
        String text;
        String type;
        
          
        @Override
         public QueryTree getSchema(QueryTree originalTree,  IContextMapping<INode> result){
                 return null;
         }
        
          public boolean equals(Object b) {
                if (!(b instanceof Library)) {
                        return false;
                } 
                Library obj = (Library) b;
                return id.equals(obj.id);
        }
          
        public int hashCode() {
                return id.hashCode();
        }  

        public String getLibraryName() {
                return libraryName;
        }

        
        
        public List<Function> getChildren() {
                return children;
        }

        public void setChildren(List<Function> children) {
                this.children = children;
        }

        public void setText(String text) {
                this.text = text;
        }

        public void setFunction(List<Function> function) {
                this.function = function;
        }

        public String getId() {
                return id;
        }

        public List<Function> getFunction() {
                return function;
        }
        
        
        
        
        
        
}
