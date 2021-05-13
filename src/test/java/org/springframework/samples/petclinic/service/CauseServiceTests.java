package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.service.exceptions.WrongTargetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class CauseServiceTests {

	@Autowired
	protected CauseService causeService;

	@Test
	void findCauseById() {
		Cause c = this.causeService.findCausesById(1);
		assertThat(c.getId()).isEqualTo(1);
	}
	
	@Test
	void findAllCauses() {
		List<Cause> cList = this.causeService.findAllCauses();
		assertThat(cList).isNotNull();
		assertThat(cList.size()).isNotZero();
	}
	
	@Test
	@Transactional
	void shouldInsertCause() {
		List<Cause> oldCauses = this.causeService.findAllCauses();
		int oldCausesNum = oldCauses.size();

		Cause c = new Cause();
		c.setDescription("Esto es un testeo");
		c.setOrganization("Mi cartera");
		c.setTarget(888.0);
		
		try {
			this.causeService.saveCauses(c);
		} catch (DataAccessException | WrongTargetException e) {
			e.printStackTrace();
		}
		
		List<Cause> newCauses = this.causeService.findAllCauses();
		int newCausesNum = newCauses.size();

		assertThat(newCausesNum).isEqualTo(oldCausesNum + 1);
	}
	
	@Test
	@Transactional
	void shouldNotInsertCause() {
		List<Cause> oldCauses = this.causeService.findAllCauses();
		int oldCausesNum = oldCauses.size();

		Cause c = new Cause();
		c.setDescription("Esto es un testeo");
		c.setOrganization("Mi cartera");
		c.setTarget(-2121.0);
		
		Assertions.assertThrows(WrongTargetException.class, () ->{
			this.causeService.saveCauses(c);
		});		
		
		List<Cause> newCauses = this.causeService.findAllCauses();
		int newCausesNum = newCauses.size();

		assertThat(newCausesNum).isEqualTo(oldCausesNum);
	}

}
