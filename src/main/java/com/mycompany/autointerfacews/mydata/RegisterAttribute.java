/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.mydata;

/**
 *
 * @author jupiter
 */
public class RegisterAttribute {
        String label;
        String name;
        String value;
        String required;
        String shown;
        String example;
        String attributeType;
        String description;
        String type;
        String from;

        public RegisterAttribute(String label, String name, String value, String required, String shown, String example, String attributeType, String description, String type, String from) {
                this.label = label;
                this.name = name;
                this.value = value;
                this.required = required;
                this.shown = shown;
                this.example = example;
                this.attributeType = attributeType;
                this.description = description;
                this.type = type;
                this.from = from;
        }

        public String getLabel() {
                return label;
        }

        public String getName() {
                return name;
        }

        public String getValue() {
                return value;
        }

        public String getRequired() {
                return required;
        }

        public String getShown() {
                return shown;
        }

        public String getExample() {
                return example;
        }

        public String getAttributeType() {
                return attributeType;
        }

        public String getDescription() {
                return description;
        }

        public String getType() {
                return type;
        }

        public String getFrom() {
                return from;
        }
        
}
