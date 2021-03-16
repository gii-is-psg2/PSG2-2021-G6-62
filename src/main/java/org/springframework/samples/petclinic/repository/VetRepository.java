package org.springframework.samples.petclinic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;

public interface VetRepository extends CrudRepository<Vet, Integer>{

	List<Vet> findAll() throws DataAccessException;

	@Query("SELECT s FROM Specialty s")
	List<Specialty> findAllSpecialties();
	
	@Query("SELECT s FROM Specialty s WHERE s.name = :name")
	Optional<Specialty> findSpecialtyByName(@Param("name") String name);

}
