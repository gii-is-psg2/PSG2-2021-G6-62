package org.springframework.samples.petclinic.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.PetHotelService;

@ExtendWith(MockitoExtension.class)
class PetFormatterTests {

	@Mock
	private PetHotelService petHotelService;

	private PetFormatter petFormatter;

	@BeforeEach
	void setup() {
		petFormatter = new PetFormatter(petHotelService);
	}

	@Test
	void testPrint() {
		Pet pet = new Pet();
		pet.setName("Pliego_de_prescripciones");
		String petName = petFormatter.print(pet, Locale.ENGLISH);
		assertEquals("Pliego_de_prescripciones", petName);
	}

	@Test
	void shouldParse() throws ParseException {
		Mockito.when(petHotelService.findAllPets()).thenReturn(makePets());
		Pet pet = petFormatter.parse("Wolf", Locale.ENGLISH);
		assertEquals("Wolf", pet.getName());
	}

	@Test
	void shouldThrowParseException() throws ParseException {
		Mockito.when(petHotelService.findAllPets()).thenReturn(makePets());
		Assertions.assertThrows(ParseException.class, () -> {
			petFormatter.parse("Fish", Locale.ENGLISH);
		});
	}

	private List<Pet> makePets() {
		List<Pet> pets = new ArrayList<Pet>();
		pets.add(new Pet() {
			{
				setName("Wolf");
			}
		});
		return pets;
	}

}
