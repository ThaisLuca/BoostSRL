/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Refine;

import edu.wisc.cs.will.FOPC.Term;
import edu.wisc.cs.will.ILP.SingleClauseNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import javafx.util.Pair;

/**
 *
 * @author Rodrigo Azevedo
 */
public class RefineNode {
        private String node;
        private boolean leftBranch;
        private boolean rightBranch;
        private RefineNode leftNode;
        private RefineNode rightNode;
        private SingleClauseNode singleNode;
        private HashMap<String, Term> singleNodeVariables;
        private SingleClauseNode singleNoNode;
        private HashMap<String, Term> singleNoNodeVariables;
        
        public RefineNode(String node, boolean leftBranch, boolean rightBranch) {
            this.node = node;
            this.leftBranch = leftBranch;
            this.rightBranch = rightBranch;
            this.singleNode = null;
        }

        public RefineNode() {
            this.node = "";
            this.singleNode = null;
        }
        
        public String getNode() {
            return node;
        }
        
        public void setNodeValues(String node, boolean leftBranch, boolean rightBranch) {
            this.node = node;
            this.leftBranch = leftBranch;
            this.rightBranch = rightBranch;
        }
        
        public void setNode(String node) {
            this.node = node;
        }
        
        public void setSingleNode(SingleClauseNode node) {
            this.singleNode = node;
        }
        
        public SingleClauseNode getSingleNode() {
            return this.singleNode;
        }
        
        public void setSingleNodeVariables(HashMap<String, Term> dict) {
            this.singleNodeVariables = dict;
        }
        
        public HashMap<String, Term> getSingleNodeVariables() {
            return this.singleNodeVariables;
        }
        
        public void setSingleNoNode(SingleClauseNode node) {
            this.singleNoNode = node;
        }
        
        public SingleClauseNode getSingleNoNode() {
            return this.singleNoNode;
        }
        
        public void setSingleNoNodeVariables(HashMap<String, Term> dict) {
            this.singleNoNodeVariables = dict;
        }
        
        public HashMap<String, Term> getSingleNoNodeVariables() {
            return this.singleNoNodeVariables;
        }
        
        public Object[] getTargetPredicate() {
            String pattern = "([a-zA-Z_0-9]*)\\s*\\(([a-zA-Z_0-9,\\s]*)\\)";
            String[] headBody = node.split(":-");
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(headBody[0]);
            while (m.find( )) {
                //return new Pair<String, String[]>(m.group(1), m.group(2).replace(" ", "").split(","));
                return new Object[] { m.group(1), m.group(2).replace(" ", "").split(",") };
            }
            return null;
        }
        
        //public Pair<String, String[]>[] getPredicates() {
        public ArrayList<Object[]> getPredicates() {    
            String pattern = "([a-zA-Z_0-9]*)\\s*\\(([a-zA-Z_0-9,\\s]*)\\)";
            String[] headBody = node.split(":-");
            int index = headBody.length == 2 ? 1 : 0;
            //if(headBody.length == 2) {
            //ArrayList<Pair<String, String[]>> pairs = new ArrayList<Pair<String, String[]>>();
            ArrayList<Object[]> pairs = new ArrayList<Object[]>();
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(headBody[index]);
            while (m.find( )) {
                //pairs.add(new Pair<String, String[]>(m.group(1), m.group(2).replace(" ", "").split(",")));
                pairs.add(new Object[] { m.group(1), m.group(2).replace(" ", "").split(",") });
            }
            return pairs;
            //}
            //return null;
        }
        
        public boolean getLeftBranch() {
            return leftBranch;
        }
        
        public boolean getRightBranch() {
            return rightBranch;
        }
        
        public void setLeftBranch(boolean leftBranch) {
            this.leftBranch = leftBranch;
        }
        
        public void setRightBranch(boolean rightBranch) {
            this.rightBranch = rightBranch;
        }
        
        public RefineNode getLeftNode() {
            return leftNode;
        }
        
        public void setLeftNode(RefineNode node) {
            leftNode = node;
        }
        
        public RefineNode getRightNode() {
            return rightNode;
        }
        
        public void setRightNode(RefineNode node) {
            rightNode = node;
        }

}
