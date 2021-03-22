package org.springframework.samples.petclinic.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.repository.PetHotelRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.service.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.service.exceptions.WrongDatesInHotelsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetHotelService {
	
	private PetHotelRepository petHotelRepository;
	
	private PetRepository petRepository;
	
	@Autowired
	public PetHotelService(PetHotelRepository petHotelRepository, PetRepository petRepository) {
		this.petHotelRepository = petHotelRepository;
		this.petRepository = petRepository;
	}	
	
	
	@Transactional(readOnly = true)
	public PetHotel findHotelById(int id) throws DataAccessException {
		return petHotelRepository.findById(id).get();
	}
	
	@Transactional(rollbackFor = WrongDatesInHotelsException.class)
	public void saveHotel(PetHotel petHotel) throws DataAccessException, WrongDatesInHotelsException {
		if(petHotel.getStartDate().isAfter(petHotel.getEndDate())) {
			throw new WrongDatesInHotelsException();
		}else {
			petHotelRepository.save(petHotel);
		}
	}
	
	@Transactional 
	public List<PetHotel> bookingsOfPersonsWithUserName(String name)throws DataAccessException {
		List<PetHotel> reservas=petHotelRepository.encontrarReservas();
		List<PetHotel> res= new ArrayList<PetHotel>();
		for(PetHotel p: reservas) {
			if(p.getUserName().equals(name)) {
				res.add(p);
			}
		}
		return res;
		
	}
	

	@Transactional
	public Collection<Pet> findPets() throws DataAccessException {
		return petRepository.findPets();
	}
	
}
