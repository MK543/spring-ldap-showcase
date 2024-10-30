package com.softwaremind.spring_ldap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.factory.PooledContextSource;
import org.springframework.ldap.pool2.validation.DefaultDirContextValidator;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableLdapRepositories(basePackages = "com.softwaremind.spring_ldap.**")
@RequiredArgsConstructor
public class LDAPConfig {

    private final LdapProperties ldapProperties;

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(pooledContextSource());
    }

    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        String[] urls = Arrays.stream(ldapProperties.getUrls().split(","))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        ldapContextSource.setUrls(urls);
        log.info("Base: {}", ldapProperties.getBase());
//        ldapContextSource.setBase(ldapProperties.getBase());
        ldapContextSource.setUserDn(ldapProperties.getUsername());
        ldapContextSource.setPassword(ldapProperties.getPassword());
        ldapContextSource.setPooled(false);
        ldapContextSource.setAnonymousReadOnly(false);
        ldapContextSource.afterPropertiesSet();
        return ldapContextSource;
    }

    @Bean
    public PooledContextSource pooledContextSource() {
        PooledContextSource pooledContextSource = new PooledContextSource(poolConfig());
        pooledContextSource.setContextSource(ldapContextSource());
        pooledContextSource.setDirContextValidator(new DefaultDirContextValidator());
        return pooledContextSource;
    }

    @Bean
    public PoolConfig poolConfig() {
        PoolConfig poolConfig = new PoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        return poolConfig;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
