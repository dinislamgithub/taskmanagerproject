package com.din.cardinity.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.din.cardinity.model.AuthRequest;
import com.din.cardinity.model.User;
import com.din.cardinity.repository.UserRepository;
import com.din.cardinity.utils.JwtUtil;

@Service
public class UserService {
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	public String generateToken(AuthRequest authRequest) throws Exception {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		Optional<User> user = userRepository.findByUserName(authRequest.getUserName());

		if (!user.isEmpty()) {
			boolean isPasswordMatch = passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword());
			if (isPasswordMatch) {
				try {
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
							authRequest.getUserName(), user.get().getPassword()));
				} catch (Exception ex) {
					throw new Exception("inavalid username/password");
				}
				return jwtUtil.generateToken(authRequest.getUserName());
			}
		}
		return "inavalid username/password";
	}

	public String authenticateLoginUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

}
