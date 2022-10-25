package com.example.demo.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modelo.ERole;
import com.example.demo.modelo.Role;
import com.example.demo.modelo.User;
import com.example.demo.modelo.dao.RoleRepository;
import com.example.demo.modelo.dao.UserRepository;
import com.example.demo.payload.request.JwtResponse;
import com.example.demo.payload.request.LoginRequest;
import com.example.demo.payload.request.MessageResponse;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.security.JWT.JwtUtils;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		try {
			if (!userRepository.existsByUsername(loginRequest.getUsername())) {
				return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Tus datos no son correctos!"));
			}
	
		if (userRepository.existsByEmail(loginRequest.getPassword())) {
			return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: Tus datos no son correctos!"));
		}
		} catch (Exception e) {
			new MessageResponse("Error: Tus datos no son correctos!");
		}
		
		Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		List<String> roles = userDetails.getAuthorities().stream()
			.map(item -> item.getAuthority())
			.collect(Collectors.toList());
		return ResponseEntity.ok(new JwtResponse(jwt,
			userDetails.getId(),
			userDetails.getUsername(),
			userDetails.getEmail(),
			roles));
	}
	
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: Username ya existente!"));
		}
		
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
				.badRequest()
				.body(new MessageResponse("Error: Email ya existente!"));
		}
		
		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
				signUpRequest.getEmail(), 
				encoder.encode(signUpRequest.getPassword()));
		
		Set<String> strRoles = signUpRequest.getRole();
		
		Set<Role> roles = new HashSet<>();
		
		if (strRoles == null) {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
				roles.add(userRole);
			} else {
				strRoles.forEach(role -> {
				switch (role) {
					case "mod":
						Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
						.orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
						roles.add(modRole);
						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
						roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente!"));
	}

}
