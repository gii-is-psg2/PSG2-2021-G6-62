package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class PetHotelServiceTest {

	@Autowired
	protected PetHotelService petHotelService;

	@Autowired
	protected PetService petService;

	@Test
	void shouldDoBookingsOfPersonsWithName() {
		Collection<PetHotel> petHotels = petHotelService.bookingsOfPersonsWithUserName("owner1");
		assertThat(petHotels).hasSize(1);
	}
	
	@Test
	void shouldDoBookingsOfPersonsWithUnknownName() {
		Collection<PetHotel> petHotels2test = petHotelService.bookingsOfPersonsWithUserName("SAUBS7ns22bs2");
		assertThat(petHotels2test).isEmpty();
	}

	@Test
	void shouldSearchForBookingById() {
		PetHotel booking = this.petHotelService.findHotelById(1);
		assertThat(booking).isNotNull();
	}

	@Test
	@Transactional
	void shouldSaveBooking() {
		PetHotel booking = new PetHotel();
		Pet pet = this.petService.findPetById(1);

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setPet(pet);
		booking.setStartDate(LocalDate.of(2065, 12, 1));
		booking.setEndDate(LocalDate.of(2065, 12, 13));

		try {
			this.petHotelService.saveHotel(booking);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		assertThat(booking.getId()).isNotNull();
	}
	
	@Test
	@Transactional
	void shouldNotSaveBookingNullPet() {
		PetHotel booking = new PetHotel();

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setStartDate(LocalDate.of(2065, 12, 12));
		booking.setEndDate(LocalDate.of(2065, 12, 14));

		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			this.petHotelService.saveHotel(booking);
		});

		assertThat(booking.getId()).isNull();
	}
	
	@Test
	void shouldFindAllPets() {
		List<Pet> pets = petHotelService.findAllPets();
		assertThat(pets).isNotEmpty();
	}
	
	@Test
	void shouldFindAllPetsByOwner() {
		List<Pet> pets = petHotelService.findPetsByUser("owner1");
		assertThat(pets).isNotEmpty();
	}
	
	@Test
	void shouldFindAllPetsByUnknownOwner() {
		List<Pet> pets = petHotelService.findPetsByUser("ohBI7AT6VTbauswnm12");
		assertThat(pets).isEmpty();
	}

}
