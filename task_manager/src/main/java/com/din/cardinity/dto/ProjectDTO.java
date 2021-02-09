package com.din.cardinity.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
	private Long projectId;
	private String projectName;
	private Date createDate;
	private Date updateDate;
	private Date dueDate;
	private UserDTO userDTO;

}
