package org.springframework.samples.petclinic.web.formatters;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.PetHotelService;

public class OwnerFormatter implements Formatter<Owner>{
	private final PetHotelService peService;
	
	@Autowired
	public OwnerFormatter(PetHotelService petHotelService) {
		this.peService = petHotelService;
	}
	
	@Override
	public Owner parse(String text, Locale locale) throws ParseException {
		Collection<Owner> findOwners = this.peService.findAllOwners();
		for (Owner o : findOwners) {
			if ((o.getFirstName()+ " " + o.getLastName()).equals(text)) {
				return o;
			}
		}
		throw new ParseException("owner not found: " + text, 0);
	}

	@Override
	public String print(Owner owner, Locale locale) {
		
		return owner.getFirstName() + " " + owner.getLastName();
	}
	
	

}
