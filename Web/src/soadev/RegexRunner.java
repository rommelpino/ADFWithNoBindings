package soadev;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexRunner {
    public RegexRunner() {
    
    }
    public static void main(String[] args) {
        String regex ="\\w*jobTile1";
        String input ="jobTile1";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        System.out.println(m.matches());
    }
}
