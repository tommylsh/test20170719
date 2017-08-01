package com.maxim.pos.common.value;

public class SchemeScheduleJobQueryCriteria extends CommonCriteria {

    private String jobName;
    private String jobNameKeyword;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobNameKeyword() {
        return jobNameKeyword;
    }

    public void setJobNameKeyword(String jobNameKeyword) {
        this.jobNameKeyword = jobNameKeyword;
    }
}
