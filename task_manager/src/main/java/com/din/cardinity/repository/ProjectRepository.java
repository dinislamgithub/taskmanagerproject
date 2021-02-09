package com.din.cardinity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.din.cardinity.model.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
	@Query(value = "SELECT * FROM task_manager.project pj WHERE pj.user_id = :userId", nativeQuery = true)
	List<ProjectEntity> findAllByUserId(@Param("userId") int userId);

	ProjectEntity findByProjectName(String projectName);
	
	@Query(value = "SELECT * FROM task_manager.project pj WHERE pj.user_id = :userId", nativeQuery = true)
	ProjectEntity findByUserId(@Param("userId") int userId);
}
