
package edu.wisc.cs.will.Transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transfer {

    //HashMap<String, String> refines = null;
    //int Tree = 0;
    boolean searchArgPermutation = true;
    boolean searchEmpty = true;
    boolean allowSameTargetMap = true;
    HashMap<String, ArrayList<String>> map = null;

    public Transfer() {
        map = new HashMap<String, ArrayList<String>>();
    }

    public void readLine(String line) {
        String str = line.replaceAll("\\s+", "");
        Pattern paramPattern = Pattern.compile("^setParam:(\\w*)=(\\w*)\\.$");
        Pattern predPattern = Pattern.compile("^(\\w*):([\\w,]*)");
        Pattern mapPattern = Pattern.compile("^setMap:(\\w*),(\\w*)\\.$");
        Matcher paramMatcher = paramPattern.matcher(str);
        Matcher predMatcher = predPattern.matcher(str);
        Matcher mapMatcher = mapPattern.matcher(str);

        print(paramMatcher.group(1));
        print(predMatcher.group(1));
    }
}