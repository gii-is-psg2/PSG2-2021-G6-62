package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.service.CauseService;
import org.springframework.samples.petclinic.service.exceptions.WrongTargetException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/cause")
public class CauseController {
	
	private CauseService causeService;
	
	private static final String TO_NEW_CAUSE = "cause/newCause";
	
	@Autowired
	public CauseController(CauseService causeService) {
		super();
		this.causeService = causeService;
	}



	@GetMapping()
	public String listCauses( Map<String, Object> model) {
		
		String vista = "cause/listCauses";
		List<Cause> causes = this.causeService.findAllCauses();
		model.put("causes", causes);
		return vista;
	}
	
	@GetMapping("/new")
	public String causeNew( Map<String, Object> model) {
		Cause cause= new Cause();
		model.put("cause", cause);
		return TO_NEW_CAUSE;
	}
	
	@PostMapping("/save")
	public String causeSave(@Valid Cause cause, BindingResult result ,Map<String, Object> model) {
		
		if(result.hasErrors()) {
			model.put("errors", result.getAllErrors());
			return TO_NEW_CAUSE;
		}else {
			try {
				this.causeService.saveCauses(cause);
			} catch (WrongTargetException e) {
				result.rejectValue("target", "notValid", "el valor no puede ser menor a 0.01");
				model.put("errors", result.getAllErrors());
				return TO_NEW_CAUSE;
			}
			
			return "redirect:/cause";
		}
		
		
	}
	

}
