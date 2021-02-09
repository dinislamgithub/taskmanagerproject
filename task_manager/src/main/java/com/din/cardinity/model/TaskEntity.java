package com.din.cardinity.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class TaskEntity implements Serializable {
	private static final long serialVersionUID = 4910225916550731448L;

	@Id
	@Column(name = "task_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "Task description cann't be null.")
//	@NotEmpty(message = "Task description cann't be empty.") 
	private String taskDescription;
	
	@NotNull(message = "Task status cann't be null.")
	//@NotEmpty(message = "Task status cann't be empty.")
	private int taskStatus;

	private Date createDate;
	private Date updateDate;
	private Date dueDate;
	private String taskDuration;

	@NotNull(message = "Project Object cann't be null.")
	//@NotEmpty(message = "Project Object cann't be empty.")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id", nullable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private ProjectEntity projectEntity;

	@Transient
	private String errorMsg;
	
	@Transient
	private List<String> errorMsgs;
}
