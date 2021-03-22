package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PetHotelServiceTest {

	 @Autowired
	protected PetHotelService petHotelService;
	 
	 @Test
	 void shouldDoBookingsOfPersonsWithName() {
		 Collection<PetHotel> petHotels= petHotelService.bookingsOfPersonsWithUserName("owner1");
		 assertThat(petHotels.size()).isEqualTo(1);
		 
		 Collection<PetHotel> petHotels2test= petHotelService.bookingsOfPersonsWithUserName("owner2");
		 assertThat(petHotels2test.isEmpty()).isTrue();
	 }
	
	
}
