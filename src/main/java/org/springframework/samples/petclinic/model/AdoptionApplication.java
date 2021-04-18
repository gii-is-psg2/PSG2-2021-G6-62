package org.springframework.samples.petclinic.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "adoptionApplication")
public class AdoptionApplication extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "adoptionRequest_id")
	private AdoptionRequest adoptionRequest;
	
	@NotBlank
	private String description;
	
	@OneToOne
	@NotNull
	private Owner owner;

	public AdoptionRequest getAdoptionRequest() {
		return adoptionRequest;
	}

	public void setAdoptionRequest(AdoptionRequest adoptionRequest) {
		this.adoptionRequest = adoptionRequest;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "AdoptionApplication [adoptionRequest=" + adoptionRequest + ", description=" + description + ", owner="
				+ owner + "]";
	}
	
}
