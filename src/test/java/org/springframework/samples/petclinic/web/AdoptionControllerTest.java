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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.AdoptionApplication;
import org.springframework.samples.petclinic.model.AdoptionRequest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.AdoptionApplicationService;
import org.springframework.samples.petclinic.service.AdoptionRequestService;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.web.formatters.OwnerFormatter;
import org.springframework.samples.petclinic.web.formatters.PetFormatter;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = AdoptionController.class,
includeFilters = @ComponentScan.Filter(value = {PetFormatter.class,OwnerFormatter.class}, type = FilterType.ASSIGNABLE_TYPE),
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
excludeAutoConfiguration= SecurityConfiguration.class)
class AdoptionControllerTest {
	
	private static final String TEST_USER_OWNER = "el_owner";
	private static final int TEST_PET_ID = 1;
	private static final int TEST_REQUEST_ID = 1;
	@MockBean
	private PetService petService;
	
	@MockBean
	private AdoptionRequestService adoptionRequestService;
	
	@MockBean
	private AdoptionApplicationService adoptionApplicationService;
	
	@MockBean
	private PetHotelService petHotelService;
	
	@MockBean
	private OwnerService ownerService;
	
	@MockBean
	private UserService userService;
	
	@Autowired
	private MockMvc mockMvc;
	
	private User user;
	private Optional<Pet> pet;
	private Owner owner;
	private Owner owner2;
	private Optional<User> user2;
	private Optional<AdoptionRequest> adoptionRequest;
	private User otroUser;
	private Optional<User> otroUser2;
	private AdoptionApplication adoptionApplication;
	private Optional<AdoptionApplication> adoptionApplication2;
	
	
	@BeforeEach
	void setup() {
		user = new User();
		user.setUsername(TEST_USER_OWNER);
		otroUser = new User();
		otroUser.setUsername("otra_persona");
		
		owner = new Owner();
		owner.setUser(user);
		
		owner2 = new Owner();
		owner2.setFirstName("el");
		owner2.setLastName("joker");
		
		List<Owner> owners = new ArrayList<Owner>();
		owners.add(owner);
		owners.add(owner2);
		
		Pet optional = new Pet();
		optional.setId(TEST_PET_ID);
		optional.setOwner(owner);
		
		AdoptionRequest optionalAdoptionRequest = new AdoptionRequest();
		optionalAdoptionRequest.setAdoptionApplication(new ArrayList<AdoptionApplication>());
		optionalAdoptionRequest.setId(TEST_REQUEST_ID);
		optionalAdoptionRequest.setPet(optional);
		
		adoptionApplication = new AdoptionApplication();
		adoptionApplication.setId(1);
		adoptionApplication.setAdoptionRequest(optionalAdoptionRequest);
		
		adoptionApplication2 = Optional.of(adoptionApplication);
		pet = Optional.of(optional);
		user2 = Optional.of(user);
		adoptionRequest = Optional.of(optionalAdoptionRequest);
		otroUser2 = Optional.of(otroUser);
		
		given(this.adoptionRequestService.findPetsInAdoption()).willReturn(new ArrayList<Pet>());
		given(this.userService.getUserSession()).willReturn(user);
		given(this.ownerService.findOwnerByUserUsername(this.userService.getUserSession().getUsername())).willReturn(new ArrayList<Owner>());
		given(this.adoptionRequestService.findAdoptionRequests()).willReturn(new ArrayList<AdoptionRequest>());
		given(this.petService.findById(TEST_PET_ID)).willReturn(pet);
		given(this.userService.findUser(TEST_USER_OWNER)).willReturn(user2);
		given(this.userService.findUser("otra_persona")).willReturn(otroUser2);
		given(this.userService.findAuthoritiesByUsername(TEST_USER_OWNER)).willReturn("owner");
		given(this.userService.findAuthoritiesByUsername("otra_persona")).willReturn("owner");
		given(this.adoptionRequestService.findAdoptionRequestById(TEST_REQUEST_ID)).willReturn(adoptionRequest);
		given(this.adoptionRequestService.findById(TEST_REQUEST_ID)).willReturn(adoptionRequest);
		given(this.petHotelService.findAllOwners()).willReturn(owners);
    	given(this.adoptionApplicationService.findAdoptionApplications(user.getUsername())).willReturn(new ArrayList<AdoptionApplication>());
    	given(this.adoptionApplicationService.findById(1)).willReturn(adoptionApplication2);
	}
	
