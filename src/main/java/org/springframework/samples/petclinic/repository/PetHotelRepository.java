package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.PetHotel;

public interface PetHotelRepository extends CrudRepository<PetHotel, Integer> {
	
	@Query("SELECT h FROM PetHotel h")
	List<PetHotel> encontrarReservas() throws DataAccessException;

}
