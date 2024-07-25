package com.example.projetTechnique.service;

import com.example.projetTechnique.exception.CanardApiExecption;
import com.example.projetTechnique.model.JwtAuthResponse;
import com.example.projetTechnique.model.Login;
import com.example.projetTechnique.model.Role;
import com.example.projetTechnique.model.User;
import com.example.projetTechnique.repository.RoleRepository;
import com.example.projetTechnique.repository.UserRepository;
import com.example.projetTechnique.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class AuthService {


    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;



    public JwtAuthResponse loggin(Login login) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userOptional =userRepository.findByEmail(login.getEmail());
        if (userOptional==null) {
            throw new CanardApiExecption(HttpStatus.UNAUTHORIZED, "Invalid username or email");
        }
        String token = jwtTokenProvider.generateToken(authentication);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccesToken(token);

        return jwtAuthResponse;
    }

    public User register(User user) {
        User userByUserName = userRepository.findByUserName(user.getUsername());
        if (userByUserName != null) {
            throw new CanardApiExecption(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail != null) {
            throw new CanardApiExecption(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User userToSave = new User();
        userToSave.setEmail(user.getEmail());
        userToSave.setLastName(user.getLastName());
        userToSave.setFirstName(user.getFirstName());
        userToSave.setUserName(user.getUsername());
        userToSave.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        Optional<Role> userRoleOpt = roleRepository.findRoleByName("USER");
        Role userRole;
        if (userRoleOpt.isEmpty()) {
            Role role = new Role();
            role.setName("USER");
            userRole = roleRepository.save(role);
        } else {
            userRole = userRoleOpt.get();
        }
        roles.add(userRole);
        userToSave.setRoles(roles);

        return userRepository.save(userToSave);
    }

}
