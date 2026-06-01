package edu.unimag.medical.security.service;

import edu.unimag.medical.exception.ConflictException;
import edu.unimag.medical.security.domain.AppUser;
import edu.unimag.medical.security.domain.Role;
import edu.unimag.medical.security.dto.AuthDtos.*;
import edu.unimag.medical.security.jwt.JwtService;
import edu.unimag.medical.security.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository users;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictException("User with email " + req.email() + " already exists");
        }

        var roles = Set.of(Role.ROLE_PATIENT);
        var user  = AppUser.builder()
                .email(req.email())
                .password(encoder.encode(req.password()))
                .roles(roles)
                .build();

        users.save(user);

        var principal = buildPrincipal(user);
        var token     = jwt.generateToken(principal, Map.of("roles", roleNames(roles)));

        return new AuthResponse(token, "Bearer", jwt.getExpirationSeconds(),
                principal.getUsername(), roles);
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        var user      = users.findByEmailIgnoreCase(req.email()).orElseThrow();
        var principal = buildPrincipal(user);
        var token     = jwt.generateToken(principal, Map.of("roles", roleNames(user.getRoles())));

        return new AuthResponse(token, "Bearer", jwt.getExpirationSeconds(),
                principal.getUsername(), user.getRoles());
    }

    @Transactional
    public AuthResponse createStaff(CreateStaffRequest req) {
        if (users.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictException("Staff member with email " + req.email() + " already exists");
        }

        var user = AppUser.builder()
                .email(req.email())
                .password(encoder.encode(req.password()))
                .roles(req.roles())
                .build();

        users.save(user);

        var principal = buildPrincipal(user);
        var token     = jwt.generateToken(principal, Map.of("roles", roleNames(user.getRoles())));

        return new AuthResponse(token, "Bearer", jwt.getExpirationSeconds(),
                principal.getUsername(), user.getRoles());
    }


    private UserDetails buildPrincipal(AppUser user) {
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }

    private java.util.List<String> roleNames(Set<Role> roles) {
        return roles.stream().map(Enum::name).toList();
    }
}