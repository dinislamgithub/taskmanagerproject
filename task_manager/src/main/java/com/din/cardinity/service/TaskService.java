package com.din.cardinity.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.din.cardinity.controller.ApiResponse;
import com.din.cardinity.controller.TaskApiResponse;
import com.din.cardinity.dto.TaskDTO;
import com.din.cardinity.model.ProjectEntity;
import com.din.cardinity.model.TaskEntity;
import com.din.cardinity.model.User;
import com.din.cardinity.repository.ProjectRepository;
import com.din.cardinity.repository.TaskRepository;
import com.din.cardinity.repository.UserRepository;
import com.din.cardinity.serviceImpl.TaskServiceImpl;
import com.din.cardinity.utils.DateUtility; 

import ch.qos.logback.core.boolex.Matcher;

@Service
@Transactional
public class TaskService implements TaskServiceImpl {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private DateUtility dateUtility;

	@Autowired
	private TaskApiResponse apiResponse;

	@Override
	public TaskEntity createTask(TaskDTO taskDTO, String userName) {
		Optional<User> userModel = Optional.ofNullable(userRepository.findByUserName(userName)).orElse(null);
		List<ProjectEntity> projEntityList = new ArrayList<ProjectEntity>();
		ProjectEntity projectEntity = null;
		TaskEntity taskEntity = new TaskEntity();
		
		if(!userModel.isEmpty()) {
			User user = userModel.get();
			projEntityList = projectRepository.findAllByUserId(user.getId());
			
			
			final String projectName = taskDTO.getProjectDTO().getProjectName();			
			for(ProjectEntity pj : projEntityList) {				
				if (projectName != null && pj !=null && pj.getProjectName().matches(projectName)) {
					projectEntity = projectRepository.findByProjectName(projectName);
				}else {
					taskEntity.setErrorMsgs(List.of("Project name doesn't matched."));
				}				
			}			
			
		}
		
		if (taskDTO != null && projectEntity != null) {
			taskEntity = new TaskEntity();
			taskEntity.setCreateDate(new Date());
			taskEntity.setDueDate(taskDTO.getDueDate());
			taskEntity.setTaskDescription(taskDTO.getTaskDescription());
			taskEntity.setTaskDuration(dateUtility.taskDurationDay(taskDTO.getCreateDate(), taskDTO.getDueDate()));
			taskEntity.setTaskStatus(taskDTO.getTaskStatus());
			taskEntity.setUpdateDate(null);
			taskEntity.setProjectEntity(projectEntity);

			List<String> list = requiredFieldValidate(taskEntity);
			if(list.isEmpty()) {
				return taskRepository.save(taskEntity);
			}else {
				taskEntity.setErrorMsgs(list);
			}
			
		} else {
			taskEntity.setErrorMsgs(List.of("You must create a project first."));
		}		
		return taskEntity;
	}

	public List<String> requiredFieldValidate(TaskEntity taskEntity) { //
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<TaskEntity>> violations = validator.validate(taskEntity);

		List<String> validationList = new ArrayList<>();
		for (ConstraintViolation<TaskEntity> violation : violations) {
			validationList = List.of(violation.getMessage());
		}
		return validationList;
	}
	
	public boolean isTaskDtoDataValidate(TaskDTO taskDTO) {
		String statusId = String.valueOf(taskDTO.getTaskStatus());		
		if(taskDTO.getTaskDescription() != null && !taskDTO.getTaskDescription().isEmpty()
				&& (taskDTO.getTaskStatus() >0 && !statusId.isEmpty())) {
			return true;
		}
		return false;
	}
	
	@Override
	public TaskEntity modifiedTask(String userName, TaskDTO taskDTO) {
		Optional<TaskEntity> task = Optional.ofNullable(taskRepository.findById(taskDTO.getTaskId()).orElse(null));
		TaskEntity taskEntityRequest = new TaskEntity();
		if(!task.isEmpty()) {
			TaskEntity taskEntity = task.get();
			ProjectEntity projectEntity = projectRepository.findById(taskEntity.getProjectEntity().getId()).get();
			User user = userRepository.findById(projectEntity.getUser().getId()).get();			

			if (taskEntity.getTaskStatus() != 0) {
				if (taskDTO.getTaskId() != null && taskDTO.getTaskId()>0) {
					if (user.getUserName().equals(userName)
							&& projectEntity.getId() == taskEntity.getProjectEntity().getId()) {
						
						boolean checkValue = isTaskDtoDataValidate(taskDTO);
						if(checkValue) {
							taskRepository.updateTaskEntity(taskDTO.getTaskId(), taskDTO.getTaskDescription(),
									taskDTO.getDueDate(), taskDTO.getTaskStatus(), new Date());
							return taskEntityRequest = taskRepository.findById(taskDTO.getTaskId()).get();
						}else {
							 taskEntityRequest.setErrorMsgs(List.of("Task description and taskStatus cann't be null/empty."));
						} 
					}
				}else {
					taskEntityRequest.setErrorMsgs(List.of("Task Id is missing to update the record."));
				}
			} else {
				taskEntityRequest.setErrorMsg("Closed task cann't be edited.");
			}
		}else { 
			taskEntityRequest.setErrorMsg("There is no data in db."); 
		}
		return taskEntityRequest;
	}
	
