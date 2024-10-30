package com.softwaremind.spring_ldap.repository;


import com.softwaremind.spring_ldap.model.UserEntry;
import org.springframework.data.ldap.repository.LdapRepository;

public interface UserRepository extends LdapRepository<UserEntry> {

}
