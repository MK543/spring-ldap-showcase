package com.softwaremind.spring_ldap.controller;


import com.softwaremind.spring_ldap.dto.SearchRequest;
import com.softwaremind.spring_ldap.dto.UserDTO;
import com.softwaremind.spring_ldap.service.LDAPUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final LDAPUserService ldapUserService;

    @PostMapping("/get")
    public ResponseEntity<UserDTO> getUser(@RequestBody SearchRequest searchRequest) {
        log.info("[getUser] Received request for LDAP User entry wit baseDN: {}", searchRequest.getBaseDN());
        UserDTO userDTO = ldapUserService.getUserAttributes(searchRequest.getBaseDN());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/modify")
    public ResponseEntity<Object> modifyUser(@RequestBody UserDTO userDTO) {
        log.info("[modifyUser] Received modify or create request for LDAP User entry: {}", userDTO);
        ldapUserService.createOrModifyUser(userDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestBody SearchRequest searchRequest) {
        log.info("[deleteUser] Received delete request for LDAP User entry: {}", searchRequest.getBaseDN());
        ldapUserService.deleteUser(searchRequest.getBaseDN());
        return ResponseEntity.noContent().build();
    }
}
