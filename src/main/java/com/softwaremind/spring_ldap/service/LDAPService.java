package com.softwaremind.spring_ldap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LDAPService {

    private static final String DEFAULT_FILTER = "(objectClass=*)";

    private final LdapTemplate ldapTemplate;

    public Map<String, Object> getEntryAttributes(String baseDN, List<String> attributes) {
        return getEntriesAttributes(baseDN, attributes).getFirst();
    }

    private List<Map<String, Object>> getEntriesAttributes(String baseDN, List<String> attributes) {
        return ldapTemplate.search(getDefaultQuery(baseDN, attributes), getDefaultMapper(attributes));
    }


    private LdapQuery getDefaultQuery(String baseDN, List<String> attributes) {

        String[] attrs = attributes.toArray(new String[0]);
        return LdapQueryBuilder.query()
                .base(baseDN)
                .attributes(attrs)
                .searchScope(SearchScope.OBJECT)
                .filter(DEFAULT_FILTER);
    }

    private AttributesMapper<Map<String, Object>> getDefaultMapper(List<String> attributes) {
        return attrs -> {
            Map<String, Object> result = new HashMap<>();
            attributes.forEach(attr -> {
                try {
                    Attribute attribute = attrs.get(attr);
                    mapAttribute(result, attr, attribute);
                } catch (NamingException e) {
                    log.error("Unable to map attribute", e);
                }
            });
            if (log.isDebugEnabled()) {
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    log.debug("Found entry attribute {} = {}", entry.getKey(), entry.getValue());
                }
            }
            return result;
        };
    }

    private void mapAttribute(Map<String, Object> map, String name, Attribute attribute) throws NamingException {
        if (attribute != null) {
            NamingEnumeration<?> values = attribute.getAll();
            List<String> valuesList = new ArrayList<>();
            while (values.hasMore()) {
                valuesList.add(values.next().toString());
            }
            if (valuesList.size() == 1) {
                map.put(name, valuesList.getFirst());
            } else {
                map.put(name, valuesList);
            }
        }

    }

    public void createEntry(String baseDN, Map<String, Object> attributes) {

        if (!attributes.containsKey("objectClass")) {
            throw new IllegalArgumentException("Attribute 'objectClass' is required");
        }

        Name dn = LdapUtils.newLdapName(baseDN);
        Attributes attrs = new BasicAttributes();
        attributes.forEach((key, value) -> {
            Attribute attr = new BasicAttribute(key);
            if (value instanceof Collection) {
                ((Collection<?>) value).forEach(attr::add);
            } else {
                attr.add(value);
            }
            attrs.put(attr);
        });

        if (!attributes.containsKey("objectClass")) {
            Attribute ocAttr = new BasicAttribute("objectClass");
            ocAttr.add("top");
            ocAttr.add("person");
            attrs.put(ocAttr);
        }

        ldapTemplate.bind(dn, null, attrs);
        log.info("Entry created successfully with DN: {}", baseDN);
    }

    public void modifyEntry(String baseDN, Map<String, Object> attributesToModify) {
        Name dn = LdapUtils.newLdapName(baseDN);
        List<ModificationItem> mods = new ArrayList<>();

        attributesToModify.forEach((key, value) -> {
            Attribute attr = new BasicAttribute(key);
            if (value == null) {
                mods.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr));
            } else if (value instanceof Collection) {
                ((Collection<?>) value).forEach(attr::add);
                mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr));
            } else {
                attr.add(value);
                mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr));
            }
        });

        ldapTemplate.modifyAttributes(dn, mods.toArray(new ModificationItem[0]));
        log.info("Entry modified successfully with DN: {}", baseDN);
    }

    public void deleteEntry(String baseDN) {
        Name dn = LdapUtils.newLdapName(baseDN);
        ldapTemplate.unbind(dn);
        log.info("Entry deleted successfully with DN: {}", baseDN);
    }

//    public void deleteAttributes(String baseDN, List<String> attributesToDelete) {
//        Name dn = LdapUtils.newLdapName(baseDN);
//        List<ModificationItem> mods = new ArrayList<>();
//
//        attributesToDelete.forEach(attrName -> {
//            Attribute attr = new BasicAttribute(attrName);
//            mods.add(new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr));
//        });
//
//        ldapTemplate.modifyAttributes(dn, mods.toArray(new ModificationItem[0]));
//        log.info("Attributes deleted successfully from entry with DN: {}", baseDN);
//    }
}
