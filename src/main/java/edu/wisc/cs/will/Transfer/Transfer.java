
package edu.wisc.cs.will.Transfer;

import edu.wisc.cs.will.Utils.Utils;

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
    HashMap<String, ArrayList<Mapping>> map = null;
    HashMap<String, Mapping> predsMapped = null;
    String targetHead = null;

    public Transfer() {
        map = new HashMap<String, ArrayList<Mapping>>();
        predsMapped = new HashMap<String, Mapping>();
    }

    public void readLine(String line) {
        String str = line.replaceAll("\\s+", "");
        Pattern paramPattern = Pattern.compile("^setParam:(\\w*)=(\\w*)\\.$");
        //Pattern.compile("^(source|target):(\\w*)\\(([\\w,]*)\\)\\.$");
        Pattern predPattern = Pattern.compile("^(\\w*\\([\\w,]*\\)):([\\w \\([\\w,]*\\),]*)\\.$");
        Pattern mapPattern = Pattern.compile("^setMap:(\\w*),(\\w*)\\.$");
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
            Pattern targetPattern = Pattern.compile("(\\w*)\\(([\\w,]*)\\)");
            Matcher targetMatcher = null;
            List<String> allPreds = Arrays.asList(predMatcher.group(2).split(",(?![^()]*\\))"));
            ArrayList<Mapping> predsMap = new ArrayList<Mapping>();
            for(int i = 0; i < allPreds.size(); i++){
                targetMatcher = targetPattern.matcher(allPreds.get(i));
                predsMap.add(new Mapping(targetMatcher.group(1), new ArrayList<String>(Arrays.asList(targetMatcher.group(2).split(",")))));
            }
            map.put(predMatcher.group(1), predsMap);
        } else if (mapMatcher.find()) {
            String srcPred = mapMatcher.group(1);
            String tarPred = mapMatcher.group(2);

            ArrayList<String> tarArgs = new ArrayList<String>(Arrays.asList(tarPred.replace("(", "").replace(")", "").split(",")));

            map.put(srcPred, new ArrayList<Mapping>(Arrays.asList(new Mapping(tarPred, tarArgs))));
            if (targetHead == null) {
                predsMapped.put(tarPred, new Mapping(tarPred, tarArgs));
                targetHead = tarPred;
            }

        } else if(!str.startsWith("//") && !str.isEmpty()) {
            Utils.println("\nEncountered an exception during parsing '" + str + "' of transfer file:");
            Utils.error("Unable to successfully parse transfer file: " + str + ".");
        }
    }

    public Object[] transferHead(Object[] head) {
        String predicate = (String) head[0];
        ArrayList<String> vars = new ArrayList<String>(Arrays.asList((String[])head[1]));
        ArrayList<String> toMap = map.get(predicate).get(0).getTargetArguments();
        ArrayList<String> args = transferVariables(vars, toMap);
        return new Object[] {targetHead, args.toArray(new String[args.size()])};
    }

    public ArrayList<Object[]> transferBody(ArrayList<Object[]> body) {
        ArrayList<Object[]> transferredBody = new ArrayList<Object[]>();
        ArrayList<String> vars;
        ArrayList<String> toMap;
        ArrayList<String> args;
        Mapping mapped = null;
        int size = body.size();
        for (int i = 0; i < size; i++) {
            if(!allowSameTargetMap){
                ArrayList<Mapping> currentPredicateMapping = map.get((String) body.get(i)[0]);
                for(int j = 0; j < currentPredicateMapping.size(); j++){
                    if(!predsMapped.containsKey(currentPredicateMapping.get(j).getTargetPredicate())){
                        mapped = currentPredicateMapping.get(j);
                        predsMapped.put(mapped.getTargetPredicate(), new Mapping(mapped.getTargetPredicate(), mapped.getTargetArguments()));
                        break;
                    }
                }
            }
            else {
                mapped = map.get((String) body.get(i)[0]).get(0);
            }
            if(mapped != null){
                vars = new ArrayList<String>(Arrays.asList((String[])body.get(i)[1]));
                toMap = mapped.getTargetArguments();
                args = transferVariables(vars, toMap);
                transferredBody.add(new Object[] {mapped.getTargetPredicate(), args.toArray(new String[args.size()])});
            }
        }
        return transferredBody;
    }

    private ArrayList<String> transferVariables(ArrayList<String> variables, ArrayList<String> toMap) {
        HashMap<String, String> read = new HashMap<String, String>();
        ArrayList<String> ret = new ArrayList<String>();
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
}