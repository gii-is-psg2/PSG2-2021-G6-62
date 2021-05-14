package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.configuration.SecurityConfiguration;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetHotel;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.PetHotelRepository;
import org.springframework.samples.petclinic.service.PetHotelService;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.samples.petclinic.web.formatters.PetFormatter;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = PetHotelController.class,
includeFilters = @ComponentScan.Filter(value = PetFormatter.class, type = FilterType.ASSIGNABLE_TYPE),
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
excludeAutoConfiguration= SecurityConfiguration.class)
class PetHotelControllerTest {
	
	private static final String TEST_NOMBRE = "spring";
	private static final String TEST_HACKER = "un_hacker_peligroso";
	private static final String TEST_USER_ADMIN = "el_admin";
	private static final String TEST_USER_OWNER = "el_owner";
	
	@MockBean
	private PetHotelService petHotelService;
	
	@MockBean
	private PetHotelRepository petHotelRepository;
	
	@MockBean
	private UserService userService;
	
	@Autowired
	private MockMvc mockMvc;
	
	private Pet pet;
	private Owner owner;
	private User user;
	private PetHotel petHotel;
	
	@BeforeEach
	void setup() {
		pet = Mockito.mock(Pet.class);
		petHotel = Mockito.mock(PetHotel.class);
		
		given(this.userService.findAuthoritiesByUsername(TEST_USER_ADMIN)).willReturn("admin");
		given(this.userService.findAuthoritiesByUsername(TEST_USER_OWNER)).willReturn("owner");
		given(this.userService.findAuthoritiesByUsername(TEST_HACKER)).willReturn("owner");
		
		given(this.petHotelService.findAllBookings()).willReturn(new ArrayList<PetHotel>());
		
		given(this.petHotelService.findPetsByUser(TEST_USER_OWNER)).willReturn(new ArrayList<Pet>());
		
		owner = new Owner();
		user = new User();
		user.setUsername("spring");
		owner.setUser(user);
	
		given(this.petHotelService.bookingsOfPersonsWithUserName(TEST_NOMBRE)).willReturn(new ArrayList<PetHotel>());
		given(this.petHotelService.findAllPets()).willReturn(Lists.newArrayList(pet));

		given(pet.getOwner()).willReturn(owner);
		given(pet.getName()).willReturn("Pipas_G");
		
		given(petHotel.getPet()).willReturn(pet);
	}
	//listar para owner
    @WithMockUser(value = "el_owner")
	@Test
	void testShowPetHotelListOwner() throws Exception {
    	user.setUsername(TEST_USER_OWNER);
    	given(this.userService.getUserSession()).willReturn(user);
    	
		mockMvc.perform(get("/pethotel"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/pethotel/"+TEST_USER_OWNER));
	}	
    
    //listar para admin
    @WithMockUser(value = "el_admin")
	@Test
	void testShowPetHotelListAdmin() throws Exception {
    	user.setUsername(TEST_USER_ADMIN);
    	given(this.userService.getUserSession()).willReturn(user);
    	
		mockMvc.perform(get("/pethotel"))
				.andExpect(status().isOk())
				.andExpect(view().name("hotel/listPetHotel"));
	}	
	
    @WithMockUser(value = "spring")
	@Test
	void testShowPetHotelListOfOwner() throws Exception {
    	
    	
		mockMvc.perform(get("/pethotel/{nombre}", TEST_NOMBRE))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("petHotel"))
				.andExpect(view().name("hotel/listPetHotel"));
	}	
    
    @WithMockUser(value = TEST_HACKER)
	@Test
	void testShowPetHotelListOfOtherOwner() throws Exception {
    	user.setUsername(TEST_USER_ADMIN);
		mockMvc.perform(get("/pethotel/{nombre}", TEST_NOMBRE))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/pethotel/"+TEST_HACKER));
	}	
	
	@WithMockUser(value = TEST_USER_ADMIN)
    @Test
    void testInitCreationFormAdmin() throws Exception {
		user.setUsername(TEST_USER_ADMIN);
		given(this.userService.getUserSession()).willReturn(user);
		mockMvc.perform(get("/pethotel/{nombre}/new", TEST_NOMBRE))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"))
			.andExpect(model().attributeExists("pets"))
			.andExpect(model().attributeExists("nombre"))
			.andExpect(model().attributeExists("petHotel"));
	}
	
	@WithMockUser(value = TEST_USER_OWNER)
    @Test
    void testInitCreationFormOwner() throws Exception {
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		mockMvc.perform(get("/pethotel/{nombre}/new", TEST_USER_OWNER))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"))
			.andExpect(model().attributeExists("nombre"))
			.andExpect(model().attributeExists("petHotel"));
	}
	
	@WithMockUser(value = TEST_HACKER)
    @Test
    void testInitCreationFormOtherOwner() throws Exception {
		user.setUsername(TEST_HACKER);
		given(this.userService.getUserSession()).willReturn(user);
		mockMvc.perform(get("/pethotel/{nombre}/new", TEST_NOMBRE))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/pethotel/"+ TEST_HACKER +"/new"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessPetHotelCreationForm() throws Exception {
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("description", "Es muy calladito")
				.param("pet", "Pipas_G")
				.param("userName", "spring")
				.param("startDate","2021/11/01")
				.param("endDate", "2021/11/02")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/pethotel/"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessPetHotelCreationFormNonExistentPet() throws Exception {
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("userName", "spring")
				.param("description", "")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessPetHotelCreationFormValidatorErrorEndDate() throws Exception {
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("description", "Es muy calladito")
				.param("pet", "Pipas_G")
				.param("userName", "spring")
				.param("startDate","2021/11/01")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"));
		
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("description", "Es muy calladito")
				.param("pet", "Pipas_G")
				.param("userName", "spring")
				.param("startDate","2000/11/01")
				.param("endDate", "2021/11/02")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"));
		
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("description", "Es muy calladito")
				.param("pet", "Pipas_G")
				.param("userName", "spring")
				.param("startDate","2099/11/01")
				.param("endDate", "2021/11/02")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testProcessPetHotelCreationFormValidatorErrorPetNull() throws Exception {
		user.setUsername(TEST_USER_OWNER);
		given(this.userService.getUserSession()).willReturn(user);
		given(petHotel.getPet()).willReturn(null);
		
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("description", "Es muy calladito")
				.param("userName", "spring")
				.param("startDate","2021/11/01")
				.param("endDate", "2021/11/02")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().isOk())
			.andExpect(view().name("hotel/createOrUpdateHotelForm"));
	}
	
	@WithMockUser(value = TEST_HACKER)
	@Test
	void testProcessPetHotelCreationFormWrongOwner() throws Exception {
		
		user.setUsername(TEST_HACKER);
		given(this.userService.getUserSession()).willReturn(user);
		
		mockMvc.perform(post("/pethotel/save")
				.with(csrf())
				.param("userName", "spring")
				.param("description", "Es muy calladito")
				.param("pet", "Pipas_G")
				.param("startDate","2022/01/01")
				.param("endDate", "2022/01/02")
				.param("firstName", "testFirstName")
				.param("lastName", "testLastName"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/pethotel/" + TEST_HACKER));
	}
}
