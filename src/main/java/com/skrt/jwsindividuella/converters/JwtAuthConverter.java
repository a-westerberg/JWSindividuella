package com.skrt.jwsindividuella.converters;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${app.security.client-id:blogg-api}")
    private String clientId;

    private static final List<String> PRINCIPAL_ORDER = List.of("preferred_username", "email", "sub");

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = new LinkedHashSet<>();

        Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
        if (scopeAuthorities != null){
            authorities.addAll(scopeAuthorities);
        }
        authorities.addAll(extractRealmRoles(jwt));
        authorities.addAll(extractClientRoles(jwt, clientId));

        String principal = resolvePrincipal(jwt, PRINCIPAL_ORDER);

        return new JwtAuthenticationToken(jwt, authorities, principal);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object realmAccessObject = jwt.getClaim("realm_access");
        if(!(realmAccessObject instanceof Map<?, ?> realmAccess)){
            return Set.of();
        }
        Object rolesObject = realmAccess.get("roles");
        if(!(rolesObject instanceof Collection<?> roles)) {
            return Set.of();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(role -> !role.isBlank())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Collection<? extends GrantedAuthority> extractClientRoles(Jwt jwt, String clientId) {
        Object resourceAccessObject = jwt.getClaim("resource_access");
        if(!(resourceAccessObject instanceof Map<?, ?> resourceAccess)){
            return Set.of();
        }
        Object clientMapObject = resourceAccess.get(clientId);
        if(!(clientMapObject instanceof Map<?, ?> clientMap)){
            return Set.of();
        }
        Object rolesObject = clientMap.get("roles");
        if(!(rolesObject instanceof Collection<?> roles)){
            return Set.of();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(role -> !role.isBlank())
                .map(role-> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toCollection(LinkedHashSet::new));

    }

    private String resolvePrincipal(Jwt jwt, List<String> order) {
        for(String claim : order) {
            String value = jwt.getClaimAsString(claim);
            if(value != null && !value.isBlank()) return value;
        }
        return jwt.getSubject();
    }

}
