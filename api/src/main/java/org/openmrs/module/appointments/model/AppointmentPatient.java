package org.openmrs.module.appointments.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.User;

public class AppointmentPatient extends BaseOpenmrsData {
    private Integer patientId;
    private String firstName;
    private String lastName;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer specialityId) {
        this.patientId = specialityId;
    }

    @Override
    public Integer getId() {
        return getPatientId();
    }

    @Override
    public void setId(Integer id) {
        setPatientId(id);
    }
}
