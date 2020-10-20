/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wisc.cs.will.Refine;

import edu.wisc.cs.will.FOPC.Term;
import edu.wisc.cs.will.ILP.SingleClauseNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Rodrigo Azevedo
 */
public class Refine {
        //HashMap<String, RefineNode> refines = null;
        HashMap<String, RefineNode> refines = null;
        int Tree = 0;
        
        public Refine() {
            //refines = new HashMap<String, RefineNode>();
            refines = new HashMap<String, RefineNode>();
        }
        
        public void addNode(int model, String key, String node, boolean leftBranch, boolean rightBranch) {
            String treeNumber = Integer.toString(model);
            RefineNode treeNode = new RefineNode();
            RefineNode rule = null;
            
            if (refines.containsKey(treeNumber)){
                treeNode = refines.get(treeNumber);
            }else{
                refines.put(treeNumber, treeNode);
            }
            
            RefineNode current = treeNode;
            String[] path = key.split(",");
            if (key.isEmpty()){
                //rule = new RefineNode(node, leftBranch, rightBranch);
                //treeNode.setNode(rule);
                treeNode.setNodeValues(node, leftBranch, rightBranch);
            }else{
                //rule = new RefineNode(node, leftBranch, rightBranch);
                for (String p : path) {
                    if (p.equals("true")) {
                        RefineNode left = current.getLeftNode();
                        if (left == null) {
                            left = new RefineNode();
                            current.setLeftNode(left);
                        }
                        current = left;
                    }else{
                        RefineNode right = current.getRightNode();
                        if (right == null) {
                            right = new RefineNode();
                            current.setRightNode(right);
                        }
                        current = right;
                    }
                }
                //current.setNode(rule);
                current.setNodeValues(node, leftBranch, rightBranch);
            }
        }
       
        public RefineNode getRefineNode(boolean[] tree) {
            ArrayList<String> str = new ArrayList<String>();
            for (boolean bool : tree) {
                str.add(String.valueOf(bool));
            }
            if (str.isEmpty())
            {
                return refines.get(Integer.toString(this.Tree));
            }
            RefineNode current = refines.get(Integer.toString(this.Tree));
            for (boolean p : tree) {
                if (p) {
                    current = current.getLeftNode();
                }else{
                    current = current.getRightNode();
                }
            }
            return current;
        }
        
        public boolean containsRefineNode(boolean[] tree) {
            if (tree.length == 0)
            {
                
                return refines.containsKey(Integer.toString(this.Tree)) && refines.get(Integer.toString(this.Tree)) != null && !refines.get(Integer.toString(this.Tree)).getNode().isEmpty();
            }
            RefineNode current = refines.get(Integer.toString(this.Tree));
            if (current == null)
            {
                return false;
            }
            for (boolean p : tree) {
                if (p) {
                    if (current.getLeftNode() == null) {
                        return false;
                    }
                    current = current.getLeftNode();
                }else{
                    if (current.getRightNode() == null) {
                        return false;
                    }
                    current = current.getRightNode();
                }
            }
            return current.getNode() != null && !current.getNode().isEmpty();
        }
        
        public RefineNode getParentRefineNode(boolean[] tree) {
            boolean[] newTree = Arrays.copyOfRange(tree, 0, tree.length-1);
            return getRefineNode(newTree);
        }
        
        public SingleClauseNode getExtensionNode(boolean[] tree) {
            if (tree.length == 0)
            {
                return null;
            }
            RefineNode parent = getParentRefineNode(tree);
            if (tree[tree.length-1] == true)
            {
                return parent.getSingleNode();
            }else{
                return parent.getSingleNoNode();
            }
        }
        
        public HashMap<String, Term> getExtensionNodeVariables(boolean[] tree) {
            if (tree.length == 0)
            {
                return null;
            }
            RefineNode parent = getParentRefineNode(tree);
            if (tree[tree.length-1] == true)
            {
                return parent.getSingleNodeVariables();
            }else{
                return parent.getSingleNoNodeVariables();
            }
        }
        
        public void setTree(int model) {
            this.Tree = model;
        }
        
        public int getTree() {
            return this.Tree;
        }
}
