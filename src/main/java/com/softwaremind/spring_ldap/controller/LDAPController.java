package com.softwaremind.spring_ldap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaremind.spring_ldap.dto.CreateEntryDTO;
import com.softwaremind.spring_ldap.dto.SearchRequest;
import com.softwaremind.spring_ldap.service.LDAPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/ldap")
@RequiredArgsConstructor
@Slf4j
public class LDAPController {

    private final LDAPService ldapService;

    private final ObjectMapper objectMapper;

    @PostMapping("/get")
    public ResponseEntity<String> getEntryAttributes(@RequestBody SearchRequest searchRequest) {
        log.info("[getEntryAttributes] Received request for LDAP entry {} for attributes: {}", searchRequest.getBaseDN(), searchRequest.getAttributes());
        Map<String, Object> entryAttributes = ldapService.getEntryAttributes(searchRequest.getBaseDN(), searchRequest.getAttributes());
        try {
            String json = objectMapper.writeValueAsString(entryAttributes);
            log.debug("[getEntryAttributes] Response from LDAP: {}", json);
            return ResponseEntity.ok(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createEntry(@RequestBody CreateEntryDTO createEntryDTO) {
        log.info("[createEntry] Received create request for LDAP entry {} for attributes: {}", createEntryDTO.getDn(), createEntryDTO.getAttributes());
        ldapService.createEntry(createEntryDTO.getDn(), createEntryDTO.getAttributes());
        return ResponseEntity.ok().build();

    }

    @PostMapping("/modify")
    public ResponseEntity<Object> modifyEntry(@RequestBody CreateEntryDTO createEntryDTO) {
        log.info("[modifyEntry] Received modify request for LDAP entry {} for attributes: {}", createEntryDTO.getDn(), createEntryDTO.getAttributes());
        ldapService.modifyEntry(createEntryDTO.getDn(), createEntryDTO.getAttributes());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteEntry(@RequestBody SearchRequest searchRequest) {
        log.info("[deleteEntry] Received deleted request for LDAP entry {}", searchRequest.getBaseDN());
        ldapService.deleteEntry(searchRequest.getBaseDN());
        return ResponseEntity.ok().build();

    }
}
