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
import org.springframework.samples.petclinic.service.CauseService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers=CauseController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
		excludeAutoConfiguration= SecurityConfiguration.class)
class CauseControllerTests {

	@MockBean
	private CauseService causeService;

	@Autowired
	private MockMvc mockMvc;

	private List<Cause> causas;

	@BeforeEach
	void setup() {
		causas = new ArrayList<Cause>();
		
		given(this.causeService.findAllCauses()).willReturn(causas);
	}

	@WithMockUser(value = "spring")
    @Test
	void testListCauses() throws Exception {
		mockMvc.perform(get("/cause"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("causes"))
				.andExpect(view().name("cause/listCauses"));
	}
	
	@WithMockUser(value = "spring")
    @Test
	void testNewCause() throws Exception {
		mockMvc.perform(get("/cause/new"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("cause"))
				.andExpect(view().name("cause/newCause"));
	}
	
	@WithMockUser(value = "spring")
    @Test
    void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/cause/save")
						.with(csrf())
						.param("organization", "micartera.com")
						.param("description", "necesito comprarme el chalet")
						.param("target", "100.0"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/cause"));
		
	}
	
	@WithMockUser(value = "spring")
    @Test
    void testProcessCreationFormError() throws Exception {
		mockMvc.perform(post("/cause/save")
						.with(csrf())
						.param("organization", "")
						.param("description", "necesito comprarme el chalet")
						.param("target", "100.0"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("cause"))
			.andExpect(model().attributeHasFieldErrors("cause", "organization"))
			.andExpect(view().name("cause/newCause"));
	}
}
