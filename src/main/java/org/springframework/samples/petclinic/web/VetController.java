package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VetController {

	private final VetService vetService;

	@Autowired
	public VetController(VetService clinicService) {
		this.vetService = clinicService;
	}
	
	@ModelAttribute("specialties")
	public List<Specialty> getAllSpecialties() {
		return this.vetService.getAllSpecialties();
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping(value = { "/vets" })
	public String showVetList(Map<String, Object> model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetService.findVets());
		model.put("vets", vets);
		return "vets/vetList";
	}

	@GetMapping(value = { "/vets.xml"})
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetService.findVets());
		return vets;
	}
	
	@GetMapping(path="/vets/new")
	public String nuevoVetGet(ModelMap model) {	
		model.addAttribute("vet", new Vet());
		return "vets/vetsEdit";
	}
	
	@PostMapping(path="/vets/new")
	public String nuevoVetPost(@Valid Vet vet, BindingResult results, ModelMap model, RedirectAttributes redirectAttributes) {	
		if (results.hasErrors()) {
			model.addAttribute("errors", results.getAllErrors());
			return "vets/vetsEdit";
		} else {
			this.vetService.save(vet);
			redirectAttributes.addFlashAttribute("message","Vet created succesfully!");
		}
		
		return "redirect:/vets";
	}
	
	@GetMapping(path="/vets/{vetId}/edit")
	public String editarVet(@PathVariable("vetId") int vetId, ModelMap model) {
		Optional<Vet> vet = this.vetService.findById(vetId);
		model.put("vet", vet.get());
		return "vets/vetsEdit";
	}

	@PostMapping(path="/vets/{vetId}/edit")
	public String editarVetPost(@PathVariable("vetId") int vetId, @Valid Vet vet, BindingResult result, ModelMap model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "vets/vetsEdit";
		} else {
			vet.setId(vetId);
			model.put("vet", vet);
			this.vetService.save(vet);
			redirectAttributes.addFlashAttribute("message", "Vet successfully updated!");
			return "redirect:/vets";
		}
	}
	
	
	@GetMapping(path = "/vets/{vetId}/delete")
	public String eliminarVet(@PathVariable("vetId") int vetId, ModelMap model, RedirectAttributes redirectAttributes) {
		Optional<Vet> vet = this.vetService.findById(vetId);
		if (vet.isPresent()) {
			vetService.delete(vet.get());
			redirectAttributes.addFlashAttribute("message", "Vet successfully deleted!");
		} else {
			redirectAttributes.addFlashAttribute("message", "Vet not found!");
		}

		return "redirect:/vets";
	}

}
