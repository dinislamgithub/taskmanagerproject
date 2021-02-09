package com.din.cardinity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.din.cardinity.model.AuthRequest;
import com.din.cardinity.service.UserService;

@RestController
public class AuthenticationController {

	@Autowired
	private UserService userService;

	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
		return userService.generateToken(authRequest);
	}

}
