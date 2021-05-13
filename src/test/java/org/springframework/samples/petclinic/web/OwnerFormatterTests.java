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
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.web.formatters.OwnerFormatter;

@ExtendWith(MockitoExtension.class)
class OwnerFormatterTests {

	@Mock
	private PetHotelService petHotelService;

	private OwnerFormatter ownerFormatter;

	@BeforeEach
	void setup() {
		ownerFormatter = new OwnerFormatter(petHotelService);
	}

	@Test
	void testPrint() {
		Owner owner = new Owner();
		owner.setFirstName("Pliego de prescripciones");
		owner.setLastName("tecnicas");
		String ownerName = ownerFormatter.print(owner, Locale.ENGLISH);
		assertEquals("Pliego de prescripciones tecnicas", ownerName);
	}

	@Test
	void shouldParse() throws ParseException {
		Mockito.when(petHotelService.findAllOwners()).thenReturn(makeOwners());
		Owner owner = ownerFormatter.parse("Wolfie Wolf", Locale.ENGLISH);
		assertEquals("Wolfie", owner.getFirstName());
		assertEquals("Wolf", owner.getLastName());
	}

	@Test
	void shouldThrowParseException() throws ParseException {
		Mockito.when(petHotelService.findAllOwners()).thenReturn(makeOwners());
		Assertions.assertThrows(ParseException.class, () -> {
			ownerFormatter.parse("Fish", Locale.ENGLISH);
		});
	}

	private List<Owner> makeOwners() {
		List<Owner> owners = new ArrayList<Owner>();
		owners.add(new Owner() {
			{
				setFirstName("Wolfie");
				setLastName("Wolf");
			}
		});
		return owners;
	}

}
