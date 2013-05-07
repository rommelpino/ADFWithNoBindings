package soadev.domain;

import java.math.BigDecimal;

public class Job {
    private String jobId;
    private String jobTitle;
    private BigDecimal maxSalary;
    private BigDecimal minSalary;
    private JobType jobType;

    public Job() {
    }

    public Job(String jobId, String jobTitle, BigDecimal maxSalary,
               BigDecimal minSalary, JobType jobType) {
        super();
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.maxSalary = maxSalary;
        this.minSalary = minSalary;
        this.jobType = jobType;
    }

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

    @Override
    public String toString() {
        return super.toString() + getJobTitle();
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public JobType getJobType() {
        return jobType;
    }
}
