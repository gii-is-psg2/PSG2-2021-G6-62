package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.model.Donation;
import org.springframework.samples.petclinic.service.CauseService;
import org.springframework.samples.petclinic.service.DonationService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers=DonationController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
		excludeAutoConfiguration= SecurityConfiguration.class)
class DonationControllerTests {

	private static final int TEST_CAUSE_ID = 1;
	
	@MockBean
	private CauseService causeService;
	
	@MockBean
	private DonationService donationService;

	@Autowired
	private MockMvc mockMvc;

	private Cause cause;
	private List<Donation> donaciones;

	@BeforeEach
	void setup() {
		cause = new Cause();
		new Cause();
		donaciones = new ArrayList<Donation>();
		
		cause.setTarget(150.0);
		
		given(this.donationService.getDonationsOfCause(TEST_CAUSE_ID)).willReturn(donaciones);
		given(this.donationService.cantidadAcumuladaEnCausa(TEST_CAUSE_ID)).willReturn(100.0);
		given(this.causeService.findCausesById(TEST_CAUSE_ID)).willReturn(cause);
	}

	@WithMockUser(value = "spring")
    @Test
	void testListCauses() throws Exception {
		mockMvc.perform(get("/donation/{causeId}", TEST_CAUSE_ID))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("cause"))
				.andExpect(model().attributeExists("donation"))
				.andExpect(model().attributeExists("date"))
				.andExpect(model().attributeExists("donations"))
				.andExpect(model().attributeExists("budgetAchieved"))
				.andExpect(view().name("cause/causeDetails"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testDonationNew() throws Exception {
		mockMvc.perform(post("/donation/{causeId}/save", TEST_CAUSE_ID)
					.with(csrf())
					.param("amount", "50.0")
					.param("userName", "spring")
					.param("date", "2021/05/01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/donation/" + TEST_CAUSE_ID));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testDonationNewWrongAmount() throws Exception {
		mockMvc.perform(post("/donation/{causeId}/save", TEST_CAUSE_ID)
					.with(csrf())
					.param("amount", "0.0")
					.param("userName", "spring")
					.param("date", "2021/05/01"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/donation/" + TEST_CAUSE_ID));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testDonationNewWrongDate() throws Exception {
		mockMvc.perform(post("/donation/{causeId}/save", TEST_CAUSE_ID)
					.with(csrf())
					.param("amount", "0.0")
					.param("userName", "spring")
					.param("date", ""))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/donation/" + TEST_CAUSE_ID));
	}
	
}
