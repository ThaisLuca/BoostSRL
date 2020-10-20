/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Transfer;

import java.util.ArrayList;

/**
 *
 * @author Rodrigo Azevedo
 */
public class Mapping {
        private String targetPredicate;
        private ArrayList<String> targetArguments;
        
        public Mapping(String targetPredicate, ArrayList<String> targetArguments) {
            this.targetPredicate = targetPredicate;
            this.targetArguments = targetArguments;
        }
        
        public String getTargetPredicate() {
            return targetPredicate;
        }
        
        public ArrayList<String> getTargetArguments() {
            return targetArguments;
        }
        
        public String toString() {
            return targetPredicate + "(" + String.join(",", targetArguments)+ ")";
        }
}
