package com.softwaremind.spring_ldap.service;

import com.softwaremind.spring_ldap.dto.UserDTO;
import com.softwaremind.spring_ldap.mapper.UserEntryMapper;
import com.softwaremind.spring_ldap.model.UserEntry;
import com.softwaremind.spring_ldap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LDAPUserService {

    private final UserRepository userRepository;

    private final UserEntryMapper userEntryMapper;

    private Optional<UserEntry> findByUsername(String baseDN) {
        return userRepository.findById(LdapUtils.newLdapName(baseDN));
    }

    public UserDTO getUserAttributes(String baseDN) {
        Optional<UserEntry> optionalUserEntry = findByUsername(baseDN);
        UserEntry userEntry = optionalUserEntry.orElseThrow(() -> new RuntimeException("User does not exists"));
        return userEntryMapper.toDTO(userEntry);
    }

    public void createOrModifyUser(UserDTO userDTO) {
        Optional<UserEntry> optionalUserEntry = findByUsername(userDTO.getBaseDN());
        UserEntry.UserEntryBuilder updatedEntryBuilder = UserEntry.builder()
                .phoneNumber(userDTO.getPhoneNumber())
                .uniqueName(userDTO.getUniqueName())
                .fname(userDTO.getFname())
                .lname(userDTO.getLname())
                .address(userDTO.getAddress())
                .id(LdapUtils.newLdapName(userDTO.getBaseDN()));
        if (optionalUserEntry.isEmpty()) {
            updatedEntryBuilder.isNewRecord(true);
            log.info("Creating user with DN: {}", userDTO.getBaseDN());
        } else {
            log.info("Updating user with DN: {}", userDTO.getBaseDN());
        }
        UserEntry userEntry = updatedEntryBuilder.build();
        userRepository.save(userEntry);
    }

    public void deleteUser(String baseDN) {
        Optional<UserEntry> optionalUserEntry = findByUsername(baseDN);
        UserEntry userEntry = optionalUserEntry.orElseThrow(() -> new RuntimeException("User does not exists"));
        userRepository.delete(userEntry);
    }


}
