package org.springframework.samples.petclinic.web.validators;

import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AdoptionApplicationValidator implements Validator {
	
	private static final String DESCRIPTION_EMPTY = "The description cannot be empty!";
	private static final String OWNER_NOT_SELECTED = "You haven't selected any owner!";

	@Override
	public void validate(Object obj, Errors errors) {
		AdoptionApplication adoptionApplication = (AdoptionApplication) obj;
		String description = adoptionApplication.getDescription();
		Owner owner = adoptionApplication.getOwner();

		// empty description validation
		if (description == null || description.trim().isEmpty()){
			errors.rejectValue("description", DESCRIPTION_EMPTY, DESCRIPTION_EMPTY);
		}
				
		// owner not selected validation
		if (owner == null){
			errors.rejectValue("owner", OWNER_NOT_SELECTED, OWNER_NOT_SELECTED);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return AdoptionApplication.class.isAssignableFrom(clazz);
	}
}
