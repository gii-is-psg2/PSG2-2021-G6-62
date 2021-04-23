package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.service.exceptions.OverlappingBookingDatesException;
import org.springframework.samples.petclinic.service.exceptions.WrongDatesInHotelsException;
import org.springframework.samples.petclinic.service.exceptions.WrongPastDateInHotelsException;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/pethotel")
public class PetHotelController {

	private PetHotelService petHotelService;
	private UserService userService;

	@Autowired
	public PetHotelController(PetHotelService petHotelService, UserService userService) {
		this.petHotelService = petHotelService;
		this.userService =userService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("pets")
	public List<Pet> populatePetTypes() {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		return this.petHotelService.findPetsByUser(nombreOwner.toString());
	}

	@GetMapping()
	public String listPetHotel( Map<String, Object> model) {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String vista = "hotel/listPetHotel";
		
		if(!authority.equals("admin")) {
			return "redirect:/pethotel/" + nombreOwner;
		}
		
		List<PetHotel> petHotel = this.petHotelService.findAllBookings();
		model.put("petHotel", petHotel);
		
		return vista;
	}
	
	@GetMapping("/selectUser")
	public String selectUserAdmin(Map<String, Object> model) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String vista = "hotel/listPetHotel";
		
		if(!authority.equals("admin")) {
			return "redirect:/pethotel/" + nombreOwner;
		}
		
		vista= "hotel/userSelect";
		List<Owner> owners = petHotelService.findAllOwners(); 
		model.put("owners", owners);
		
		model.put("petHotel", new PetHotel());
		return vista;
	}
	
	@GetMapping("/selectUserToNew")
	public String selectUserToNew(@RequestParam("userName") String userName) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		
		if(!authority.equals("admin")) {
			return "redirect:/pethotel/" + nombreOwner;
		}
		
		return "redirect:/pethotel/"+ userName +"/new";
	}
	
	@GetMapping("/{nombre}")
	public String listPetHotelOfOwner(@PathVariable("nombre") String nombre, Map<String, Object> model) {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(!nombreOwner.equals(nombre)) {
			return "redirect:/pethotel/"+nombreOwner;
		}
		
		String vista= "hotel/listPetHotel";
		List<PetHotel> petHotel = this.petHotelService.bookingsOfPersonsWithUserName(nombre);
		model.put("petHotel", petHotel);
		return vista;
	}

	@GetMapping("/{nombre}/new")
	public String initCreationForm(@PathVariable("nombre") String nombre, Map<String, Object> model) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		
		if(authority.equals("admin")) {
			List<Pet>pets=this.petHotelService.findPetsByUser(nombre.toString());
			model.put("pets", pets);
		}else {
			
			if(!nombreOwner.equals(nombre)) {
				return "redirect:/pethotel/"+nombreOwner+"/new";
			}
		}
		

			PetHotel petHotel = new PetHotel();
			model.put("nombre", nombre);
			model.put("petHotel", petHotel);

		String vista = "hotel/createOrUpdateHotelForm";

		return vista;
		
	}

	@PostMapping(value = "/save")
	public String processCreationForm(@Valid PetHotel petHotel, BindingResult result, Map<String, Object> model) {
		
		if (result.hasErrors()) {
			return "hotel/createOrUpdateHotelForm";
		} else {
			Object nombre = SecurityContextHolder.getContext().getAuthentication().getName();
			String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

			if(!authority.equals("admin")) {
				String nombreOwner=petHotel.getUserName();

				if(!nombreOwner.equals(nombre)) {
					return "redirect:/pethotel/"+nombre;
				} else {
					try {
						this.petHotelService.saveHotelForOwner(petHotel);
					} catch (WrongPastDateInHotelsException e) {
						result.rejectValue("startDate", "duplicated", "la fecha de inicio debe ser antes de la final y despues de la de actual");
						result.rejectValue("endDate", "duplicated", "la fecha final debe ser después de la de inicio");
						model.put("nombre", petHotel.getUserName());
						return "hotel/createOrUpdateHotelForm";
					} catch (OverlappingBookingDatesException e) {
						result.rejectValue("startDate", "duplicated", "Ya dispones de una o más reservas en este intervalo de tiempo!");
						result.rejectValue("endDate", "duplicated", "Ya dispones de una o más reservas en este intervalo de tiempo!");
						model.put("nombre", petHotel.getUserName());
						return "hotel/createOrUpdateHotelForm";
					}
					return "redirect:/pethotel/" + nombre;
				}
			} else {
				try {
					this.petHotelService.saveHotel(petHotel);
				} catch (WrongDatesInHotelsException e) {
					result.rejectValue("startDate", "duplicated", "la fecha de inicio debe ser antes de la final");
					result.rejectValue("endDate", "duplicated", "la fecha final debe ser después de la de inicio");
					model.put("nombre", petHotel.getUserName());
					return "hotel/createOrUpdateHotelForm";
				} catch (OverlappingBookingDatesException e) {
					result.rejectValue("startDate", "duplicated", "Ya dispones de una o más reservas en este intervalo de tiempo!");
					result.rejectValue("endDate", "duplicated", "Ya dispones de una o más reservas en este intervalo de tiempo!");
					model.put("nombre", petHotel.getUserName());
					return "hotel/createOrUpdateHotelForm";
				}

				return "redirect:/pethotel";
			}
		}
	}
}
