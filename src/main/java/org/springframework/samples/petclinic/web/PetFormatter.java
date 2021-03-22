package org.springframework.samples.petclinic.web;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.stereotype.Component;

@Component
public class PetFormatter implements Formatter<Pet> {

	private final PetHotelService peService;
	
	@Autowired
	public PetFormatter(PetHotelService petHotelService) {
		this.peService = petHotelService;
	}
	
	@Override
	public String print(Pet pet, Locale locale) {
		return pet.getName();
	}

	@Override
	public Pet parse(String text, Locale locale) throws ParseException {
		Collection<Pet> findPets = this.peService.findPets();
		for (Pet type : findPets) {
			if (type.getName().equals(text)) {
				return type;
			}
		}
		throw new ParseException("type not found: " + text, 0);
	}

}
