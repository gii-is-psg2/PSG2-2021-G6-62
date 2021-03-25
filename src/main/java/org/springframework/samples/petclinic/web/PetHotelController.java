package org.springframework.samples.petclinic.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.service.exceptions.WrongDatesInHotelsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/pethotel")
public class PetHotelController {

	private PetHotelService petHotelService;

	@Autowired
	public PetHotelController(PetHotelService petHotelService) {
		this.petHotelService = petHotelService;
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@ModelAttribute("pets")
	public List<Pet> populatePetTypes() {
		Object nombreOwner= SecurityContextHolder.getContext().getAuthentication().getName();
		return this.petHotelService.findPetsByUser(nombreOwner.toString());
	}
	
	@GetMapping()
	public String listPetHotel() {
		Object nombreOwner= SecurityContextHolder.getContext().getAuthentication().getName();
		String vista= "redirect:/pethotel/"+nombreOwner;
		return vista;
	}
	
	@GetMapping("/{nombre}")
	public String listPetHotelOfOwner(@PathVariable("nombre") String nombre,Map<String,Object> model) {
		List<PetHotel> petHotel = petHotelService.bookingsOfPersonsWithUserName(nombre);
		model.put("petHotel", petHotel);
		String vista= "hotel/listPetHotel";
		return vista;
	}
	
	@GetMapping("/new/{nombre}")
	public String initCreationForm(@PathVariable("nombre") String nombre,Map<String,Object> model) {
		PetHotel petHotel= new PetHotel();
		model.put("nombre", nombre);
		model.put("petHotel", petHotel);
		
		String vista= "hotel/createOrUpdateHotelForm";
		
		return vista;
	}
	
	@PostMapping(value = "/save")
	public String processCreationForm(@Valid PetHotel petHotel, BindingResult result) {
		if (result.hasErrors()) {
			return "hotel/createOrUpdateHotelForm";
		}
		else {
			try {
				this.petHotelService.saveHotel(petHotel);
			}catch (WrongDatesInHotelsException e) {
				result.rejectValue("startDate", "duplicated", "start date must be before end date");
				result.rejectValue("endDate", "duplicated", "end date must be after start date");
                return "hotel/createOrUpdateHotelForm";
			}
			
			return "redirect:/pethotel/"+petHotel.getUserName();
		}
	}
}
