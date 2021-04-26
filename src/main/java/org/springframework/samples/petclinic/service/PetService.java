package org.springframework.samples.petclinic.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.service.exceptions.DuplicatedPetNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PetService {

	private PetRepository petRepository;
	private VisitRepository visitRepository;
	private PetHotelService petHotelService;
	private AdoptionRequestService adoptionRequestService;

	@Autowired
	public PetService(PetRepository petRepository,
			VisitRepository visitRepository, PetHotelService petHotelService, AdoptionRequestService adoptionRequestService) {
		this.petRepository = petRepository;
		this.visitRepository = visitRepository;
		this.petHotelService = petHotelService;
		this.adoptionRequestService = adoptionRequestService;
	}
	
	@Transactional(readOnly = true)
	public List<Pet> findAll() throws DataAccessException {
		return (List<Pet>) petRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() throws DataAccessException {
		return petRepository.findPetTypes();
	}
	
	@Transactional
	public void saveVisit(Visit visit) throws DataAccessException {
		visitRepository.save(visit);
	}

	@Transactional(readOnly = true)
	public Pet findPetById(int id) throws DataAccessException {
		return petRepository.findById(id);
	}

	@Transactional(rollbackFor = DuplicatedPetNameException.class)
	public void savePet(Pet pet) throws DataAccessException, DuplicatedPetNameException {
			Pet otherPet=pet.getOwner().getPetwithIdDifferent(pet.getName(), pet.getId());
            if (StringUtils.hasLength(pet.getName()) &&  (otherPet!= null && !otherPet.getId().equals(pet.getId()))) {            	
            	throw new DuplicatedPetNameException();
            }else
                petRepository.save(pet);                
	}

	public Collection<Visit> findVisitsByPetId(int petId) {
		return visitRepository.findByPetId(petId);
	}
	
	@Transactional(readOnly = true)	
	public Optional<Pet> findById(Integer id) throws DataAccessException {
		return petRepository.findById(id);
	}
	
	@Transactional(readOnly = true)	
	public Optional<Visit> findVisitById(Integer id) throws DataAccessException {
		return visitRepository.findById(id);
	}
	
	public void delete(Pet pet) {
		for (Visit v : pet.getVisits()) {
			visitRepository.delete(v);
		}
		
		for (PetHotel p : pet.getHotel()) {
			if(p != null)
				petHotelService.delete(p);
		}
		
		for (AdoptionRequest a : pet.getAdoptionRequests()) {
			if(a != null)
				adoptionRequestService.delete(a);
		}
		
		
		petRepository.delete(pet);
	}
	
	public void deleteVisit(Visit visit) {
		visitRepository.delete(visit);
	}

}
