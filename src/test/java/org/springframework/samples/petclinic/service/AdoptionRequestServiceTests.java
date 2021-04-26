package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class AdoptionRequestServiceTests { 
	
	@Autowired
	protected AdoptionRequestService adoptionRequestService;
	@Autowired
	protected AdoptionApplicationService adoptionApplicationService;
	@Autowired
	protected PetService petService;
	
	@Test
	void shouldFindAdoptionRequests() {
		Collection<AdoptionRequest> adoptionRequests = this.adoptionRequestService.findAdoptionRequests();
		
		assertThat(adoptionRequests).isNotEmpty();
		assertThat(adoptionRequests.size()).isPositive();
	}
	
	@Test
	void shouldFindParticularAdoptionRequest() {
		AdoptionRequest adoptionRequest = this.adoptionRequestService.findById(1).orElseGet(null);
		
		assertThat(adoptionRequest).isNotNull();
		assertThat(adoptionRequest.getId()).isEqualTo(1);
	}
	
	@Test
	void shouldFindPetsInAdoption() {
		Collection<Pet> pets = this.adoptionRequestService.findPetsInAdoption();
		
		assertThat(pets).isNotNull();
		assertThat(pets.size()).isPositive();
	}
	


	@Test
	void shouldDeleteAdoptionRequest() {
		AdoptionRequest aR = this.adoptionRequestService.findAdoptionRequestById(1).get();
		
		Collection<AdoptionRequest> adoptionRequests = this.adoptionRequestService.findAdoptionRequests();
		int numAdoptionRequests1 = adoptionRequests.size();
		
		Collection<AdoptionApplication> adoptionApplications = this.adoptionApplicationService.findAllAdoptionApplications();
		int numAdoptionApplications1 = adoptionApplications.size();
		
		this.adoptionRequestService.delete(aR);
		
		adoptionRequests = this.adoptionRequestService.findAdoptionRequests();
		int numAdoptionRequests2 = adoptionRequests.size();
		
		adoptionApplications = this.adoptionApplicationService.findAllAdoptionApplications();
		int numAdoptionApplications2 = adoptionApplications.size();
		
		assertThat(numAdoptionRequests2 + 1).isEqualTo(numAdoptionRequests1);
		assertThat(numAdoptionApplications2 + aR.getAdoptionApplications().size()).isEqualTo(numAdoptionApplications1);
	}
	
	@Test
	@Transactional
	void shouldSaveAdoptionRequest() {
		int totalRequests1 = this.adoptionRequestService.findAdoptionRequests().size();
		Pet p = this.petService.findPetById(1);
		
		AdoptionRequest aR = new AdoptionRequest();
		aR.setPet(p);
		
		this.adoptionRequestService.save(aR);
		
		p = this.petService.findPetById(1);
		int totalRequests2 = this.adoptionRequestService.findAdoptionRequests().size();
		
		assertThat(totalRequests2).isEqualTo(totalRequests1 + 1);
	}

}
