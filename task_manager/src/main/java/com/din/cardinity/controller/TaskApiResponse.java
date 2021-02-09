package com.din.cardinity.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.din.cardinity.model.TaskEntity;

import lombok.Data;

@Data
@Component
public class TaskApiResponse {
	 List<TaskEntity> responseList = new ArrayList<>();
	 TaskEntity responseObject = new TaskEntity();
	 private List<String> errorMessage; 
	 private HttpStatus statusCode;
}
