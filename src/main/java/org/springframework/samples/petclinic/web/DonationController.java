package org.springframework.samples.petclinic.web;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.model.Donation;
import org.springframework.samples.petclinic.service.CauseService;
import org.springframework.samples.petclinic.service.DonationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/donation")
public class DonationController {

	private DonationService donationService;
	private CauseService causeService;

	private static final String REDIRECT_TO_DONATION = "redirect:/donation/";
	private static final String VIEW_CAUSE_DETAILS = "cause/causeDetails";
    private static final String MESSAGE = "message";

	public DonationController(DonationService donationService, CauseService causeService) {
		super();
		this.donationService = donationService;
		this.causeService = causeService;
	}



	@GetMapping("/{causeId}")
	public String causeDetails(@PathVariable("causeId") int causeId, Map<String, Object> model) {
		List<Donation> donations= this.donationService.getDonationsOfCause(causeId);
		model.put("donations", donations);

		Cause cause=this.causeService.findCausesById(causeId);
		model.put("cause", cause);


		Double budgetAchieved=this.donationService.cantidadAcumuladaEnCausa(causeId);

		model.put("budgetAchieved", budgetAchieved);

		//parte de crear donación
		Donation donation= new Donation();
		model.put("donation", donation);

		LocalDate date= LocalDate.now();
		model.put("date", date);

		return VIEW_CAUSE_DETAILS;
	}

	@PostMapping("/{causeId}/save")
	public String donationNew(@Valid Donation donation, BindingResult result, @PathVariable("causeId") int causeId , Map<String, Object> model, RedirectAttributes redirectAttributes) {
		if (donation.getAmount() == null || donation.getAmount() <= 0)
        {
            redirectAttributes.addFlashAttribute(MESSAGE , "Debe introducir una cantidad válida");
            return REDIRECT_TO_DONATION+causeId;
        }

	    if(result.hasErrors()) {
			return REDIRECT_TO_DONATION+causeId;
		}
		//create donation
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Cause cause= this.causeService.findCausesById(causeId);
		if(this.donationService.cantidadAcumuladaEnCausa(causeId) > cause.getTarget()) {
            redirectAttributes.addFlashAttribute(MESSAGE , "Debe introducir una cantidad válida");
			return REDIRECT_TO_DONATION+causeId;
		}
		Donation donRes= this.donationService.creaDonacion(donation, userName, cause);

		//save
		Double target = cause.getTarget();
		Double budgetAchieved=this.donationService.cantidadAcumuladaEnCausa(causeId);
		Double umbral= target-budgetAchieved;

		this.donationService.saveDonation(donRes,umbral);
		return REDIRECT_TO_DONATION+causeId;




	}

}
