package org.springframework.samples.petclinic.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.AdoptionApplicationService;
import org.springframework.samples.petclinic.service.AdoptionRequestService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.UserService;
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

	private final PetService petService;
	private final AdoptionRequestService adoptionRequestService;
	private final UserService userService;
	private final OwnerService ownerService;
	private final AdoptionApplicationService adoptionApplicationService;

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
	public String requestAdoptionForPet(@PathVariable("petId") int petId, Map<String, Object> model) {

//		Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findAdoptionRequestById(adoptionRequestId);
		User currentUser = this.userService.findUser(this.userService.getUserSession().getUsername()).get();

		Optional<Pet> pet = this.petService.findById(petId);
		String vista = "redirect:/adoptions";
		
		if (pet.isPresent() && pet.get().getOwner().getUser().equals(currentUser)) {
			AdoptionRequest adoptionRequest = new AdoptionRequest();
			adoptionRequest.setPet(pet.get());
			this.adoptionRequestService.save(adoptionRequest);
		}

		return vista;
	}
	
	@GetMapping(value = "/adoptions/{adoptionRequestId}/apply")
	public String applyForAdoptionGet(@PathVariable("adoptionRequestId") int adoptionRequestId, Map<String, Object> model, 
			RedirectAttributes redirectAttributes) {

		Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findAdoptionRequestById(adoptionRequestId);
		User currentUser = this.userService.findUser(this.userService.getUserSession().getUsername()).get();

		String vista = "redirect:/adoptions";
		
		if (adoptionRequest.isPresent() && !adoptionRequest.get().getPet().getOwner().getUser().equals(currentUser)) {
			if (!adoptionRequest.get().getAdoptionApplications().stream().map(x -> x.getOwner().getUser())
					.collect(Collectors.toSet()).contains(currentUser)) {
			model.put("adoptionApplication", new AdoptionApplication());
			model.put("adoptionRequest", adoptionRequest.get());
			vista = "adoptions/applyForAdoptionForm";
			} else {
				if (Locale.getDefault().getDisplayLanguage().equals("español")) {
					redirectAttributes.addFlashAttribute("message", "No puedes crear una solicitud de adopcion para la misma mascota mas de una vez!");
				} else {
					redirectAttributes.addFlashAttribute("message", "You can't apply for the adoption of the same pet twice or more!");
				}
			}
		}

		return vista;
	}
	
	@PostMapping(value = "/adoptions/{adoptionRequestId}/apply")
	public String applyForAdoptionPost(@PathVariable("adoptionRequestId") int adoptionRequestId, 
			@Valid AdoptionApplication adoptionApplication, Map<String, Object> model, BindingResult result,
			RedirectAttributes redirectAttributes) {

		Owner selectedOwner = adoptionApplication.getOwner();
		Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findAdoptionRequestById(adoptionRequestId);
		
		String vista = "redirect:/adoptions";
		
		if (result.hasErrors()) {
			vista = "adoptions/applyForAdoptionForm";
		} else {
			if (adoptionRequest.isPresent()) {
				if (!adoptionRequest.get().getAdoptionApplications().stream().map(x -> x.getOwner())
						.collect(Collectors.toSet()).contains(selectedOwner)) {
					adoptionApplication.setAdoptionRequest(adoptionRequest.get());
					this.adoptionApplicationService.save(adoptionApplication);
				} else {
					if (Locale.getDefault().getDisplayLanguage().equals("español")) {
						redirectAttributes.addFlashAttribute("message", "No puedes crear una solicitud de adopcion para la misma mascota mas de una vez!");
					} else {
						redirectAttributes.addFlashAttribute("message", "You can't apply for the adoption of the same pet twice or more!");
					}
				}
			}
		}
		return vista;
	}

	@GetMapping(value = "/adoptionApplications")
	public String listAdoptionApplications(Map<String, Object> model) {
		List<AdoptionApplication> applications = this.adoptionApplicationService.findAdoptionApplications(this.userService.getUserSession().getUsername());
		model.put("adoptionApplications", applications);
		return "adoptions/adoptionApplicationsList";
	}
	
	 @GetMapping(value = "/adoptions/{adoptionApplicationId}/adopt")
	 public String changePetOwner(@PathVariable("adoptionApplicationId") int adoptionApplicationId, Map<String, Object> model) {
		 Optional<AdoptionApplication> adoptionApplication = this.adoptionApplicationService.findById(adoptionApplicationId);
		 User currentUser = this.userService.getUserSession();
		 Pet petInAdoption = adoptionApplication.get().getAdoptionRequest().getPet();
		 if (adoptionApplication.isPresent() && petInAdoption.getOwner().getUser().equals(currentUser)) {
			 Owner newOwner = adoptionApplication.get().getOwner();
			 Owner oldOwner = adoptionApplication.get().getAdoptionRequest().getPet().getOwner();
			 oldOwner.removePet(petInAdoption);
			 newOwner.addPet(adoptionApplication.get().getAdoptionRequest().getPet());
			 this.ownerService.saveOwner(oldOwner);
			 this.ownerService.saveOwner(newOwner);
		 }
		 return "adoptions/adoptionApplicationsList";
	 }
	 
	 @GetMapping(value = "/adoptions/{adoptionRequestId}/delete")
	 public String deleteAdoptionRequest(@PathVariable("adoptionRequestId") int adoptionRequestId, ModelMap model,
			 RedirectAttributes redirectAttributes) {
		 Optional<AdoptionRequest> adoptionRequest = this.adoptionRequestService.findById(adoptionRequestId);

		 if (adoptionRequest.isPresent()) {
			 if (adoptionRequest.get().getPet().getOwner().getUser().equals(this.userService.getUserSession())) {
				 this.adoptionRequestService.delete(adoptionRequest.get());
				 redirectAttributes.addFlashAttribute("message", "Adoption request successfully deleted!");
			 } else {
				 redirectAttributes.addFlashAttribute("message", "This adoption request is not yours!");
			 }
		 } else {
			 redirectAttributes.addFlashAttribute("message", "Adoption request not found!");
		 }
		 return "redirect:/adoptions";
	 }

	 
	 @GetMapping(value = "/adoptionApplications/{adoptionApplicationId}/delete")
	 public String deleteAdoptionApplication(@PathVariable("adoptionApplicationId") int adoptionApplicationId, ModelMap model,
			 RedirectAttributes redirectAttributes) {
		 Optional<AdoptionApplication> adoptionApplication = this.adoptionApplicationService.findById(adoptionApplicationId);

		 if (adoptionApplication.isPresent()) {
			 if (adoptionApplication.get().getAdoptionRequest().getPet().getOwner().getUser().equals(this.userService.getUserSession())) {
				 this.adoptionApplicationService.delete(adoptionApplication.get());
				 redirectAttributes.addFlashAttribute("message", "Adoption application successfully deleted!");
			 } else {
				 redirectAttributes.addFlashAttribute("message", "This adoption application is not for you!");
			 }
		 } else {
			 redirectAttributes.addFlashAttribute("message", "Adoption application not found!");
		 }
		 return "redirect:/adoptionApplications";
	 }

}
