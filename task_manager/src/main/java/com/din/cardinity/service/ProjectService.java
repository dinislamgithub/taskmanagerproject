package com.din.cardinity.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.din.cardinity.controller.ApiResponse;
import com.din.cardinity.dto.ProjectDTO;
import com.din.cardinity.model.ProjectEntity;
import com.din.cardinity.model.User;
import com.din.cardinity.repository.ProjectRepository;
import com.din.cardinity.repository.UserRepository;
import com.din.cardinity.serviceImpl.ProjectServiceImpl;

 

@Service
@Transactional
public class ProjectService implements ProjectServiceImpl { 

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired 
	private ApiResponse apiResponse;

	ProjectEntity projectEntity = new ProjectEntity();
	ProjectDTO projectDTO = new ProjectDTO();

	@Override
	public ProjectEntity createAdminAndUserProject(ProjectDTO projectDTO, String userName) {
		User user = userRepository.findByUserName(userName).get();
		final boolean uniqueProject =getUniqueProject(projectDTO); //
		
		ProjectEntity projectEntity = new ProjectEntity();
		if (projectDTO != null && userName != null) {
			projectEntity.setCreateDate(new Date());
			projectEntity.setDueDate(projectDTO.getDueDate());
			projectEntity.setUpdateDate(null);			
			projectEntity.setProjectName(projectDTO.getProjectName());
			projectEntity.setUser(user);
			
			if(uniqueProject) {
				List<String> list = requiredFieldValidate(projectEntity);
				if(list.isEmpty()) {
					return projectRepository.save(projectEntity);
				}else {
					projectEntity.setErrorMsg(list);
				}
			}else {
				projectEntity.setErrorMsg(List.of("Project name is exist. try another name to create project."));
			}
			
		}
		return projectEntity;
	}

	public boolean getUniqueProject(ProjectDTO projectDTO) {
		List<ProjectEntity> projectList = projectRepository.findAll();
		if(!projectList.isEmpty()) {
			for(ProjectEntity t : projectList) {
				if(t.getProjectName().matches(projectDTO.getProjectName())) {				
					return false;
				}
			}
		}
		return true;
	}
	
	public List<String> requiredFieldValidate(ProjectEntity projectEntity) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<ProjectEntity>> violations = validator.validate(projectEntity);

		List<String> validationList = new ArrayList<>();
		for (ConstraintViolation<ProjectEntity> violation : violations) {
			validationList = List.of(violation.getMessage());
		}
		return validationList;
	}
	
	@Override
	public List<ProjectEntity> getAllAdminAndUserProjects(String userName) {
		List<ProjectEntity> projectList = new ArrayList<ProjectEntity>();
		User user = userRepository.findByUserName(userName).get(); 
		if (userName != null) {
			return projectList = projectRepository.findAllByUserId(user.getId());
		}
		return projectList;
	}

	@Override
	public List<ProjectEntity> getUserProjectsByAdmin(String userName) {
		List<User> userList = userRepository.findAll();
		List<User> userResponseList = new ArrayList<>();
		if (userList != null && userList.size() > 0) {
			for (User u : userList) {
				if (u.getUserName().equals("user")) {
					userResponseList.add(u);
				}
			}
		}
		List<ProjectEntity> projectList = projectRepository.findAll();
		List<ProjectEntity> projectResponseList = new ArrayList<>();
		for (ProjectEntity pj : projectList) {
			for (User u : userResponseList) {
				if (u.getId() == pj.getUser().getId()) {
					projectResponseList.add(pj);
				}
			}			
		}
		if(projectResponseList.isEmpty()) {
			apiResponse.setErrorMessage(List.of("User Role_Based Project doesn't create yet."));
		}
		
		return projectResponseList;
	}

	@Override
	public String deleteAdminAndUserProject(String userName, Long projectId) {
		Optional<User> user = userRepository.findByUserName(userName);
		List<ProjectEntity> projectResponseList = projectRepository.findAllByUserId(user.get().getId());
		for (ProjectEntity pj : projectResponseList) {
			if (projectId != null && pj.getId() == projectId) {
				projectRepository.delete(pj); 
				return "Successfully Project Deleted.";
			}
		}
		return "Project Id Not Found.";
	}

}
