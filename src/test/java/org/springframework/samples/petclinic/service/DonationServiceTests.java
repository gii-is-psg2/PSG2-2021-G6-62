package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.model.Donation;
import org.springframework.stereotype.Service;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class DonationServiceTests {

	@Autowired
	protected DonationService donationService;

	@Test
	void shouldGetDonationsOfCause() {
		List<Donation> lDonation = this.donationService.getDonationsOfCause(1);
		assertThat(lDonation).isNotNull();
		assertThat(lDonation.size()).isNotZero();
	}

	@Test
	void shouldNotGetDonationsOfCause() {
		List<Donation> lDonation = this.donationService.getDonationsOfCause(-21);
		assertThat(lDonation).isNotNull();
		assertThat(lDonation.size()).isZero();
	}
	
	@Test
	void shouldGetCantidadAcumuladaEnCausa() {
		Double acum = this.donationService.cantidadAcumuladaEnCausa(1);
		assertThat(acum).isNotZero();
	}
	
	@Test
	void shouldNotGetCantidadAcumuladaEnCausa() {
		Double acum = this.donationService.cantidadAcumuladaEnCausa(-211);
		assertThat(acum).isZero();
	}
	
	@Test
	void shouldCreateDonation() {
		Donation aux = new Donation();
		Cause cause = new Cause();
		
		Donation d = this.donationService.creaDonacion(aux, "test", cause);
		
		assertThat(d.getUserName()).isEqualTo("test");
	}
	
	@Test
	void shouldSaveDonation() {
		Donation donation = new Donation();
		donation.setAmount(100.0);
		
		this.donationService.saveDonation(donation, 100.0);
		
		assertThat(donation.getId()).isNotNull();
	}
	
	@Test
	void shouldSaveDonationExceedingTarget() {
		Donation donation = new Donation();
		donation.setAmount(100.0);
		
		this.donationService.saveDonation(donation, 10.0);
		
		assertThat(donation.getId()).isNotNull();
		assertThat(donation.getAmount()).isEqualTo(10.0);
	}

}
