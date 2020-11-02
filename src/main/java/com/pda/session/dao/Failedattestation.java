package com.pda.session.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "failedattestation")
public class Failedattestation extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String event_type;
    private String program_name;
    private String role;
    private Long session_id;
    private String session_start_date;
    private String session_end_date;
    private String topic_name;
    private String user_id;
    private int number_of_participants;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getNumber_of_participants() {
        return number_of_participants;
    }

    public void setNumber_of_participants(int number_of_participants) {
        this.number_of_participants = number_of_participants;
    }

    public Failedattestation() {
    }

    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getSession_id() {
        return session_id;
    }

    public void setSession_id(Long session_id) {
        this.session_id = session_id;
    }

    public String getSession_start_date() {
        return session_start_date;
    }

    public void setSession_start_date(String session_start_date) {
        this.session_start_date = session_start_date;
    }

    public String getSession_end_date() {
        return session_end_date;
    }

    public void setSession_end_date(String session_end_date) {
        this.session_end_date = session_end_date;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
