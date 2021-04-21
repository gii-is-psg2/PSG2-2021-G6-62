package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.service.exceptions.OverlappingBookingDatesException;
import org.springframework.samples.petclinic.service.exceptions.WrongDatesInHotelsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PetHotelServiceTest {

	@Autowired
	protected PetHotelService petHotelService;

	@Autowired
	protected PetService petService;

	@Test
	void shouldDoBookingsOfPersonsWithName() {
		Collection<PetHotel> petHotels = petHotelService.bookingsOfPersonsWithUserName("owner1");
		assertThat(petHotels.size()).isEqualTo(1);
	}
	
	@Test
	void shouldDoBookingsOfPersonsWithUnknownName() {
		Collection<PetHotel> petHotels2test = petHotelService.bookingsOfPersonsWithUserName("SAUBS7ns22bs2");
		assertThat(petHotels2test.isEmpty()).isTrue();
	}

	@Test
	void shouldSearchForBookingById() {
		PetHotel booking = this.petHotelService.findHotelById(1);
		assertThat(booking).isNotNull();
	}

	@Test
	@Transactional
	void shouldSaveBooking() throws OverlappingBookingDatesException {
		PetHotel booking = new PetHotel();
		Pet pet = this.petService.findPetById(1);

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setPet(pet);
		booking.setStartDate(LocalDate.of(2065, 12, 1));
		booking.setEndDate(LocalDate.of(2065, 12, 13));

		try {
			this.petHotelService.saveHotel(booking);
		} catch (DataAccessException | WrongDatesInHotelsException e) {
			e.printStackTrace();
		}

		assertThat(booking.getId()).isNotNull();
	}

	@Test
	@Transactional
	void shouldNotSaveBookingWrongDates() {
		PetHotel booking = new PetHotel();
		Pet pet = this.petService.findPetById(1);

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setPet(pet);
		booking.setStartDate(LocalDate.of(2065, 12, 12));
		booking.setEndDate(LocalDate.of(2065, 12, 1));

		Assertions.assertThrows(WrongDatesInHotelsException.class, () -> {
			this.petHotelService.saveHotel(booking);
		});

		assertThat(booking.getId()).isNull();
	}
	
	@Test
	@Transactional
	void shouldNotSaveBookingOverlappingDates() throws DataAccessException, WrongDatesInHotelsException, OverlappingBookingDatesException {
		PetHotel booking = new PetHotel();
		PetHotel booking2 = new PetHotel();
		Pet pet = this.petService.findPetById(1);

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setPet(pet);
		booking.setStartDate(LocalDate.of(2065, 12, 1));
		booking.setEndDate(LocalDate.of(2065, 12, 13));
		this.petHotelService.saveHotel(booking);

		booking2.setDescription("Este pet imaginario se va de casa");
		booking2.setUserName("owner1");
		booking2.setPet(pet);
		booking2.setStartDate(LocalDate.of(2065, 12, 2));
		booking2.setEndDate(LocalDate.of(2065, 12, 14));
		
		Assertions.assertThrows(OverlappingBookingDatesException.class, () -> {
			this.petHotelService.saveHotel(booking2);
		});

		assertThat(booking2.getId()).isNull();
	}
	
	@Test
	@Transactional
	void shouldNotSaveBookingOverlappingDates2() throws DataAccessException, WrongDatesInHotelsException, OverlappingBookingDatesException {
		PetHotel booking = new PetHotel();
		PetHotel booking2 = new PetHotel();
		Pet pet = this.petService.findPetById(1);

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setPet(pet);
		booking.setStartDate(LocalDate.of(2065, 12, 1));
		booking.setEndDate(LocalDate.of(2065, 12, 13));
		this.petHotelService.saveHotel(booking);

		booking2.setDescription("Este pet imaginario se va de casa");
		booking2.setUserName("owner1");
		booking2.setPet(pet);
		booking2.setStartDate(LocalDate.of(2065, 12, 5));
		booking2.setEndDate(LocalDate.of(2065, 12, 10));
		
		Assertions.assertThrows(OverlappingBookingDatesException.class, () -> {
			this.petHotelService.saveHotel(booking2);
		});

		assertThat(booking2.getId()).isNull();
	}
	
	@Test
	@Transactional
	void shouldNotSaveBookingNullPet() {
		PetHotel booking = new PetHotel();

		booking.setDescription("Este pet imaginario se va de casa");
		booking.setUserName("owner1");
		booking.setStartDate(LocalDate.of(2065, 12, 12));
		booking.setEndDate(LocalDate.of(2065, 12, 14));

		Assertions.assertThrows(NullPointerException.class, () -> {
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
