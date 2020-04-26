package com.spring.ms.example.currencyconversionservice.currencyconversionservice;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class CurrencyExchangeServiceFallback implements CurrencyExchangeServiceProxy{

	@Override
	public CurrencyConversionBean retrieveExchangeValueF(String from, String to) {
		
		 return new CurrencyConversionBean(null,"USD","INR",new BigDecimal(65),null,null,7000);
	}

	
}
