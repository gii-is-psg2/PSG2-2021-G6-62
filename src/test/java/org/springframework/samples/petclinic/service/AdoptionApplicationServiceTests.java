package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class AdoptionApplicationServiceTests { 
	
	@Autowired
	protected AdoptionRequestService adoptionRequestService;
	@Autowired
	protected AdoptionApplicationService adoptionApplicationService;
	@Autowired
	protected PetService petService;
	@Autowired
	protected OwnerService ownerService;
	
	@Test
	void shouldFindAdoptionApplications() {
		Collection<AdoptionApplication> adoptionApplications = this.adoptionApplicationService.findAllAdoptionApplications();
		
		assertThat(adoptionApplications).isNotEmpty();
		assertThat(adoptionApplications.size()).isPositive();
	}
	
	@Test
	void shouldFindAdoptionApplicationByApplicantName() {
		Collection<AdoptionApplication> adoptionApplications = this.adoptionApplicationService.findAdoptionApplications("owner1");
		
		assertThat(adoptionApplications).isNotEmpty();
		assertThat(adoptionApplications.size()).isPositive();
	}
	
	@Test
	void shouldFindAdoptionApplicationById() {
		AdoptionApplication adoptionApplication = this.adoptionApplicationService.findById(1).orElse(null);
		
		assertThat(adoptionApplication).isNotNull();
		assertThat(adoptionApplication.getId()).isEqualTo(1);
	}
	

	@Test
	void shouldDeleteAdoptionRequest() {
		AdoptionApplication aA = this.adoptionApplicationService.findById(1).get();
		
		Collection<AdoptionApplication> adoptionApplications = this.adoptionApplicationService.findAllAdoptionApplications();
		int numAdoptionApplications1 = adoptionApplications.size();
		
		
		this.adoptionApplicationService.delete(aA);
		
		adoptionApplications = this.adoptionApplicationService.findAllAdoptionApplications();
		int numAdoptionApplications2 = adoptionApplications.size();
		
		assertThat(numAdoptionApplications2 + 1).isEqualTo(numAdoptionApplications1);
	}
	
	@Test
	@Transactional
	void shouldSaveAdoptionApplication() {
		int totalApplications1 = this.adoptionApplicationService.findAllAdoptionApplications().size();
		Owner o = this.ownerService.findOwnerById(1);
		AdoptionRequest aR = this.adoptionRequestService.findAdoptionRequestById(1).get();
		
		AdoptionApplication aA = new AdoptionApplication();
		aA.setOwner(o);
		aA.setDescription("aaaaa");
		aA.setAdoptionRequest(aR);
		
		this.adoptionApplicationService.save(aA);
		
		int totalApplications2 = this.adoptionApplicationService.findAllAdoptionApplications().size();
		
		assertThat(totalApplications2).isEqualTo(totalApplications1 + 1);
	}

}
