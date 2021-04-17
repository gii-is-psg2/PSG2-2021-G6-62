package org.springframework.samples.petclinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.model.AdoptionApplication;


public interface AdoptionApplicationRepository extends CrudRepository<AdoptionApplication, Integer> {

	@Query("select a from AdoptionApplication a where a.adoptionRequest.pet.owner.user.username = ?1")
	public List<AdoptionApplication> getAdoptionApplicationByUserUsername(String username);
}