package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Pet;


public interface AdoptionRequestRepository extends CrudRepository<AdoptionRequest, Integer> {

	@Query("SELECT ar.pet FROM AdoptionRequest ar")
	List<Pet> findPetsInAdoption();
}
