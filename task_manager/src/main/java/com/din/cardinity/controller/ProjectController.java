package com.din.cardinity.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.din.cardinity.dto.ProjectDTO;
import com.din.cardinity.model.ProjectEntity;
import com.din.cardinity.service.ProjectService;
import com.din.cardinity.service.UserService;

@RestController
@RequestMapping("/api")
public class ProjectController {
	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ApiResponse apiResponse; 
	
	@RequestMapping(value = "/v1/create_project", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ApiResponse> createAdminAndUserProject(@Valid @RequestBody ProjectDTO projectDTO) {
		ProjectEntity projectEntityResponse = new ProjectEntity();
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (projectDTO != null && (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user"))) {
			projectEntityResponse = projectService.createAdminAndUserProject(projectDTO, loginAuthenticUserName);

			if (projectEntityResponse.getErrorMsg() == null || projectEntityResponse.getErrorMsg().isEmpty()) {
				apiResponse.setResponseObject(projectEntityResponse);
				apiResponse.setStatusCode(HttpStatus.CREATED);
				return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.CREATED);
			} else {
				apiResponse.setErrorMessage(projectEntityResponse.getErrorMsg());
				apiResponse.setStatusCode(HttpStatus.FORBIDDEN);
				return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.FORBIDDEN);
			}
		}
		return null;
	} 
	
	@RequestMapping(value = "/v1/all_project", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ApiResponse> getAllAdminAndUserProjects() {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
			apiResponse.setResponseList(projectService.getAllAdminAndUserProjects(loginAuthenticUserName));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	} 
	
	@RequestMapping(value = "/v1/user_allproject_by_admin", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<ApiResponse> getUserProjectsByAdmin() {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin")) {
			apiResponse.setResponseList(projectService.getUserProjectsByAdmin(loginAuthenticUserName));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/delete_project", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteAdminAndUserProject(@RequestBody ProjectDTO projectDTO) {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();
		String msg = "";
		if (projectDTO.getProjectId() != null) {
			if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
				msg = projectService.deleteAdminAndUserProject(loginAuthenticUserName, projectDTO.getProjectId());
			}
		}
		return new ResponseEntity<String>(msg, HttpStatus.OK);
	}

}
