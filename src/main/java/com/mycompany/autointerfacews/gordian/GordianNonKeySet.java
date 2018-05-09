/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.gordian;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class GordianNonKeySet {
        List<Set<String>> nonKeySet;

        public GordianNonKeySet() {
                nonKeySet = new ArrayList<>();
        }

        public GordianNonKeySet(List<Set<String>> nonKeySet) {
                this.nonKeySet = nonKeySet;
        }

        public List<Set<String>> getNonKeySet() {
                return nonKeySet;
        }
        

        public Boolean insert(Set<String> nonKey) {
                if (nonKey.isEmpty()) return false;
                Boolean toAdd = true;
                for (Set<String> nk : nonKeySet) {
                        if (nk.containsAll(nonKey)) {
                                toAdd = false;
                                break;
                        }
                }
                if (toAdd == true) {
                        Iterator<Set<String>> it = nonKeySet.iterator();
                        while(it.hasNext()) {
                                Set<String> nk = it.next();
                                if (nonKey.containsAll(nk)) {
                                        it.remove();
                                }
                        }
                        nonKeySet.add(nonKey);
                }
                return true;
        }
        
         public Boolean isFutile(Set<String> nonKey) {
                if (nonKey.isEmpty()) return false;
                Boolean toAdd = false;
//                for (Set<String> nk : nonKeySet) {
//                        if (nk.containsAll(nonKey)) {
//                                toAdd = true;
//                                break;
//                        }
//                }
                if (nonKeySet.containsAll(nonKey) ) {
                        return true;
                }
                return toAdd;
        }
        
        public List<Set<String>> obtainKeys(Set<String> headers) {
                List<Set<String>> keySet = new ArrayList<>();
                for (Set<String> nonKey : nonKeySet) {
                        List<Set<String>> complementSet = new ArrayList<>();
                        Set<String> complement = new HashSet<>(headers);
                        complement.removeAll(nonKey);
                        
                        for ( String key : complement) {
                                Set<String> row = new HashSet<>();
                                row.add(key);
                                complementSet.add(row);
                        }
                        
                        if (keySet.isEmpty()) {
                                keySet = complementSet;
                        } else {
                                List<Set<String>> newSet = new ArrayList<>();
                                for ( Set<String> pKey : complementSet) {
                                        for (Set<String> key : keySet) {
                                                Set<String> addSet = new HashSet<>(pKey);
                                                addSet.addAll(key);
                                                newSet.add(addSet);
                                        }
                                }
                                //simplify set
                                List<Set<String>> newSet2 = new ArrayList<>(newSet);
                                Iterator<Set<String>> it2 = newSet2.iterator();
                                
                                while(it2.hasNext()) {
                                        Set<String> nk2 = it2.next();
                                        Iterator<Set<String>> it = newSet.iterator();
                                        while (it.hasNext()) {
                                                Set<String> nk = it.next();
                                                if (!nk.equals(nk2) && nk.containsAll(nk2)) {
                                                        it.remove();
                                                }
                                        }
                                }
                                keySet = new ArrayList<>(new HashSet<>(newSet));
                        }
                }
                
                return keySet;
        }
        
        @Override
        public String toString() {
                String res = "";
                
                for (Set<String> row : nonKeySet) {
                        res += StringUtils.join(row, ", ") + "\n";
                }
                
                return res;
        }
        
}
