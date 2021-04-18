package org.springframework.samples.petclinic.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "adoptionRequest")
public class AdoptionRequest extends BaseEntity {
	
	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "adoptionRequest")
	private List<AdoptionApplication> adoptionApplications;

	public Pet getPet() {
		return pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public List<AdoptionApplication> getAdoptionApplications() {
		return adoptionApplications;
	}

	public void setAdoptionApplication(List<AdoptionApplication> adoptionApplication) {
		this.adoptionApplications = adoptionApplication;
	}
	
	public void addAdoptionApplication(AdoptionApplication adoptionApplication) {
		this.adoptionApplications.add(adoptionApplication);
	}
}
