package soadev;

import groovy.util.Eval;

import java.math.BigDecimal;

import java.util.Date;

import soadev.domain.Job;

public class GroovyTestRunner {
    public static void main(String[] args) {
        GroovyTestRunner runner = new GroovyTestRunner();
        runner.test1();
    }
    
    public void test1(){
        Job job = new Job();
        job.setJobId("job1");
        job.setJobTitle("jobTitle1");
        job.setMaxSalary(new BigDecimal("100"));
//        String expression = "o.jobId == 'job1' && o.jobTitle.any{ it =~ 'jobTitle1'}  && o.maxSalary > 10";
        String expression = "o.jobTitle.matches('.*job.*')";
        Object result = Eval.me("o",job,expression);
        System.out.println(result);
    }
    
    public void testDate(){
        Date now = new Date();
        String expression = "o.getTime()";
        Object result = Eval.me("o",now,expression);
        System.out.println(result);
    }
}
