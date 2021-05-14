package org.springframework.samples.petclinic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetHotelRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetHotelService {
	
	private PetHotelRepository petHotelRepository;
	
	private PetRepository petRepository;
	
	private OwnerRepository ownerRepository;
	
	@Autowired
	public PetHotelService(PetHotelRepository petHotelRepository, PetRepository petRepository, OwnerRepository ownerRepository) {
		this.petHotelRepository = petHotelRepository;
		this.petRepository = petRepository;
		this.ownerRepository= ownerRepository;
	}	
	
	
	@Transactional(readOnly = true)
	public PetHotel findHotelById(int id) throws DataAccessException {
		return petHotelRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public void saveHotel(PetHotel petHotel) {
		petHotelRepository.save(petHotel);
	}
	
	@Transactional 
	public List<PetHotel> bookingsOfPersonsWithUserName(String name)throws DataAccessException {
		List<PetHotel> reservas = petHotelRepository.encontrarReservas();
		List<PetHotel> res = new ArrayList<>();
		for(PetHotel p: reservas) {
			if (p.getUserName().equals(name)) {
				res.add(p);
			}
		}
		return res;
	}
	

	@Transactional
	public List<Pet> findPetsByUser(String username) throws DataAccessException {
		return petRepository.findPetsByUser(username);
	}
	
	@Transactional
	public List<PetHotel> findAllBookings() throws DataAccessException {
		return (List<PetHotel>) petHotelRepository.findAll();
	}
	
	@Transactional
	public List<Pet> findAllPets() throws DataAccessException {
		return (List<Pet>) petRepository.findAll();
	}
	
	@Transactional
	public List<Owner> findAllOwners() throws DataAccessException {
		return (List<Owner>) ownerRepository.findAll();
	}
	
	@Transactional
	public List<PetHotel> findPetHotelByPetId(Integer petId) throws DataAccessException {
		return petHotelRepository.findPetHotelByPetId(petId);
	}

	public void delete(PetHotel petHotel) {
		petHotelRepository.delete(petHotel);
	}
	
	
}
