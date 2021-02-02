package com.sap.banking.positivepay.service.impl;

import com.sap.banking.positivepay.exception.PositivePayNotFoundException;
import com.sap.banking.positivepay.model.PositivePay;
import com.sap.banking.positivepay.persistence.PositivePayRepository;
import com.sap.banking.positivepay.service.PositivePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositivePayServiceImpl implements PositivePayService {

    @Autowired
    private PositivePayRepository positivePayRepository;

    @Override
    public List<PositivePay> getAllPositivePays() {
        return positivePayRepository.findAll();
    }

    @Override
    public void deletePositivePayRequest(Long applicationId) {
        try {
            positivePayRepository.deleteById(applicationId);
        } catch (EmptyResultDataAccessException dataAccessException) {
            throw new PositivePayNotFoundException(String.valueOf(applicationId), dataAccessException);
        }
    }

    @Override
    public PositivePay savePositivePayRequest(PositivePay positivePay) {
        return positivePayRepository.save(positivePay);
    }

    @Override
    public Optional<PositivePay> getPositivePayById(Long positivePayId) {
        return positivePayRepository.findById(positivePayId);
    }

}
