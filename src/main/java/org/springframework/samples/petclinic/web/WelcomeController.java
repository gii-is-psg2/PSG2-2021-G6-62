package org.springframework.samples.petclinic.web;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
	
	
	  @GetMapping({"/","/welcome"})
	  public String welcome(Map<String, Object> model) {	    
		Object nombreOwner= SecurityContextHolder.getContext().getAuthentication().getName();
		model.put("nombre", nombreOwner);
	    return "welcome";
	  }
}
