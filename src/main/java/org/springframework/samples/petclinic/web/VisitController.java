/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;

import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transaction;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
public class VisitController {

	private final PetService petService;

	@Autowired
	public VisitController(PetService petService) {
		this.petService = petService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

//	@ModelAttribute("visit")
//	public Visit loadPetWithVisit(@PathVariable("petId") int petId) {
//		Pet pet = this.petService.findPetById(petId);
//		Visit visit = new Visit();
//		pet.addVisit(visit);
//		return visit;
//	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
	@GetMapping(value = "/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		Pet pet = this.petService.findPetById(petId);
		Visit visit = new Visit();
		pet.addVisit(visit);
		model.put("visit", visit);
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
	@PostMapping(value = "/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@PathVariable("petId") int petId, @Valid Visit visit, BindingResult result) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		else {
			Pet pet = this.petService.findPetById(petId);
			visit.setPet(pet);
			this.petService.saveVisit(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping(value = "/owners/*/pets/{petId}/visits")
	public String showVisits(@PathVariable int petId, Map<String, Object> model) {
		model.put("visits", this.petService.findPetById(petId).getVisits());
		return "visitList";
	}
	
	@GetMapping(value = "/owners/{ownerId}/pets/{petId}/visits/{visitId}/delete")
	public String deleteVisit(@PathVariable("visitId") int visitId, @PathVariable("ownerId") int ownerId, ModelMap model, RedirectAttributes redirectAttributes) {
		Optional<Visit> visit = this.petService.findVisitById(visitId);
		if (visit.isPresent()) {
			petService.deleteVisit(visit.get());
			redirectAttributes.addFlashAttribute("message", "Visit successfully deleted!");
		} else {
			redirectAttributes.addFlashAttribute("message", "Visit not found!");
		}

		return "redirect:/owners/" + ownerId;
	}

}
