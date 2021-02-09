package com.din.cardinity.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.din.cardinity.model.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE task_manager.task t SET t.task_description = :task_description, t.due_date = :due_date, t.task_status = :task_status, t.update_date = :update_date WHERE t.task_id = :task_id", nativeQuery = true)
	void updateTaskEntity(@Param("task_id") Long task_id, @Param("task_description") String taskDescription,
			@Param("due_date") Date dueDate, @Param("task_status") int taskStatus,
			@Param("update_date") Date updateDate);

}
