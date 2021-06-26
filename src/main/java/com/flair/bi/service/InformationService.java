package com.flair.bi.service;

import com.flair.bi.domain.Information;
import com.flair.bi.repository.InformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InformationService {

	private final InformationRepository informationRepository;

	@Transactional
	public List<Information> getAll() {
		return informationRepository.findAll();
	}

	@Transactional
	public List<Information> getAll(Boolean isDesktop) {
		return informationRepository.findByIsDesktop(isDesktop);
	}
}
