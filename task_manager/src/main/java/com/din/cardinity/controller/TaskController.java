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

import com.din.cardinity.dto.TaskDTO;
import com.din.cardinity.model.TaskEntity;
import com.din.cardinity.service.TaskService;
import com.din.cardinity.service.UserService;

@RestController
@RequestMapping("/api")
public class TaskController {

	@Autowired
	private UserService userService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskApiResponse apiResponse;

	public TaskDTO taskDTO;

	@RequestMapping(value = "/v1/create_task", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> createTask(@Valid @RequestBody TaskDTO taskDTO) {
		TaskEntity taskEntityResponse = new TaskEntity();
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (taskDTO != null && (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user"))) {
			taskEntityResponse = taskService.createTask(taskDTO, loginAuthenticUserName);

			if (taskEntityResponse.getErrorMsgs() == null || taskEntityResponse.getErrorMsgs().isEmpty()) {
				apiResponse.setResponseObject(taskEntityResponse);
				apiResponse.setStatusCode(HttpStatus.CREATED);
				return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.CREATED);
			} else {
				apiResponse.setErrorMessage(taskEntityResponse.getErrorMsgs());
				apiResponse.setStatusCode(HttpStatus.FORBIDDEN);
				return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.FORBIDDEN);
			}

		}
		return null;
	}

	@RequestMapping(value = "/v1/modified_task", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> modifiedTask(@Valid @RequestBody TaskDTO taskDTO) {
		TaskEntity taskEntityResponse = new TaskEntity();
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (taskDTO != null && (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user"))) {
			taskEntityResponse = taskService.modifiedTask(loginAuthenticUserName, taskDTO);
			apiResponse.setResponseObject(taskEntityResponse);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/v1/get_task", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> getAminAndUserTask() {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
			apiResponse.setResponseList(taskService.getTasks(loginAuthenticUserName));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/get_alltask_by_project", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> getAllTaskByProject(@RequestBody TaskDTO taskDTO) {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
			apiResponse.setResponseList(taskService.getAllTaskByProject(loginAuthenticUserName, taskDTO));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/get_exipred_task", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> getExpiredTask() {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
			apiResponse.setResponseList(taskService.getAllExpaireTask(loginAuthenticUserName));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/get_task_by_status", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> getTaskByStatus(@RequestBody TaskDTO taskDTO) {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin") || loginAuthenticUserName.equals("user")) {
			apiResponse.setResponseList(taskService.getTaskByStatus(loginAuthenticUserName, taskDTO));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/get_alluser_task", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<TaskApiResponse> getAllUserTaskByAdmin() {
		final String loginAuthenticUserName = userService.authenticateLoginUserName();

		if (loginAuthenticUserName.equals("admin")) {
			apiResponse.setResponseList(taskService.getAllUserTaskByAdmin(loginAuthenticUserName));
			apiResponse.setStatusCode(HttpStatus.OK);
		}
		return new ResponseEntity<TaskApiResponse>(apiResponse, HttpStatus.OK);
	}
}
