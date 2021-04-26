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

	private static final String END_DATE = "endDate";
	private static final String START_DATE = "startDate";
	private static final String PET_HOTEL = "petHotel";
	private static final String ADMIN = "admin";
	private static final String REDIRECT_PETHOTEL = "redirect:/pethotel/";
	private static final String DUPLICATED = "duplicated";
	private PetHotelService petHotelService;
	private UserService userService;
	
	private static final String TOO_MANY_BOOKINGS = "Ya dispones de una o más reservas en este intervalo de tiempo!";
	private static final String CREATE_OR_UPDATE_FORM_VIEW = "hotel/createOrUpdateHotelForm";
	private static final String LIST_PET_HOTEL_VIEW = "hotel/listPetHotel";
	private static final String NOMBRE = "nombre";

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
			List<Pet>pets=this.petHotelService.findPetsByUser(nombre);
			model.put("pets", pets);
		}else {
			
			if(!nombreOwner.equals(nombre)) {
				return REDIRECT_PETHOTEL+nombreOwner+"/new";
			}
		}
		
		PetHotel petHotel = new PetHotel();
		model.put(NOMBRE, nombre);
		model.put(PET_HOTEL, petHotel);

		return CREATE_OR_UPDATE_FORM_VIEW;
		
	}

	@PostMapping(value = "/save")
	public String processCreationForm(@Valid PetHotel petHotel, BindingResult result, Map<String, Object> model) {
		
		if (result.hasErrors()) {
			return CREATE_OR_UPDATE_FORM_VIEW;
		} else {
			Object nombre = SecurityContextHolder.getContext().getAuthentication().getName();
			String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

			if(!authority.equals(ADMIN)) {
				String nombreOwner=petHotel.getUserName();

				if(!nombreOwner.equals(nombre)) {
					return REDIRECT_PETHOTEL+nombre;
				} else {
					try {
						this.petHotelService.saveHotelForOwner(petHotel);
					} catch (WrongPastDateInHotelsException e) {
						result.rejectValue(START_DATE, DUPLICATED, "la fecha de inicio debe ser antes de la final y despues de la de actual");
						result.rejectValue(END_DATE, DUPLICATED, "la fecha final debe ser después de la de inicio");
						model.put(NOMBRE, petHotel.getUserName());
						return CREATE_OR_UPDATE_FORM_VIEW;
					} catch (OverlappingBookingDatesException e) {
						result.rejectValue(START_DATE, DUPLICATED, TOO_MANY_BOOKINGS);
						result.rejectValue(END_DATE, DUPLICATED, TOO_MANY_BOOKINGS);
						model.put(NOMBRE, petHotel.getUserName());
						return CREATE_OR_UPDATE_FORM_VIEW;
					}
					return REDIRECT_PETHOTEL + nombre;
				}
			} else {
				try {
					this.petHotelService.saveHotel(petHotel);
				} catch (WrongDatesInHotelsException e) {
					result.rejectValue(START_DATE, DUPLICATED, "la fecha de inicio debe ser antes de la final");
					result.rejectValue(END_DATE, DUPLICATED, "la fecha final debe ser después de la de inicio");
					model.put(NOMBRE, petHotel.getUserName());
					return CREATE_OR_UPDATE_FORM_VIEW;
				} catch (OverlappingBookingDatesException e) {
					result.rejectValue(START_DATE, DUPLICATED, TOO_MANY_BOOKINGS);
					result.rejectValue(END_DATE, DUPLICATED, TOO_MANY_BOOKINGS);
					model.put(NOMBRE, petHotel.getUserName());
					return CREATE_OR_UPDATE_FORM_VIEW;
				}

				return "redirect:/pethotel";
			}
		}
	}
}
