package org.springframework.samples.petclinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.repository.AdoptionApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdoptionApplicationService {

	private AdoptionApplicationRepository adoptionApplicationRepository;

	@Autowired
	public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository) {
		this.adoptionApplicationRepository = adoptionApplicationRepository;
	}		

	@Transactional(readOnly = true)	
	public List<AdoptionApplication> findAllAdoptionApplications() throws DataAccessException {
		return (List<AdoptionApplication>) adoptionApplicationRepository.findAll();
	}
	
	@Transactional(readOnly = true)	
	public List<AdoptionApplication> findAdoptionApplications(String username) throws DataAccessException {
		return (List<AdoptionApplication>) adoptionApplicationRepository.getAdoptionApplicationByUserUsername(username);
	}
	
	@Transactional(readOnly = true)	
	public Optional<AdoptionApplication> findById(Integer id) throws DataAccessException {
		return adoptionApplicationRepository.findById(id);
	}
	
	@Transactional
	public void delete(AdoptionApplication adoptionApplication) {
		adoptionApplicationRepository.delete(adoptionApplication);
	}
	
	@Transactional
	public void save(AdoptionApplication adoptionApplication) throws DataAccessException {
		adoptionApplicationRepository.save(adoptionApplication);
	}
}
