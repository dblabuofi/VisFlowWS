/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author jupiter
 */
public class MyAttribute {

        String label;
        String name;
        String type;//attribute type
        String value;
        String selected;
        Boolean required;
        Boolean shown;
        String example;
        String attributeType;
        List<MySelection> selectionpair;
        List<String> possibleValues;
        String description;
        String from;

        public MyAttribute(String name, String type, String description) {
                this.name = name;
                this.type = type;
                this.description = description;
        }
        
        @Override
        public String toString() {
                return name + " " + example;
        }

        @Override
        public boolean equals(Object o) {
                if (o instanceof MyAttribute) {
                        MyAttribute other = (MyAttribute) o;
                        return this.name.equals(other.name) && this.required == required;
                }
                return false;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 79 * hash + Objects.hashCode(this.name);
                hash = 79 * hash + Objects.hashCode(this.example);
                hash = 79 * hash + Objects.hashCode(this.required);
                hash = 79 * hash + Objects.hashCode(this.description);
                return hash;
        }

        public String getSelected() {
                return selected;
        }

        public String getLabel() {
                return label;
        }

        public String getType() {
                return type;
        }

        public String getAttributeType() {
                return attributeType;
        }

        public List<MySelection> getSelectionpair() {
                return selectionpair;
        }

        public String getFrom() {
                return from;
        }

        public Boolean getShown() {
                return shown;
        }

        public String getValue() {
                return value;
        }

        public String getName() {
                return name;
        }

        public String getExample() {
                return example;
        }

        public Boolean getRequired() {
                return required;
        }

        public String getDescription() {
                return description;
        }

        public List<String> getPossibleValues() {
                return possibleValues;
        }

}
