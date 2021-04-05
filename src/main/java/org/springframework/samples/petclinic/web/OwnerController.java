package org.springframework.samples.petclinic.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.AuthoritiesService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerService ownerService;
	private UserService userService;

	@Autowired
	public OwnerController(OwnerService ownerService, UserService userService, AuthoritiesService authoritiesService) {
		this.ownerService = ownerService;
		this.userService = userService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping(value = "/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping(value = "/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			//creating owner, user and authorities
			this.ownerService.saveOwner(owner);
			
			return "redirect:/owners/" + owner.getId();
		}
	}

	@GetMapping(value = "/owners/find")
	public String initFindForm(Map<String, Object> model) {
	
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping(value = "/owners")
	public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {
		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}

		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		Collection<Owner> results = new ArrayList<Owner>();
		// find owners by last name
		if (authority.equals("admin")) {
			 results = this.ownerService.findOwnerByLastNameAdmin(owner.getLastName());

		} else {
			 results = this.ownerService.findOwnerByLastName(owner.getLastName());
		}
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping(value = "/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		Owner owner = this.ownerService.findOwnerById(ownerId);
		
		if (authority.equals("admin") || owner.getUser().equals(this.userService.getUserSession())) {
			model.addAttribute(owner);
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		} else {
			return "redirect:/";
		}
	}

	@PostMapping(value = "/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId) {
		
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

		if(authority.equals("admin") || owner.getUser().equals(this.userService.getUserSession())) {
			if (result.hasErrors()) {
				return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
			}
			else {
				owner.setId(ownerId);
				this.ownerService.saveOwner(owner);
				return "redirect:/owners/{ownerId}";
			}
		} else {
			return "redirect:/";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		Owner owner = this.ownerService.findOwnerById(ownerId);
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		ModelAndView mav;
		if (authority.equals("admin") || owner.getUser().equals(this.userService.getUserSession())) {
			mav = new ModelAndView("owners/ownerDetails");
			mav.addObject(owner);
		} else {
			mav = new ModelAndView("redirect:/");
		}
		return mav;
	}
	
	@GetMapping(path = "/owners/{ownerId}/delete")
	public String eliminarOwner(@PathVariable("ownerId") int ownerId, ModelMap model, RedirectAttributes redirectAttributes) {
		Owner owner = this.ownerService.findOwnerById(ownerId);
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());

		if (owner != null) {
			if (authority.equals("admin") || owner.getUser().equals(this.userService.getUserSession())) {
				ownerService.delete(owner);
				redirectAttributes.addFlashAttribute("message", "Owner successfully deleted!");
			}
		} else {
				redirectAttributes.addFlashAttribute("message", "Owner not found!");
		}
		return "redirect:/owners";
	}

}
