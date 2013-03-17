package soadev.domain;

import java.math.BigDecimal;

public class Job {
    private String jobId;
    private String jobTitle;
    private BigDecimal maxSalary;
    private BigDecimal minSalary;

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }
}
