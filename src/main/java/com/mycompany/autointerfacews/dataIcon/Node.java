/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.dataIcon;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author jupiter
 */
public class Node {

        String id;
        double x;
        double y;
        String label;
        String timestamp;
        String type;
        String image;
        String shape;
        String stop;
        List<Node> from;
        List<Node> to;
        List<MyResource> resourcesIn;
        List<MyResource> resources;
        List<MyResource> resourcesOut;
        List<Action> actions;
        List<Function> librariesIn;

        //working part
        //use eXist store the data
        String collection;
        //file locations
        List<String> inputs;
        List<String> outputs;
        
        
        @Override
        public String toString() {
                return id + " " + type + " ; ";
        }

        public int hashCode() {
                UUID uid = UUID.fromString(id);
                return uid.hashCode();
        }

        public boolean compareTo(Node o) {
                return this.id.equals(o.id);
        }
        
        //setter

        public void setInputs(List<String> inputs) {
                this.inputs = inputs;
        }

        public void setOutputs(List<String> outputs) {
                this.outputs = outputs;
        }
        
        //getters
        
        public List<String> getOutputs() {
                return outputs;
        }

        public List<String> getInputs() {
                return inputs;
        }

        public String getStop() {
                return stop;
        }

        public List<MyResource> getResourcesIn() {
                return resourcesIn;
        }

        public List<MyResource> getResourcesOut() {
                return resourcesOut;
        }

        public List<Function> getLibrariesIn() {
                return librariesIn;
        }

        public String getCollection() {
                return collection;
        }

        public List<Action> getActions() {
                return actions;
        }

        public String getId() {
                return id;
        }

        public double getX() {
                return x;
        }

        public double getY() {
                return y;
        }

        public String getLabel() {
                return label;
        }

        public String getTimestamp() {
                return timestamp;
        }

        public String getType() {
                return type;
        }

        public String getImage() {
                return image;
        }

        public String getShape() {
                return shape;
        }

        public List<Node> getFrom() {
                return from;
        }

        public List<Node> getTo() {
                return to;
        }

        public List<MyResource> getResources() {
                return resources;
        }

}
