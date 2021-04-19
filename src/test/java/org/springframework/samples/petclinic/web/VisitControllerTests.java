package org.springframework.samples.petclinic.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
@WebMvcTest(controllers = VisitController.class,
			excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
			excludeAutoConfiguration= SecurityConfiguration.class)
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final int TEST_PET_ID = 1;
	private static final int TEST_VISIT_ID = 1;
	private static final String TEST_USER_ADMIN = "el_admin";
	private static final String TEST_USER_OWNER = "el_owner";


	@MockBean
	private PetService clinicService;
	@MockBean
	private OwnerService ownerService;
	@MockBean
	private UserService userService;

	@Autowired
	private MockMvc mockMvc;
	
	private Owner george;
	private User user;

	@BeforeEach
	void setup() {
		
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		user = new User();
		user.setUsername("spring");
		george.setUser(user);
		
		given(this.ownerService.findOwnerById(TEST_OWNER_ID)).willReturn(george);
		
		given(this.userService.findAuthoritiesByUsername(TEST_USER_ADMIN)).willReturn("admin");
		given(this.userService.findAuthoritiesByUsername(TEST_USER_OWNER)).willReturn("owner");
		
		given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(new Pet());
		given(this.clinicService.findVisitById(TEST_VISIT_ID)).willReturn(Optional.of(new Visit()));
	}

        @WithMockUser(value = "spring")
        @Test
	void testInitNewVisitForm() throws Exception {
        	user.setUsername(TEST_USER_ADMIN);
    		given(this.userService.getUserSession()).willReturn(user);
    		
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@WithMockUser(value = "spring")
        @Test
	void testProcessNewVisitFormSuccess() throws Exception {
		user.setUsername(TEST_USER_ADMIN);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name", "George")
							.with(csrf())
							.param("description", "Visit Description"))                                
                .andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@WithMockUser(value = "spring")
        @Test
	void testProcessNewVisitFormHasErrors() throws Exception {
		user.setUsername(TEST_USER_ADMIN);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
							.with(csrf())
							.param("name", "George"))
				.andExpect(model().attributeHasErrors("visit")).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

// Metodo comentado en el controlador porque no se usa
	
//	@WithMockUser(value = "spring")
//        @Test
//	void testShowVisits() throws Exception {
//		user.setUsername(TEST_USER_ADMIN);
//		given(this.userService.getUserSession()).willReturn(user);
//		
//		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)).andExpect(status().isOk())
//				.andExpect(model().attributeExists("visits")).andExpect(view().name("visitList"));
//	}
	
	@WithMockUser(value = "spring")
	@Test
	void testDeleteVisit() throws Exception {
		user.setUsername(TEST_USER_ADMIN);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/{visitId}/delete", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attribute("message", is("Visit successfully deleted!")))
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

}
