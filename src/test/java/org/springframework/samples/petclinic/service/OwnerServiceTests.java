package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.samples.petclinic.model.Authorities;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class OwnerServiceTests { 
	
	@Autowired
	protected OwnerService ownerService;
	@Autowired
	protected UserService userService;
	@Autowired
	protected PetService petService;
	
	@Test
	void shouldFindOwnersByLastName() {
		Collection<Owner> owners = this.ownerService.findOwnerByLastNameAdmin("Davis");
		assertThat(owners.size()).isEqualTo(2);

		owners = this.ownerService.findOwnerByLastNameAdmin("Daviss");
		assertThat(owners.isEmpty()).isTrue();
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		Owner owner = this.ownerService.findOwnerById(1);
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets().size()).isEqualTo(1);
		assertThat(owner.getPets().get(0).getType()).isNotNull();
		assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
	}

	@Test
	@Transactional
	public void shouldInsertOwner() {
		Collection<Owner> owners = this.ownerService.findOwnerByLastNameAdmin("Schultz");
		int found = owners.size();

		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Schultz");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		User user=new User();
		user.setUsername("Sam");
		user.setPassword("supersecretpassword");
		user.setEnabled(true);
		owner.setUser(user);                

		this.ownerService.saveOwner(owner);
		assertThat(owner.getId().longValue()).isNotEqualTo(0);

		owners = this.ownerService.findOwnerByLastNameAdmin("Schultz");
		assertThat(owners.size()).isEqualTo(found + 1);
	}

	@Test
	@Transactional
	void shouldUpdateOwner() {
		Owner owner = this.ownerService.findOwnerById(1);
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";

		owner.setLastName(newLastName);
		this.ownerService.saveOwner(owner);

		// retrieving new name from database
		owner = this.ownerService.findOwnerById(1);
		assertThat(owner.getLastName()).isEqualTo(newLastName);
	}

	@Test
	@Transactional
	void shouldDeleteOwner() throws DataAccessException {
		List<Owner> allOwners = this.ownerService.findAll();

		User newUser = new User();
		Owner newOwner = new Owner();
		Authorities newAuthority = new Authorities();
		
		newAuthority.setAuthority("admin");
		newAuthority.setUser(newUser);
		
		newUser.setUsername("testUsername");
		newUser.setPassword("testPassword");
		
		newOwner.setFirstName("testFirstName");
		newOwner.setLastName("testLastName");
		newOwner.setAddress("testAddress");
		newOwner.setCity("testCity");
		newOwner.setTelephone("4444444444");
		newOwner.setUser(newUser);

		this.ownerService.saveOwner(newOwner);
		this.ownerService.delete(newOwner);
		List<Owner> allOwnersAfterInsertAndDelete = this.ownerService.findAll();

		assertThat(allOwnersAfterInsertAndDelete.size()).isEqualTo(allOwners.size());
	}
	
	@Test
	@Transactional
	void shouldDeleteOwnerExtended() throws DataAccessException {
		//Tries to insert non-persistent data
		List<Owner> allOwners = this.ownerService.findAll();

		User newUser = new User();
		Owner newOwner = new Owner();
		Authorities newAuthority = new Authorities();
		
		newAuthority.setAuthority("admin");
		newAuthority.setUser(newUser);
		
		newUser.setUsername("testUsername");
		newUser.setPassword("testPassword");
		
		newOwner.setFirstName("testFirstName");
		newOwner.setLastName("testLastName");
		newOwner.setAddress("testAddress");
		newOwner.setCity("testCity");
		newOwner.setTelephone("4444444444");
		newOwner.setUser(newUser);

		Assertions.assertThrows(InvalidDataAccessApiUsageException.class,() -> {this.ownerService.delete(newOwner);});
		
		List<Owner> allOwnersAfterFakeDelete = this.ownerService.findAll();
		assertThat(allOwnersAfterFakeDelete.size()).isEqualTo(allOwners.size());
	}
	
	@Test
	@Transactional
	void shouldAlsoDeleteOwnerPets() throws DataAccessException {
		Owner oldOwner = this.ownerService.findOwnerById(1);
		List<Owner> allOwners = this.ownerService.findAll();
		List<Pet> allPets = this.petService.findAll();
		
		this.ownerService.delete(oldOwner);
		
		List<Owner> allOwnersAfterDelete = this.ownerService.findAll();
		List<Pet> allPetsAfterDelete = this.petService.findAll();
		
		assertThat(allOwnersAfterDelete.size()).isEqualTo(allOwners.size() - 1);
		assertThat(allPetsAfterDelete.size()).isEqualTo(allPets.size() - oldOwner.getPets().size());
	}

}
