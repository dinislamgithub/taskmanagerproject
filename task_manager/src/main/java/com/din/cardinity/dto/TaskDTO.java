package com.din.cardinity.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
	private Long taskId;
	private String taskDescription;
	private int taskStatus;
	private Date createDate;
	private Date updateDate;
	private Date dueDate;
	private ProjectDTO projectDTO;
}
