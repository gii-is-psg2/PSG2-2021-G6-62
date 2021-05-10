package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.repository.PetHotelRepository;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.web.validators.PetHotelValidator;
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

	private static final String PET_HOTEL = "petHotel";
	private static final String ADMIN = "admin";
	private static final String REDIRECT_PETHOTEL = "redirect:/pethotel/";
	private static final String CREATE_OR_UPDATE_FORM_VIEW = "hotel/createOrUpdateHotelForm";
	private static final String LIST_PET_HOTEL_VIEW = "hotel/listPetHotel";
	private static final String NOMBRE = "nombre";
	
	private PetHotelService petHotelService;
	private PetHotelRepository petHotelRepository;
	private UserService userService;

	@Autowired
	public PetHotelController(PetHotelService petHotelService, UserService userService, PetHotelRepository petHotelRepository) {
		this.petHotelService = petHotelService;
		this.userService = userService;
		this.petHotelRepository = petHotelRepository;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@InitBinder("petHotel")
	public void initPetHotelBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetHotelValidator(this.petHotelRepository, this.userService));
	}

	@ModelAttribute("pets")
	public List<Pet> populatePetTypes() {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		return this.petHotelService.findPetsByUser(nombreOwner.toString());
	}

	@GetMapping()
	public String listPetHotel(Map<String, Object> model) {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String vista = LIST_PET_HOTEL_VIEW;
		
		if(!authority.equals(ADMIN)) {
			return REDIRECT_PETHOTEL + nombreOwner;
		}
		
		List<PetHotel> petHotel = this.petHotelService.findAllBookings();
		model.put(PET_HOTEL, petHotel);
		
		return vista;
	}
	
	@GetMapping("/selectUser")
	public String selectUserAdmin(Map<String, Object> model) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String vista = "hotel/userSelect";
		
		if(!authority.equals(ADMIN)) {
			return REDIRECT_PETHOTEL + nombreOwner;
		}
		
		
		List<Owner> owners = petHotelService.findAllOwners(); 
		model.put("owners", owners);
		
		model.put(PET_HOTEL, new PetHotel());
		return vista;
	}
	
	@GetMapping("/selectUserToNew")
	public String selectUserToNew(@RequestParam("userName") String userName) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		
		if(!authority.equals(ADMIN)) {
			return REDIRECT_PETHOTEL + nombreOwner;
		}
		
		return REDIRECT_PETHOTEL+ userName +"/new";
	}
	
	@GetMapping("/{nombre}")
	public String listPetHotelOfOwner(@PathVariable("nombre") String nombre, Map<String, Object> model) {
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(!nombreOwner.equals(nombre)) {
			return REDIRECT_PETHOTEL+nombreOwner;
		}
		
		String vista= LIST_PET_HOTEL_VIEW;
		List<PetHotel> petHotel = this.petHotelService.bookingsOfPersonsWithUserName(nombre);
		model.put(PET_HOTEL, petHotel);
		return vista;
	}

	@GetMapping("/{nombre}/new")
	public String initCreationForm(@PathVariable("nombre") String nombre, Map<String, Object> model) {
		
		Object nombreOwner = SecurityContextHolder.getContext().getAuthentication().getName();
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		
		if(authority.equals(ADMIN)) {
			List<Pet>pets = this.petHotelService.findPetsByUser(nombre);
			model.put("pets", pets);
		} else if (!nombreOwner.equals(nombre)) {
			return REDIRECT_PETHOTEL + nombreOwner + "/new";
		}
		
		PetHotel petHotel = new PetHotel();
		model.put(NOMBRE, nombre);
		model.put(PET_HOTEL, petHotel);

		return CREATE_OR_UPDATE_FORM_VIEW;
	}

	@PostMapping(value = "/save")
	public String processCreationForm(@Valid PetHotel petHotel, BindingResult result, Map<String, Object> model) {

		Object nombre = SecurityContextHolder.getContext().getAuthentication().getName();

		if (result.hasErrors()) {
			model.put(NOMBRE, nombre);
			return CREATE_OR_UPDATE_FORM_VIEW;
		}
		
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String nombreOwner = petHotel.getUserName();

		if (!authority.equals(ADMIN) && !nombreOwner.equals(nombre)) {
			return REDIRECT_PETHOTEL + nombre;
		}

		this.petHotelService.saveHotel(petHotel);
		return REDIRECT_PETHOTEL;
	}
}
