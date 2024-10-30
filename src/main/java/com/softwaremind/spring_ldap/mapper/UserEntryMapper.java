package com.softwaremind.spring_ldap.mapper;

import com.softwaremind.spring_ldap.dto.UserDTO;
import com.softwaremind.spring_ldap.model.UserEntry;
import org.springframework.stereotype.Component;

@Component
public class UserEntryMapper {

    public UserDTO toDTO(UserEntry user) {
        return UserDTO.builder()
                .uniqueName(user.getUniqueName())
                .phoneNumber(user.getPhoneNumber())
                .fname(user.getFname())
                .lname(user.getLname())
                .address(user.getAddress())
                .build();
    }
}
