package com.flair.bi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flair.bi.domain.Information;

public interface InformationRepository extends JpaRepository<Information, Long> {

	@Query(value = "SELECT * FROM information WHERE is_desktop = ?1", nativeQuery = true)
	List<Information> findByIsDesktop(Boolean isDesktop);
}