    @WithMockUser(value = "el_owner")
	@Test
	void testListAdoptions() throws Exception {
		mockMvc.perform(get("/adoptions"))
				.andExpect(model().attributeExists("adoptionRequests"))
				.andExpect(model().attributeExists("currentUser"))
				.andExpect(status().isOk())
				.andExpect(view().name("adoptions/adoptionsList"));
	}	
    
    @WithMockUser(value = "el_owner")
	@Test
	void testListAdoptionApplications() throws Exception {
		mockMvc.perform(get("/adoptionApplications"))
				.andExpect(model().attributeExists("adoptionApplications"))
				.andExpect(status().isOk())
				.andExpect(view().name("adoptions/adoptionApplicationsList"));
	}	
    
    @WithMockUser(value = "el_owner")
	@Test
	void testRequestAdoptionForPet() throws Exception {
		mockMvc.perform(get("/adoptions/{petId}/new", TEST_PET_ID))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptions"));
	}	
    
    @WithMockUser(value = "el_owner")
	@Test
	void testApplyForAdoptionGetSameOwner() throws Exception {
		mockMvc.perform(get("/adoptions/{adoptionRequestId}/apply", TEST_REQUEST_ID))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptions"));
	}	
    
    @WithMockUser(value = "otra_persona")
	@Test
	void testApplyForAdoptionGetDifferentOwner() throws Exception {
		given(this.userService.getUserSession()).willReturn(otroUser);
		
		mockMvc.perform(get("/adoptions/{adoptionRequestId}/apply", TEST_REQUEST_ID))
				.andExpect(model().attributeExists("adoptionRequest"))
				.andExpect(model().attributeExists("adoptionApplication"))
				.andExpect(status().isOk())
				.andExpect(view().name("adoptions/applyForAdoptionForm"));
	}	
    
    @WithMockUser(value = "otra_persona")
	@Test
	void testApplyForAdoptionPostDifferentOwnerError() throws Exception {
		given(this.userService.getUserSession()).willReturn(otroUser);

		mockMvc.perform(post("/adoptions/{adoptionRequestId}/apply", TEST_REQUEST_ID)
				.with(csrf())
				.param("description", "holaaaaaaa"))
				.andExpect(status().isOk())
				.andExpect(view().name("adoptions/applyForAdoptionForm"));
	}	
    
    @WithMockUser(value = "otra_persona")
	@Test
	void testDeleteAdoptionApplicationError() throws Exception {
		given(this.userService.getUserSession()).willReturn(otroUser);
		
		mockMvc.perform(get("/adoptionApplications/{adoptionApplicationId}/delete", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptionApplications"));
	}	
    
    @WithMockUser(value = "el_owner")
	@Test
	void testDeleteAdoptionApplication() throws Exception {
		mockMvc.perform(get("/adoptionApplications/{adoptionApplicationId}/delete", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptionApplications"));
	}	
    
    @WithMockUser(value = "otra_persona")
	@Test
	void testDeleteAdoptionRequestError() throws Exception {
		given(this.userService.getUserSession()).willReturn(otroUser);
		
		mockMvc.perform(get("/adoptions/{adoptionRequestId}/delete", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptions"));
	}	
    
    @WithMockUser(value = "el_owner")
	@Test
	void testDeleteAdoptionRequest() throws Exception {
		mockMvc.perform(get("/adoptions/{adoptionRequestId}/delete", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/adoptions"));
	}	
    
}
