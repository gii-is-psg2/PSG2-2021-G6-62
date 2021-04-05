package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Service;
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
	void shouldFindSpecialties() {
		Collection<Specialty> specs = this.vetService.getAllSpecialties();
		assertThat(specs.size()).isNotEqualTo(0);
	}
	
	@Test
	void shouldFindCertainSpecialty() {
		Specialty spec = this.vetService.getAllSpecialties().stream().findFirst().get();
		
		Optional<Specialty> spec2 = this.vetService.findSpecialtyByName(spec.getName());
		assertThat(spec2.isPresent()).isTrue();
	}
	
	@Test
	void shouldNotFindCertainSpecialty() {
		Optional<Specialty> spec = this.vetService.findSpecialtyByName("Esta especialidad no existe");
		assertThat(spec.isPresent()).isFalse();
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
	@Transactional
	void shouldSaveVet() throws DataAccessException {
		List<Vet> allVets = (List<Vet>) this.vetService.findVets();

		Vet newVet = new Vet();
		newVet.setFirstName("testFirstName");
		newVet.setLastName("testLastName");

		this.vetService.save(newVet);
		List<Vet> allVetsAfterInsert = (List<Vet>) this.vetService.findVets();

		assertThat(allVetsAfterInsert.size()).isEqualTo(allVets.size() + 1);
	}
	
	@Test
	@Transactional
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
	
	@Test
	@Transactional
	void shouldDeleteVetExtended() throws DataAccessException {
		List<Vet> allVets = (List<Vet>) this.vetService.findVets();

		Vet newVet = new Vet();
		newVet.setFirstName("testFirstName");
		newVet.setLastName("testLastName");

		this.vetService.delete(newVet);
		
		List<Vet> allVetsAfterFakeDelete = (List<Vet>) this.vetService.findVets();

		assertThat(allVetsAfterFakeDelete.size()).isEqualTo(allVets.size());
	}
}
