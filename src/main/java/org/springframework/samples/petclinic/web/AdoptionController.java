package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.AdoptionApplicationService;
import org.springframework.samples.petclinic.service.AdoptionRequestService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.web.validators.AdoptionApplicationValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdoptionController {

	private static final String ESPANOL = "español";
	private final PetService petService;
	private final AdoptionRequestService adoptionRequestService;
	private final UserService userService;
	private final OwnerService ownerService;
	private final AdoptionApplicationService adoptionApplicationService;
	
	private static final String MESSAGE = "message";
	private static final String OWNER = "owner";
	private static final String ONLY_OWNERS_SHOULD_ADOPT = "Only owners can adopt pets!";
	
	private static final String REDIRECT_TO_ADOPTIONS = "redirect:/adoptions";
	private static final String REDIRECT_TO_ADOPTIONS2 = "redirect:/adoptions";
	private static final String REDIRECT_ADOPTION_APPLICATIONS = "redirect:/adoptionApplications";
	private static final String REDIRECT_ADOPTION_APPLICATIONS2 = "redirect:/adoptionApplications";

	@Autowired
	public AdoptionController(PetService petService, AdoptionRequestService adoptionRequestService, UserService userService,
			AdoptionApplicationService adoptionApplicationService, OwnerService ownerService) {
		this.petService = petService;
		this.adoptionRequestService = adoptionRequestService;
		this.adoptionApplicationService = adoptionApplicationService;
		this.userService = userService;
		this.ownerService = ownerService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@InitBinder("adoptionApplication")
	public void initAdoptionApplicationBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new AdoptionApplicationValidator());
	}
	
	@ModelAttribute("ownersOfUser")
	public List<Owner> getOwnersOfUser() {
		return this.ownerService.findOwnerByUserUsername(this.userService.getUserSession().getUsername());
	}
	
	@GetMapping(value = "/adoptions")
	public String listAdoptions(Map<String, Object> model) {

		String currentUser = this.userService.getUserSession().getUsername();
		List<AdoptionRequest> adoptionRequests = this.adoptionRequestService.findAdoptionRequests();

		model.put("adoptionRequests", adoptionRequests);
		model.put("currentUser", currentUser);
		return "adoptions/adoptionsList";
	}
	
	@GetMapping(value = "/adoptions/{petId}/new")
	public String requestAdoptionForPet(@PathVariable("petId") int petId, Map<String, Object> model, RedirectAttributes redirectAttributes) {

		User currentUser = this.userService.findUser(this.userService.getUserSession().getUsername()).orElseThrow(NoSuchElementException::new);
		Optional<Pet> pet = this.petService.findById(petId);
		
		Pet petOrElseThrow = pet.orElseThrow(NoSuchElementException::new);
		
		if (!petOrElseThrow.getOwner().getUser().equals(currentUser)) {
			if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
				redirectAttributes.addFlashAttribute(MESSAGE , "No puedes poner en adopcion una mascota que no es tuya!");
				return REDIRECT_TO_ADOPTIONS;
			}
			
				redirectAttributes.addFlashAttribute(MESSAGE , "You can't request the adoption of a pet that is not yours!");
				return REDIRECT_TO_ADOPTIONS;
		}

		if (this.adoptionRequestService.findPetsInAdoption().contains(petOrElseThrow)) {
			if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
				redirectAttributes.addFlashAttribute(MESSAGE , "Esta mascota ya se encuentra en adopcion!");
				return REDIRECT_TO_ADOPTIONS;
			}
			
				redirectAttributes.addFlashAttribute(MESSAGE , "This pet has already been requested for adoption!");
				return REDIRECT_TO_ADOPTIONS;
		}

		AdoptionRequest adoptionRequest = new AdoptionRequest();
		adoptionRequest.setPet(petOrElseThrow);
		this.adoptionRequestService.save(adoptionRequest);
		return REDIRECT_TO_ADOPTIONS2; 
	}
	
	@GetMapping(value = "/adoptions/{adoptionRequestId}/apply")
	public String applyForAdoptionGet(@PathVariable("adoptionRequestId") int adoptionRequestId, Map<String, Object> model, 
			RedirectAttributes redirectAttributes) {

		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

		if (!authority.equals(OWNER)) {
			redirectAttributes.addFlashAttribute(MESSAGE , ONLY_OWNERS_SHOULD_ADOPT);
			return REDIRECT_TO_ADOPTIONS;
		}
		
		Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findAdoptionRequestById(adoptionRequestId);
		User currentUser = this.userService.findUser(this.userService.getUserSession().getUsername()).orElseThrow(NoSuchElementException::new);
		
		AdoptionRequest adoptionRequestOrElseThrow = adoptionRequest.orElseThrow(NoSuchElementException::new);
		
		if (adoptionRequestOrElseThrow.getPet().getOwner().getUser().equals(currentUser)) {
			if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
				redirectAttributes.addFlashAttribute(MESSAGE , "No puedes intentar adoptar a tu propia mascota!");
				return REDIRECT_TO_ADOPTIONS;
			}
			
				redirectAttributes.addFlashAttribute(MESSAGE , "You can't apply for the adoption of your own pet!");
				return REDIRECT_TO_ADOPTIONS;
		}
		
		if (adoptionRequestOrElseThrow.getAdoptionApplications().stream().map(x -> x.getOwner().getUser())
				.collect(Collectors.toSet()).contains(currentUser)) {
			
			if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
				redirectAttributes.addFlashAttribute(MESSAGE, "No puedes crear una solicitud de adopcion para la misma mascota mas de una vez!");
				return REDIRECT_TO_ADOPTIONS;
			}
				redirectAttributes.addFlashAttribute(MESSAGE , "You can't apply for the adoption of the same pet twice or more!");
				return REDIRECT_TO_ADOPTIONS;
		}

		model.put("adoptionApplication", new AdoptionApplication());
		model.put("adoptionRequest", adoptionRequestOrElseThrow);
		return "adoptions/applyForAdoptionForm";
	}
	
	@PostMapping(value = "/adoptions/{adoptionRequestId}/apply")
	public String applyForAdoptionPost(@PathVariable("adoptionRequestId") int adoptionRequestId, 
			@Valid AdoptionApplication adoptionApplication, BindingResult result, Map<String, Object> model,
			RedirectAttributes redirectAttributes) {

		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		
		if (!authority.equals(OWNER)) {
			redirectAttributes.addFlashAttribute(MESSAGE, ONLY_OWNERS_SHOULD_ADOPT);
			return REDIRECT_TO_ADOPTIONS;
		}
		
		Owner selectedOwner = adoptionApplication.getOwner();
		Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findAdoptionRequestById(adoptionRequestId);
		
		AdoptionRequest adoptionRequestOrElseThrow = adoptionRequest.orElseThrow(NoSuchElementException::new);
		
		if (result.hasErrors()) {
			model.put("adoptionApplication", adoptionApplication);
			model.put("adoptionRequest", adoptionRequestOrElseThrow);
			return "adoptions/applyForAdoptionForm";
		}

		Set<Owner> applicantOwners = adoptionRequestOrElseThrow.getAdoptionApplications().stream()
				.map(AdoptionApplication::getOwner).collect(Collectors.toSet());
		
		if (applicantOwners.contains(selectedOwner)) {
			
			if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
				redirectAttributes.addFlashAttribute(MESSAGE, "No puedes crear una solicitud de adopcion para la misma mascota mas de una vez!");
				return REDIRECT_TO_ADOPTIONS;
			}
			
			redirectAttributes.addFlashAttribute(MESSAGE, "You can't apply for the adoption of the same pet twice or more!");
			return REDIRECT_TO_ADOPTIONS;
		}
		
		adoptionApplication.setAdoptionRequest(adoptionRequestOrElseThrow);
		this.adoptionApplicationService.save(adoptionApplication);
		return REDIRECT_TO_ADOPTIONS;
	}

	@GetMapping(value = "/adoptionApplications")
	public String listAdoptionApplications(Map<String, Object> model) {
		List<AdoptionApplication> applications = this.adoptionApplicationService.findAdoptionApplications(this.userService.getUserSession().getUsername());
		model.put("adoptionApplications", applications);
		return "adoptions/adoptionApplicationsList";
	}
	
	 @GetMapping(value = "/adoptions/{adoptionApplicationId}/adopt")
	 public String changePetOwner(@PathVariable("adoptionApplicationId") int adoptionApplicationId, Map<String, Object> model,
				RedirectAttributes redirectAttributes) {
		 
		 String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

		 if (!authority.equals(OWNER)) {
			 redirectAttributes.addFlashAttribute(MESSAGE, ONLY_OWNERS_SHOULD_ADOPT);
			 return REDIRECT_TO_ADOPTIONS;
		 }
		 
		 Optional<AdoptionApplication> adoptionApplication = this.adoptionApplicationService.findById(adoptionApplicationId);
		 User currentUser = this.userService.getUserSession();
		 
		 AdoptionApplication adoptionApplicationOrElseThrow = adoptionApplication.orElseThrow(NoSuchElementException::new);
		 Pet petInAdoption = adoptionApplicationOrElseThrow.getAdoptionRequest().getPet();
		 
		 if (!petInAdoption.getOwner().getUser().equals(currentUser)) {
			 if (Locale.getDefault().getDisplayLanguage().equals(ESPANOL)) {
					redirectAttributes.addFlashAttribute(MESSAGE, "No eres el dueño de esta mascota!");
					return REDIRECT_TO_ADOPTIONS;
				}
				
				redirectAttributes.addFlashAttribute(MESSAGE, "This pet is not yours!");
				return REDIRECT_TO_ADOPTIONS;
		 }
		 
		 Owner newOwner = adoptionApplicationOrElseThrow.getOwner();
		 Owner oldOwner = petInAdoption.getOwner();
		 oldOwner.removePet(petInAdoption);
		 newOwner.addPet(adoptionApplicationOrElseThrow.getAdoptionRequest().getPet());
		 this.ownerService.saveOwner(oldOwner);
		 this.ownerService.saveOwner(newOwner);
		 this.adoptionRequestService.delete(adoptionApplicationOrElseThrow.getAdoptionRequest());
		 return "adoptions/adoptionApplicationsList";
	 }
	 
	 @GetMapping(value = "/adoptions/{adoptionRequestId}/delete")
	 public String deleteAdoptionRequest(@PathVariable("adoptionRequestId") int adoptionRequestId, ModelMap model,
			 RedirectAttributes redirectAttributes) {

		 try {
			 AdoptionRequest adoptionRequest = this.adoptionRequestService.findById(adoptionRequestId).orElseThrow(NoSuchElementException::new);

			 if (adoptionRequest.getPet().getOwner().getUser().equals(this.userService.getUserSession())) {
				 this.adoptionRequestService.delete(adoptionRequest);
				 redirectAttributes.addFlashAttribute(MESSAGE, "Adoption request successfully deleted!");
				 return REDIRECT_TO_ADOPTIONS;
			 }
			 
				 redirectAttributes.addFlashAttribute(MESSAGE, "This adoption request is not yours!");

		 } catch (NoSuchElementException e) {
			 redirectAttributes.addFlashAttribute(MESSAGE, "Adoption request not found!");
		 }
		 
		 return REDIRECT_TO_ADOPTIONS2;
	 }

	 
	 @GetMapping(value = "/adoptionApplications/{adoptionApplicationId}/delete")
	 public String deleteAdoptionApplication(@PathVariable("adoptionApplicationId") int adoptionApplicationId, ModelMap model,
			 RedirectAttributes redirectAttributes) {
		 
		 try {
			 AdoptionApplication adoptionApplication = this.adoptionApplicationService.findById(adoptionApplicationId)
				 .orElseThrow(NoSuchElementException::new);
		 
			 if (adoptionApplication.getAdoptionRequest().getPet().getOwner().getUser().equals(this.userService.getUserSession())) {
				 this.adoptionApplicationService.delete(adoptionApplication);
				 redirectAttributes.addFlashAttribute(MESSAGE, "Adoption application successfully deleted!");
				 return REDIRECT_ADOPTION_APPLICATIONS;
			 }
			 
			 redirectAttributes.addFlashAttribute(MESSAGE, "This adoption application is not for you!");
				
		 } catch (NoSuchElementException e) {
			 redirectAttributes.addFlashAttribute(MESSAGE, "Adoption application not found!");
		 }
		 
		 return REDIRECT_ADOPTION_APPLICATIONS2;
	 }
}
