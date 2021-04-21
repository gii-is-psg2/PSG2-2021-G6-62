package org.springframework.samples.petclinic.service;


import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Cause;
import org.springframework.samples.petclinic.repository.CauseRepository;
import org.springframework.samples.petclinic.service.exceptions.WrongTargetException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CauseService {

	private CauseRepository causeRepository;

	public CauseService(CauseRepository causeRepository) {
		super();
		this.causeRepository = causeRepository;
	}
	
	@Transactional(readOnly = true)
	public Cause findCausesById(int id) throws DataAccessException {
		return causeRepository.findById(id).get();
	}
	
	@Transactional(rollbackFor = WrongTargetException.class)
	public void saveCauses(Cause cause) throws DataAccessException, WrongTargetException {
		if(cause.getTarget()<0.01) {
			throw new WrongTargetException();
		}else {
			causeRepository.save(cause);
		}
		
	}
	
	@Transactional
	public List<Cause> findAllCauses() throws DataAccessException {
		return (List<Cause>) causeRepository.findAll();
	}
	
}
