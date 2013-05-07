package soadev.domain;

import java.io.PrintStream;

import java.util.*;

import groovy.util.Eval;

import junit.framework.*;

import static junit.framework.Assert.*;

public class EvalGroovyTest {
    public static void main(String[] args) {
        String result =
            Eval.me("def language = 'Groovy'; \"Hello from $language\";").toString();
        assertEquals("Invoke Eval.me() without arguments", "Hello from Groovy",
                     result);
        System.out.println(result);
        final Map values = new HashMap();
        values.put("name", "mrhaki");
        values.put("lang", "Groovy");
        String expression = "\"Hello $params.name from $params.lang\"";
        assertEquals("Invoke Eval.me() with 2 arguments: first is name of object used by expression, second is object self",
                     "Hello mrhaki from Groovy",
                     Eval.me("params", values, expression).toString());

        assertTrue("Invoke Eval.x() where the passed arguments name is x in the expression",
                   (Boolean)Eval.x("mrhaki", "x.any { it =~ 'a' }"));

        assertTrue("Invoke Eval.xy() where the passed arguments name is x and y in the expression",
                   (Boolean)Eval.xy("mrhaki", "h", "x.any { it =~ y }"));

        expression =
                "x.\"$z\"() * y"; // Unreadable expression to return x with the method z applied y times.
        assertEquals("Invoke Eval.xyz() where the passed arguments name is x, y and z in the expression",
                     "GROOVYGROOVY",
                     Eval.xyz("groovy", 2, "toUpperCase", expression).toString());
    }

}
