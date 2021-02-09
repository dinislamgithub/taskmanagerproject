package com.din.cardinity.serviceImpl;

import java.util.List;

import com.din.cardinity.dto.ProjectDTO;
import com.din.cardinity.model.ProjectEntity;

public interface ProjectServiceImpl {
	public ProjectEntity createAdminAndUserProject(ProjectDTO projectDTO, String userName);

	public List<ProjectEntity> getAllAdminAndUserProjects(String userName);

	public List<ProjectEntity> getUserProjectsByAdmin(String userName);

	public String deleteAdminAndUserProject(String userName, Long projectId);

}
