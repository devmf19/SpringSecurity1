package com.consiti.springsecurity1.security.controller;

import com.consiti.springsecurity1.dto.Message;
import com.consiti.springsecurity1.security.dto.JwtDto;
import com.consiti.springsecurity1.security.dto.LoginDto;
import com.consiti.springsecurity1.security.dto.NewUserDto;
import com.consiti.springsecurity1.security.entity.Role;
import com.consiti.springsecurity1.security.entity.User;
import com.consiti.springsecurity1.security.enums.RoleName;
import com.consiti.springsecurity1.security.jwt.JwtProvider;
import com.consiti.springsecurity1.security.service.RoleService;
import com.consiti.springsecurity1.security.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    JwtProvider jwtProvider;

    //create new user
    @PostMapping("")
    public ResponseEntity<Message> newUser(@Valid @RequestBody NewUserDto newUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<Message>(new Message("Verifique los datos introducidos"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existByUsername(newUser.getUsername())) {
            return new ResponseEntity<Message>(new Message("El nombre de usuario ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        if (userService.existByEmail(newUser.getEmail())) {
            return new ResponseEntity<Message>(new Message("El email ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }

        User user = new User(newUser.getName(),
                newUser.getEmail(),
                newUser.getUsername(),
                 passwordEncoder.encode(newUser.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getByRoleName(RoleName.ROLE_USER).get());

        if (newUser.getRoles().contains("admin")) {
            roles.add(roleService.getByRoleName(RoleName.ROLE_ADMIN).get());
        }

        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<Message>(new Message("Usuario registrado con éxito"), HttpStatus.CREATED);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginUser, BindingResult bindingResult) {
        log.info("user: "+loginUser.getUsername());
        log.info("password: "+loginUser.getPassword());

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<Message>(new Message("Usuario inválido"), HttpStatus.UNAUTHORIZED);
        }
        //authentication
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //authorization
        String jwt = jwtProvider.generateToken(authentication);
        JwtDto jwtDto = new JwtDto(jwt);

        User user = userService.getByUsername(loginUser.getUsername()).get();
        String roles = user.getRoles()
                .stream()
                .map(e-> e.getRoleName())
                .collect(Collectors.toList()).toString();

        HashMap<String, String> response = new HashMap<>();
        response.put("token", jwtDto.getToken());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("name",user.getName());
        response.put("roles", roles);

        return new ResponseEntity<Object>(response, HttpStatus.ACCEPTED);
    }

    //refresh token
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestBody JwtDto jwtDto) throws ParseException {
        String token = jwtProvider.refreshToken(jwtDto);
        JwtDto jwt = new JwtDto(token);
        return new ResponseEntity<JwtDto>(jwt, HttpStatus.OK);
    }
}
