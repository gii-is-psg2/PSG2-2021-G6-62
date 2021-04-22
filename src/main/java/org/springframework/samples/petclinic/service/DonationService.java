package org.springframework.samples.petclinic.service; 
 
import java.time.LocalDate; 
import java.util.ArrayList; 
import java.util.List; 
 
import org.springframework.dao.DataAccessException; 
import org.springframework.samples.petclinic.model.Cause; 
import org.springframework.samples.petclinic.model.Donation; 
import org.springframework.samples.petclinic.repository.DonationRepository; 
import org.springframework.stereotype.Service; 
 
@Service 
public class DonationService { 
 
	private DonationRepository donationRepository; 
	 
	public DonationService(DonationRepository donationRepository) { 
		super(); 
		this.donationRepository = donationRepository; 
	} 
	 
	public List<Donation> getDonationsOfCause(Integer causeId)throws DataAccessException{ 
		List<Donation> donations=(List<Donation>) donationRepository.findAll(); 
		List<Donation> res= new ArrayList<Donation>(); 
		for(Donation p: donations) { 
				if(p.getCause().getId().equals(causeId)) { 
					res.add(p); 
				} 
		} 
		return res; 
	} 
	public Double cantidadAcumuladaEnCausa(Integer causeId) { 
		Double res=0.; 
		List<Donation> donations=(List<Donation>) this.donationRepository.findAll(); 
		for(Donation p: donations) { 
			if(p.getCause().getId().equals(causeId)) { 
				 
				res+=p.getAmount(); 
			} 
		} 
		return res; 
		 
	} 
	public Donation creaDonacion(Donation donation,String username, Cause cause) { 
		donation.setCause(cause); 
		donation.setDate(LocalDate.now()); 
		donation.setUserName(username); 
		return donation; 
		 
	} 
	 
	public void saveDonation(Donation donation, Double umbral) { 
		if(donation.getAmount()>umbral) { 
			donation.setAmount(umbral); 
		} 
		donationRepository.save(donation); 
		 
	} 
	 
} 
