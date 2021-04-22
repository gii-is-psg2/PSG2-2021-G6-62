package org.springframework.samples.petclinic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "cause")
public class Cause extends BaseEntity{
	
	@Column(name = "organization")
	@NotEmpty
	private String organization;
	
	@Column(name = "description")
	@NotEmpty
	private String description;
	
	@Column(name = "target")
	private double target;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}


	@Override
	public String toString() {
		return "Cause [organization=" + organization + ", description=" + description + ", target=" + target
				+ "]";
	}
	
	

}
