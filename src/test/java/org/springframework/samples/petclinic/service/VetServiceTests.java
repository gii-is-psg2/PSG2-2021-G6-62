package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.model.Authorities;
import org.springframework.samples.petclinic.service.exceptions.DuplicatedPetNameException;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class VetServiceTests {

	@Autowired
	protected VetService vetService;	

	@Test
	void shouldFindVets() {
		Collection<Vet> vets = this.vetService.findVets();

		Vet vet = EntityUtils.getById(vets, Vet.class, 3);
		assertThat(vet.getLastName()).isEqualTo("Douglas");
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}
	
	@Test
	void shouldFindVetById() {
		Optional<Vet> vet = this.vetService.findById(1);
		assertThat(vet.isPresent()).isTrue();
	}
	
	@Test
	void shouldNotFindVetById() {
		Optional<Vet> vetFail = this.vetService.findById(9999);
		assertThat(vetFail.isPresent()).isFalse();
	}

	@Test
	void shouldDeleteVet() throws DataAccessException {
		List<Vet> allVets = (List<Vet>) this.vetService.findVets();

		Vet newVet = new Vet();
		newVet.setFirstName("testFirstName");
		newVet.setLastName("testLastName");

		this.vetService.save(newVet);
		this.vetService.delete(newVet);
		List<Vet> allVetsAfterInsertAndDelete = (List<Vet>) this.vetService.findVets();

		assertThat(allVetsAfterInsertAndDelete.size()).isEqualTo(allVets.size());
	}
}
