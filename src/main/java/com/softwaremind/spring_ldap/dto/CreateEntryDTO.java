package com.softwaremind.spring_ldap.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class CreateEntryDTO {

    private String dn;

    private Map<String, Object> attributes;
}
