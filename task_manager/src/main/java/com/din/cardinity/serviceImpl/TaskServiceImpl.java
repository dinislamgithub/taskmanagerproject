package com.din.cardinity.serviceImpl;

import java.util.List;

import com.din.cardinity.dto.TaskDTO;
import com.din.cardinity.model.TaskEntity;

public interface TaskServiceImpl {
	public TaskEntity createTask(TaskDTO taskDTO, String userName);

	public TaskEntity modifiedTask(String userName, TaskDTO taskDTO);

	public List<TaskEntity> getTasks(String userName);

	public List<TaskEntity> getAllTaskByProject(String userName, TaskDTO taskDTO);

	public List<TaskEntity> getAllExpaireTask(String userName);

	public List<TaskEntity> getTaskByStatus(String userName, TaskDTO taskDTO);

	public List<TaskEntity> getAllUserTaskByAdmin(String userName);

}
