package org.springframework.samples.petclinic.web;

import org.springframework.samples.petclinic.service.exceptions.TestException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrashController {

	@GetMapping(value = "/oups")
	public String triggerException() throws TestException {
		throw new TestException();
	}

}
