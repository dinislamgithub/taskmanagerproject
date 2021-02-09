package com.din.cardinity.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.din.cardinity.model.ProjectEntity;

import lombok.Data;

@Data
@Component
public class ApiResponse {
	
 List<ProjectEntity> responseList = new ArrayList<>();
 ProjectEntity responseObject = new ProjectEntity();
 private List<String> errorMessage; 
 private HttpStatus statusCode;
 
}
