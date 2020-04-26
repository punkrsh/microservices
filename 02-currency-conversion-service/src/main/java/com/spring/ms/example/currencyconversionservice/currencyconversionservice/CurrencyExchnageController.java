package com.spring.ms.example.currencyconversionservice.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class CurrencyExchnageController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CurrencyExchangeServiceProxy feignProxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity){
		
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
 
        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity
        		("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
        				CurrencyConversionBean.class, uriVariables);
 
        CurrencyConversionBean response = responseEntity.getBody();
 
        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());

	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	@HystrixCommand(fallbackMethod="fallbackConvertCurrencyFeign")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity){
		
	   CurrencyConversionBean response = feignProxy.retrieveExchangeValueF(from, to);
 
       logger.info("{}",response);
       
       BigDecimal total_amt =  quantity.multiply(response.getConversionMultiple());
       
       //throw new RuntimeException("Not available");
       return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
    		   total_amt, response.getPort());

	}
	
	public CurrencyConversionBean fallbackConvertCurrencyFeign(@PathVariable String from, 
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		
		logger.info("In fallaback...");
		
		CurrencyConversionBean response = feignProxy.retrieveExchangeValueF(from, to);
		 
		BigDecimal total_amt =  quantity.multiply(response.getConversionMultiple());
	       
		return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
	    		   total_amt, 80);
	}
	
	
}
