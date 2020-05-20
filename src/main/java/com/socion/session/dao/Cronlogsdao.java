package com.socion.session.dao;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Cronlogsdao {
    @Id
    @GeneratedValue
    private Long id;
    private String nameofcron;
    private String startofcron;
    private Long totalrecords;
    private Long successrecords;
    private Long failedrecords;

    public Cronlogsdao() {
    }

    public Long getTotalrecords() {
        return totalrecords;
    }

    public void setTotalrecords(Long totalrecords) {
        this.totalrecords = totalrecords;
    }

    public Long getSuccessrecords() {
        return successrecords;
    }

    public void setSuccessrecords(Long successrecords) {
        this.successrecords = successrecords;
    }

    public Long getFailedrecords() {
        return failedrecords;
    }

    public void setFailedrecords(Long failedrecords) {
        this.failedrecords = failedrecords;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String endofcron;
    private Boolean Success;

    public String getNameofcron() {
        return nameofcron;
    }

    public void setNameofcron(String nameofcron) {
        this.nameofcron = nameofcron;
    }

    public String getStartofcron() {
        return startofcron;
    }

    public void setStartofcron(String startofcron) {
        this.startofcron = startofcron;
    }

    public String getEndofcron() {
        return endofcron;
    }

    public void setEndofcron(String endofcron) {
        this.endofcron = endofcron;
    }

    public Boolean getSuccess() {
        return Success;
    }

    public void setSuccess(Boolean success) {
        Success = success;
    }
}