package com.example.projetTechnique.service;

import com.example.projetTechnique.exception.CanardApiExecption;
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
import java.util.Set;

@AllArgsConstructor
@Service
public class AuthService {


    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;



    public String loggin(Login login) {
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(login.getUsernameOrEmail(),login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return token;
    }

    public User register(User user) {
        User userByUserName = userRepository.findByUserName(user.getUserName());
        if (userByUserName != null){
            throw  new CanardApiExecption(HttpStatus.BAD_REQUEST, "Username is already exists");
        }
        User userByEmail = userRepository.findByEmail(user.getEmail());

        if (userByEmail != null){
            throw  new CanardApiExecption(HttpStatus.BAD_REQUEST,"Email is already exists");
        }

        User userToSave = new User();
        userToSave.setEmail(user.getEmail());
        userToSave.setLastName(user.getLastName());
        userToSave.setFirstName(user.getFirstName());
        userToSave.setUserName(user.getUserName());
        userToSave.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findRoleByName("USER").get();
        roles.add(userRole);
        userToSave.setRoles(roles);
        userRepository.save(userToSave);

        return userToSave;
    }
}
