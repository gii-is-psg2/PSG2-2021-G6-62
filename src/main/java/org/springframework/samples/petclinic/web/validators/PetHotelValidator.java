package org.springframework.samples.petclinic.web.validators;

import java.time.LocalDate;

import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.repository.PetHotelRepository;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PetHotelValidator implements Validator {
	
	private static final String END_DATE = "endDate";
	private static final String START_DATE = "startDate";
	private static final String OVERLAPPING_DATES = "El rango de fechas escogido no puede solaparse con uno ya existente para la misma mascota";
	private static final String DESCRIPTION_EMPTY = "The description cannot be empty!";
	private static final String PET_NOT_SELECTED = "You haven't selected any pet!";
	
	private PetHotelRepository petHotelRepository;
	private UserService userService;

	public PetHotelValidator(PetHotelRepository petHotelRepository, UserService userService) {
		this.petHotelRepository = petHotelRepository;
		this.userService = userService;
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		PetHotel petHotel = (PetHotel) obj;
		String description = petHotel.getDescription();
		Pet pet = petHotel.getPet();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

		// empty description validation
		if (description == null || description.trim().isEmpty()){
			errors.rejectValue("description", DESCRIPTION_EMPTY, DESCRIPTION_EMPTY);
		}
				
		// start date not null validation
		if (petHotel.getStartDate() == null) {
			errors.rejectValue(START_DATE, "La fecha de inicio no debe ser nula", "La fecha de inicio no debe ser nula");
		
		// end date not null validation
		} else if (petHotel.getEndDate() == null) {
			errors.rejectValue(END_DATE, "La fecha de fin no debe ser nula", "La fecha de fin no debe ser nula");
		
		// start date must be after current date validation
		} else if (!authority.equals("admin") && !petHotel.getStartDate().isAfter(LocalDate.now())) {
			errors.rejectValue(START_DATE, "La fecha de inicio debe ser posterior a la fecha actual", "La fecha de inicio debe ser posterior a la fecha actual");
		
		// start date must be before end date validation
		} else if (petHotel.getStartDate().isAfter(petHotel.getEndDate())) {
			errors.rejectValue(START_DATE, "La fecha de inicio debe ser anterior a la fecha final", "La fecha de inicio debe ser anterior a la fecha final");
			errors.rejectValue(END_DATE, "La fecha de fin debe ser posterior a la fecha inicial", "La fecha de fin debe ser posterior a la fecha inicial");
		
		// pet not selected validation
		} else if (pet == null) {
				errors.rejectValue("pet", PET_NOT_SELECTED, PET_NOT_SELECTED);
		} else {
			
			// selected range of dates can not overlap with existent bookings validation
			for (PetHotel ph : this.petHotelRepository.findPetHotelByPetId(petHotel.getPet().getId())) {
				if (petHotel.getStartDate().isBefore(ph.getEndDate()) && petHotel.getEndDate().isAfter(ph.getStartDate())) {
					errors.rejectValue(START_DATE, OVERLAPPING_DATES, OVERLAPPING_DATES);
					errors.rejectValue(END_DATE, OVERLAPPING_DATES, OVERLAPPING_DATES);
					errors.rejectValue("pet", OVERLAPPING_DATES, OVERLAPPING_DATES);
				}
			}
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return PetHotel.class.isAssignableFrom(clazz);
	}
}
