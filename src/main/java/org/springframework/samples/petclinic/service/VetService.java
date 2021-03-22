package org.springframework.samples.petclinic.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VetService {

	private VetRepository vetRepository;

	@Autowired
	public VetService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}		

	@Transactional(readOnly = true)	
	public Collection<Vet> findVets() throws DataAccessException {
		return vetRepository.findAll();
	}
	
	@Transactional(readOnly = true)	
	public Optional<Vet> findById(Integer id) throws DataAccessException {
		return vetRepository.findById(id);
	}
	
	public void delete(Vet vet) {
		vetRepository.delete(vet);
	}
	
	@Transactional
	public void save(Vet vet) throws DataAccessException {
		vetRepository.save(vet);
	}
	
	@Transactional
	public List<Specialty> getAllSpecialties() {
		return vetRepository.findAllSpecialties();
	}
	
	@Transactional
	public Optional<Specialty> findSpecialtyByName(String name) {
		return vetRepository.findSpecialtyByName(name);
	}
}