	@Override
	public List<TaskEntity> getTasks(String userName) {
		List<TaskEntity> taskEntityResponseList = new ArrayList<TaskEntity>();
		if (userName != null) {
			User user = userRepository.findByUserName(userName).get();
			List<ProjectEntity> projectList = projectRepository.findAllByUserId(user.getId());
			List<TaskEntity> taskList = taskRepository.findAll();			

			if (!projectList.isEmpty() && projectList.size() > 0) {
				for (ProjectEntity pj : projectList) {
					for (TaskEntity t : taskList) {
						if (t.getProjectEntity().getId() == pj.getId()) {
							taskEntityResponseList.add(t);
						}
					}
				}
			}else {
				TaskEntity t = new TaskEntity();
				t.setErrorMsg("Data is empty.");
				taskEntityResponseList.add(t);
			}
		}
		return taskEntityResponseList; 
	}

	@Override
	public List<TaskEntity> getAllTaskByProject(String userName, TaskDTO taskDTO) {
		List<TaskEntity> taskEntityResponseList = new ArrayList<TaskEntity>();
		TaskEntity t = new TaskEntity();
		if (userName != null && taskDTO.getProjectDTO().getProjectId() != null) {
			User user = userRepository.findByUserName(userName).get();
			List<ProjectEntity> projectList = projectRepository.findAllByUserId(user.getId());
			List<TaskEntity> taskList = taskRepository.findAll();

			if (!projectList.isEmpty() && projectList.size() > 0) {
				for (ProjectEntity pj : projectList) {
					for (TaskEntity task : taskList) {
						if ((task.getProjectEntity().getId() == taskDTO.getProjectDTO().getProjectId())
								&& (task.getProjectEntity().getId() == pj.getId())) {
							taskEntityResponseList.add(task);
						} else {
							t = new TaskEntity();
							t.setErrorMsg("Project Id is not matched.");
						}
					}
				}
			}else {
				t.setErrorMsg("You must create a project first.");
				taskEntityResponseList.add(t);
			}

		} else {
			t.setErrorMsg("Project Id is not found.");
			taskEntityResponseList.add(t);
		}
		return taskEntityResponseList;
	}

	@Override
	public List<TaskEntity> getAllExpaireTask(String userName) {
		List<TaskEntity> taskEntityResponseList = new ArrayList<TaskEntity>();
		if (userName != null) {
			User user = userRepository.findByUserName(userName).get();
			List<ProjectEntity> projectList = projectRepository.findAllByUserId(user.getId());
			List<TaskEntity> taskList = taskRepository.findAll();			

			if (!projectList.isEmpty() && projectList.size() > 0) {
				for (ProjectEntity pj : projectList) {
					for (TaskEntity task : taskList) {
						if (task.getProjectEntity().getId() == pj.getId()) {
							try {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(task.getDueDate());

								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date currenDate = df.parse(df.format(new Date()));
								Date dueDate = df.parse(df.format(calendar.getTime()));

								if (dueDate.compareTo(currenDate) < 0) {
									taskEntityResponseList.add(task);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
				return taskEntityResponseList;
			}else {
				TaskEntity t = new TaskEntity();
				t.setErrorMsg("Project expired task data is empty.");
				taskEntityResponseList.add(t);
			}
		}
		return taskEntityResponseList; 
	}

	@Override
	public List<TaskEntity> getTaskByStatus(String userName, TaskDTO taskDTO) {
		List<TaskEntity> taskEntityResponseList = new ArrayList<TaskEntity>();
		if (userName != null && taskDTO != null) {
			User user = userRepository.findByUserName(userName).get();
			List<ProjectEntity> projectList = projectRepository.findAllByUserId(user.getId());
			List<TaskEntity> taskList = taskRepository.findAll();			

			if (!projectList.isEmpty() && projectList.size() > 0) {
				for (ProjectEntity pj : projectList) {
					for (TaskEntity task : taskList) {
						if ((task.getProjectEntity().getId() == pj.getId())
								&& task.getTaskStatus() == taskDTO.getTaskStatus()) {
							taskEntityResponseList.add(task);
						}
					}
				}
			}else {
				TaskEntity t = new TaskEntity();
				t.setErrorMsg("Request doesn't fetche anything from db.");
				taskEntityResponseList.add(t);
			}
		}
		return taskEntityResponseList;
	}

	@Override
	public List<TaskEntity> getAllUserTaskByAdmin(String userName) {
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
		
		List<TaskEntity> taskEntityResposeList = new ArrayList<TaskEntity>();
		List<TaskEntity> taskList = taskRepository.findAll();
		if(taskList.size() >= 0 && !taskList.isEmpty()) {
			for(ProjectEntity p : projectResponseList) {
				for(TaskEntity t : taskList) {
					if(t.getProjectEntity().getId() == p.getId()) {
						taskEntityResposeList.add(t);
					}
				}				
			}
			if(taskEntityResposeList.isEmpty()) {
				apiResponse.setErrorMessage(List.of("User Role_Based task is empty."));
			}
			return taskEntityResposeList;
		}
		return null;
	}


}
