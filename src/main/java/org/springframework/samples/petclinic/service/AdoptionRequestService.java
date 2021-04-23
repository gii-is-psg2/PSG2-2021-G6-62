package org.springframework.samples.petclinic.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.AdoptionRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdoptionRequestService {

	private AdoptionRequestRepository adoptionRequestRepository;
	private AdoptionApplicationService adoptionApplicationService;

	@Autowired
	public AdoptionRequestService(AdoptionRequestRepository adoptionRequestRepository,
			AdoptionApplicationService adoptionApplicationService) {
		this.adoptionRequestRepository = adoptionRequestRepository;
		this.adoptionApplicationService = adoptionApplicationService;
	}		

	@Transactional(readOnly = true)	
	public List<AdoptionRequest> findAdoptionRequests() throws DataAccessException {
		return (List<AdoptionRequest>) adoptionRequestRepository.findAll();
	}
	
	@Transactional(readOnly = true)	
	public Optional<AdoptionRequest> findById(Integer id) throws DataAccessException {
		return adoptionRequestRepository.findById(id);
	}
	
	@Transactional
	public void delete(AdoptionRequest adoptionRequest) {
		for (AdoptionApplication a : adoptionRequest.getAdoptionApplications()) {
			adoptionApplicationService.delete(a);
		}
		adoptionRequestRepository.delete(adoptionRequest);
	}
	
	@Transactional
	public void save(AdoptionRequest adoptionRequest) throws DataAccessException {
		adoptionRequestRepository.save(adoptionRequest);
	}

	public Optional<AdoptionRequest> findAdoptionRequestById(Integer id) {
		return adoptionRequestRepository.findById(id);
	}
	
	public List<Pet> findPetsInAdoption() {
		return this.adoptionRequestRepository.findPetsInAdoption();
	}
}
