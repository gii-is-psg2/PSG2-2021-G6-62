package org.springframework.samples.petclinic.model; 
 
import java.time.LocalDate; 
 
import javax.persistence.CascadeType; 
import javax.persistence.Column; 
import javax.persistence.Entity; 
import javax.persistence.JoinColumn; 
import javax.persistence.ManyToOne; 
import javax.persistence.OneToOne; 
import javax.persistence.Table; 
import javax.validation.constraints.NotEmpty; 
import javax.validation.constraints.NotNull; 
 
import org.springframework.format.annotation.DateTimeFormat; 
 
@Entity 
@Table(name = "donations") 
public class Donation extends BaseEntity{ 
	 
	@Column(name = "donation_date") 
	@DateTimeFormat(pattern = "yyyy/MM/dd") 
	private LocalDate date; 
	 
	@Column(name = "amount") 
	@NotNull 
	private Double amount ; 
 
	@Column(name = "user_name") 
	private String userName; 
	 
	@ManyToOne 
	@JoinColumn(name = "cause_id") 
	private Cause cause; 
 
	public LocalDate getDate() { 
		return date; 
	} 
 
	public void setDate(LocalDate date) { 
		this.date = date; 
	} 
 
	public Double getAmount() { 
		return amount; 
	} 
 
	public void setAmount(Double amount) { 
		this.amount = amount; 
	} 
 
	public Cause getCause() { 
		return cause; 
	} 
 
	public void setCause(Cause cause) { 
		this.cause = cause; 
	} 
 
	public String getUserName() { 
		return userName; 
	} 
 
	public void setUserName(String userName) { 
		this.userName = userName; 
	} 
 
	 
} 