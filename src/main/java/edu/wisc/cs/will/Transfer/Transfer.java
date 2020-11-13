
package edu.wisc.cs.will.Transfer;

import edu.wisc.cs.will.Refine.RefineNode;
import edu.wisc.cs.will.Utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thais Luca
 */
public class Transfer {

    //HashMap<String, String> refines = null;
    //int Tree = 0;
    boolean searchArgPermutation = true;
    boolean searchEmpty = true;
    boolean allowSameTargetMap = true;
    HashMap<String, ArrayList<Mapping>> allMaps = null; //Keeps mapping by similarity.
    HashMap<String, Mapping> predsMapped = null;  //If allowSameTargetMap = true, keep the source predicates already mapped to a source.
    HashMap<String, ArrayList<String>> sourceArgs = null; //Keeps all source arguments to transfer variables.
    ArrayList<String> targetsMapped = null; //If allowSameTargetMap = true, keeps all targets already mapped to a source.
    String targetHead = null;

    public Transfer() {
        allMaps = new HashMap<>();
        predsMapped = new HashMap<>();
        targetsMapped = new ArrayList<>();
        sourceArgs = new HashMap<>();
    }

    public void readLine(String line) {
        String str = line.replaceAll("\\s+", "");
        Pattern paramPattern = Pattern.compile("^setParam:(\\w*)=(\\w*)\\.$");
        //Pattern.compile("^(source|target):(\\w*)\\(([\\w,]*)\\)\\.$");
        Pattern predPattern = Pattern.compile("^(\\w+\\([\\w,]+\\)): *((?:[\\w ]+\\([\\w,]+\\),*)+)$");
        Pattern mapPattern = Pattern.compile("^setMap:(\\w+\\([\\w,]+\\)),(\\w+\\([\\w,]+\\))");
        Matcher paramMatcher = paramPattern.matcher(str);
        Matcher predMatcher = predPattern.matcher(str);
        Matcher mapMatcher = mapPattern.matcher(str);
        String targetHead = null;

        if (paramMatcher.find()) {
            if (paramMatcher.group(1).equals("searchArgPermutation")) {
                searchArgPermutation = Boolean.parseBoolean(paramMatcher.group(2));
            } else if (paramMatcher.group(1).equals("searchEmpty")) {
                searchEmpty = Boolean.parseBoolean(paramMatcher.group(2));
            } else if (paramMatcher.group(1).equals("allowSameTargetMap")) {
                allowSameTargetMap = Boolean.parseBoolean(paramMatcher.group(2));
            }
        } else if (predMatcher.find()) {
            Pattern argsPattern = Pattern.compile("(\\w+)\\((.*?)\\)"); //Get the relation name and its literals
            Matcher argstMatcher = null;
            List<String> allPreds = Arrays.asList(predMatcher.group(2).split(",(?![^()]*\\))"));
            ArrayList<Mapping> predsMap = new ArrayList<Mapping>();
            for(int i = 0; i < allPreds.size(); i++){
                String currentTarget = allPreds.get(i);
                argstMatcher = argsPattern.matcher(currentTarget);
                if(argstMatcher.find()) {
                    predsMap.add(new Mapping(argstMatcher.group(1), generateVariables(Arrays.asList(argstMatcher.group(2).split(",")).size())));
                }
            }
            String srcPred = predMatcher.group(1);
            allMaps.put(srcPred.replaceAll("\\(.*\\)", ""), predsMap);
            sourceArgs.put(srcPred.replaceAll("\\(.*\\)", ""), new ArrayList<String>(Arrays.asList(srcPred.split("[\\(\\)]")[1].split(","))));
        } else if (mapMatcher.find()) {
            String srcPred = mapMatcher.group(1);
            String tarPred = mapMatcher.group(2);
            ArrayList<String> srcArgs = new ArrayList<String>(Arrays.asList(srcPred.split("[\\(\\)]")[1].split(",")));
            ArrayList<String> tarArgs = new ArrayList<String>(Arrays.asList(tarPred.split("[\\(\\)]")[1].split(",")));
            if (!compareArgs(srcArgs, generateVariables(srcArgs.size()))) {
                Utils.println("\nSource predicate '" + srcPred + "' has to respect alphabetic order in its variables.");
                //Utils.reportStackTrace(e);
                Utils.error("Unable to successfully define transfer from source predicate '" + srcPred + "' to target predicate '" + tarPred + "'.");
            }
            srcPred = srcPred.replaceAll("\\(.*\\)", "");
            tarPred = tarPred.replaceAll("\\(.*\\)", "");
            allMaps.put(srcPred, new ArrayList<Mapping>(Arrays.asList(new Mapping(tarPred, tarArgs))));
            if (targetHead == null) {
                predsMapped.put(srcPred, new Mapping(tarPred, tarArgs));
                targetsMapped.add(tarPred);
                sourceArgs.put(srcPred, srcArgs);
                setTargetHead(tarPred);
            }
        } else if(!str.startsWith("//") && !str.isEmpty()) {
            Utils.println("\nEncountered an exception during parsing '" + str + "' of transfer file:");
            Utils.error("Unable to successfully parse transfer file: " + str + ".");
        }
    }

