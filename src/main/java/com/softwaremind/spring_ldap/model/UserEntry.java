package com.softwaremind.spring_ldap.model;

import lombok.*;
import org.springframework.data.domain.Persistable;
import org.springframework.ldap.odm.annotations.*;

import javax.naming.Name;

@Entry(
        base = "ou=users,o=organization.com",
        objectClasses = {"top", "user"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class UserEntry implements Persistable<Name> {


    @Id
    private Name id;

    @DnAttribute(value = "uniqueName", index = 0)
    @Attribute(name = "uniqueName")
    private String uniqueName;

    @Attribute
    private String phoneNumber;

    @Attribute
    private String fname;

    @Attribute
    private String lname;

    @Attribute
    private String address;

    @Transient
    private boolean isNewRecord;

    @Override
    public boolean isNew() {
        return this.isNewRecord;
    }

    public void setNew(boolean isNew) {
        this.isNewRecord = isNew;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "id=" + id +
                ", uniqueName='" + uniqueName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
