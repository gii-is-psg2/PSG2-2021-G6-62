package org.springframework.samples.petclinic.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.VetService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers=VetController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
		excludeAutoConfiguration= SecurityConfiguration.class)
class VetControllerTests {
	
	private static final Integer TEST_VET_ID = 1;
	private static final Integer TEST_VET_FAKE_ID = -1;
	private static final Integer TEST_VET_NOT_FOUND_ID = 2;

	@MockBean
	private VetService vetService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() {

		Vet james = new Vet();
		james.setFirstName("James");
		james.setLastName("Carter");
		james.setId(1);
		Vet helen = new Vet();
		helen.setFirstName("Helen");
		helen.setLastName("Leary");
		helen.setId(2);
		Specialty radiology = new Specialty();
		radiology.setId(1);
		radiology.setName("radiology");
		helen.addSpecialty(radiology);
		
		given(this.vetService.findVets()).willReturn(Lists.newArrayList(james, helen));
		given(this.vetService.findById(TEST_VET_ID)).willReturn(Optional.of(james));
		given(this.vetService.findById(TEST_VET_NOT_FOUND_ID)).willReturn(Optional.empty());
	}
        
    @WithMockUser(value = "spring")
		@Test
	void testShowVetListHtml() throws Exception {
		mockMvc.perform(get("/vets")).andExpect(status().isOk()).andExpect(model().attributeExists("vets"))
				.andExpect(view().name("vets/vetList"));
	}	

	@WithMockUser(value = "spring")
        @Test
	void testShowVetListXml() throws Exception {
		mockMvc.perform(get("/vets.xml").accept(MediaType.APPLICATION_XML)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_XML_VALUE))
				.andExpect(content().node(hasXPath("/vets/vetList[id=1]/id")));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testCreateVetGet() throws Exception {
		mockMvc.perform(get("/vets/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("vet"))
			.andExpect(view().name("vets/vetsEdit"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testCreateVetPost() throws Exception {
		mockMvc.perform(post("/vets/new")
				.with(csrf())
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", is("Vet created succesfully!")))
			.andExpect(view().name("redirect:/vets"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testCreateWrongVetPost() throws Exception {
		mockMvc.perform(post("/vets/new")
				.with(csrf())
				.param("firstName", "")
				.param("lastName", ""))
		.andExpect(status().isOk())
		.andExpect(model().attributeHasErrors("vet"))
		.andExpect(model().attributeHasFieldErrors("vet", "firstName"))
		.andExpect(model().attributeHasFieldErrors("vet", "lastName"))
		.andExpect(view().name("vets/vetsEdit"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testEditVetGet() throws Exception {
		mockMvc.perform(get("/vets/{vetId}/edit", TEST_VET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("vet"))
			.andExpect(view().name("vets/vetsEdit"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testEditVetPost() throws Exception {
		mockMvc.perform(post("/vets/{vetId}/edit", TEST_VET_ID)
				.with(csrf())
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", is("Vet successfully updated!")))
			.andExpect(view().name("redirect:/vets"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testEditWrongVetPost() throws Exception {
		mockMvc.perform(post("/vets/{vetId}/edit", TEST_VET_FAKE_ID)
				.with(csrf())
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().is3xxRedirection())	
			.andExpect(view().name("redirect:/vets")); 
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testEditWrongFieldVetPost() throws Exception {
		mockMvc.perform(post("/vets/{vetId}/edit", TEST_VET_ID)
				.with(csrf())
				.param("firstName", "")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("vet"))
			.andExpect(model().attributeHasFieldErrors("vet", "firstName"))
			.andExpect(view().name("vets/vetsEdit"));
			
		//OJO, REVISALO FELIPE
	}

	@WithMockUser(value = "spring")
	@Test
	void testDeleteVet() throws Exception {
		
		mockMvc.perform(get("/vets/{vetId}/delete", TEST_VET_ID)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", is("Vet successfully deleted!")))
			.andExpect(view().name("redirect:/vets"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testDeleteFakeVet() throws Exception {
		
		mockMvc.perform(get("/vets/{vetId}/delete", TEST_VET_FAKE_ID)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", is("Vet not found!")))
			.andExpect(view().name("redirect:/vets"));
	}
}
