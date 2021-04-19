package org.springframework.samples.petclinic.web;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.service.CauseService;
import org.springframework.samples.petclinic.service.UserService;
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
	private UserService userService;
	
	
	@Autowired
	public CauseController(CauseService causeService,UserService userService) {
		super();
		this.causeService = causeService;
		this.userService = userService;
	}



	@GetMapping()
	public String listPetHotel( Map<String, Object> model) {
		
		String vista = "cause/listCauses";
		List<Cause> causes = this.causeService.findAllCauses();
		model.put("causes", causes);
		return vista;
	}
	
	@GetMapping("/new")
	public String causeNew( Map<String, Object> model) {
		
		String authority = this.userService.findAuthoritiesByUsername(this.userService.getUserSession().getUsername());
		String vista = "redirect:/cause";
		
		if(!authority.equals("owner")&&!authority.equals("admin")) {
			return vista;
		}
		
		vista = "cause/newCause";
		Cause cause= new Cause();
		model.put("cause", cause);
		return vista;
	}
	
	@PostMapping("/save")
	public String causeSave(@Valid Cause cause, BindingResult result ,Map<String, Object> model) {
		
		if(result.hasErrors()) {
			model.put("errors", result.getAllErrors());
			return "cause/newCause";
		}else {
			try {
				this.causeService.saveCauses(cause);
			} catch (WrongTargetException e) {
				result.rejectValue("target", "duplicated", "el valor no puede ser menor a 0");
				model.put("errors", result.getAllErrors());
				return "cause/newCause";
			}
			
			return "redirect:/cause";
		}
		
		
	}
	

}