    public Object[] transferHead(Object[] head) {
        String predicate = (String) head[0];
        ArrayList<String> vars = new ArrayList<String>(Arrays.asList((String[])head[1]));
        ArrayList<String> toMap = predsMapped.get(predicate).getTargetArguments();
        ArrayList<String> args = transferVariables(vars, toMap);
        return new Object[] {targetHead, args.toArray(new String[args.size()])};
    }

    public ArrayList<Object[]> transferBody(ArrayList<Object[]> body) {
        ArrayList<Object[]> transferredBody = new ArrayList<Object[]>();
        ArrayList<String> vars;
        ArrayList<String> toMap;
        ArrayList<String> args;
        Mapping mapped;
        int size = body.size();
        for (int i = 0; i < size; i++) {
            mapped = null;

            ArrayList<Mapping> currentPredicateMapping = allMaps.get(body.get(i)[0]);
            if(!allowSameTargetMap){
                mapped = currentPredicateMapping.get(0);
            } else {
                //If the predicate is already mapped, gets the legal mapping from mappings
                if(predsMapped.containsKey(body.get(i)[0])){
                    mapped = predsMapped.get(body.get(i)[0]);
                } else {
                    //Finds a legal mapping for the current predicate
                    for (int j = 0; j < currentPredicateMapping.size(); j++) {
                        if (!targetsMapped.contains(currentPredicateMapping.get(j).getTargetPredicate())) {
                            mapped = currentPredicateMapping.get(j);
                            predsMapped.put((String) body.get(i)[0], new Mapping(mapped.getTargetPredicate(), mapped.getTargetArguments()));
                            targetsMapped.add(mapped.getTargetPredicate());
                            break;
                        }
                    }
                }
            }
            if (mapped != null) {
                vars = new ArrayList<>(Arrays.asList((String[])body.get(i)[1]));
                toMap = mapped.getTargetArguments();
                args = transferVariables(vars, toMap);
                transferredBody.add(new Object[] {mapped.getTargetPredicate(), args.toArray(new String[args.size()])});
            }
        }
        return transferredBody;
    }

    private ArrayList<String> transferVariables(ArrayList<String> variables, ArrayList<String> toMap) {
        HashMap<String, String> read = new HashMap<>();
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> fromMap = generateVariables(variables.size());

        for (int i=0; i < variables.size(); i++) {
            read.put(fromMap.get(i), variables.get(i));
        }
        for (int i=0; i < toMap.size(); i++) {
            ret.add(read.get(toMap.get(i)));
        }
        return ret;
    }

    private ArrayList<String> generateVariables(int size) {
        ArrayList<String> vars = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            vars.add(Character.toString((char)(i+65)));
        }
        return vars;
    }

    public void promoteNode(RefineNode node) {
        if (node.getLeftNode() == null)
        {
            RefineNode right = node.getRightNode();
            if (right == null){
                node.setNode(null);
            }else{
                node.setNode(right.getNode());
                node.setLeftBranch(right.getLeftBranch());
                node.setRightBranch(right.getRightBranch());
                node.setLeftNode(right.getLeftNode());
                node.setRightNode(right.getRightNode());
            }
        }else{
            RefineNode right = node.getRightNode();
            RefineNode left = node.getLeftNode();
            node.setNode(left.getNode());
            node.setLeftBranch(left.getLeftBranch());
            node.setRightBranch(left.getRightBranch());
            node.setLeftNode(left.getLeftNode());
            node.setRightNode(left.getRightNode());
            if (right != null) {
                sendNodeToFalse(node, right);
            }
        }
    }

    private void sendNodeToFalse(RefineNode node, RefineNode send)
    {
        RefineNode current = node;
        while (current.getRightNode() != null)
        {
            current = current.getRightNode();
        }
        current.setRightNode(send);
        current.setRightBranch(true);
    }

    private boolean compareArgs(ArrayList<String> a, ArrayList<String> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i=0; i< a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }


    public String getTargetHead() {
        return targetHead;
    }

    public void setTargetHead(String targetHead) {
        this.targetHead = targetHead;
    }
}