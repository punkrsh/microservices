package com.spring.ms.example.currencyexchangeservice;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchnageController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ExchangeValueRepository repository;

	@Autowired
	Environment enviroment;

	@PostMapping("/addConversionFactor")
	public ResponseEntity<String> addConversionFactor(@RequestBody ExchangeValue exchangeValue) {

		if (null == exchangeValue) {

			return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);
		}

		String from = exchangeValue.getFrom();
		String to = exchangeValue.getTo();

		ExchangeValue avail_exchange = repository.findByFromAndTo(from, to);

		if (null == avail_exchange) {

			repository.save(exchangeValue);

			return new ResponseEntity<>("Created successfully!", HttpStatus.CREATED);
		}

		return new ResponseEntity<>("Record already available", HttpStatus.CONFLICT);
	}

	@PutMapping("/updateConversionFactor")
	public ResponseEntity<String> updateConversionFactor(@RequestBody ExchangeValue exchangeValue) {

		if (null == exchangeValue) {

			return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);
		}

		String from = exchangeValue.getFrom();
		String to = exchangeValue.getTo();

		ExchangeValue avail_exchange = repository.findByFromAndTo(from, to);
		if (null == avail_exchange) {

			return new ResponseEntity<>("Requested entry not found!", HttpStatus.NOT_FOUND);
		}
		avail_exchange.setConversionMultiple(exchangeValue.getConversionMultiple());

		exchangeValue = repository.save(avail_exchange);

		exchangeValue.setPort(Integer.parseInt(enviroment.getProperty("local.server.port")));

		logger.info("{}", exchangeValue);

		return new ResponseEntity<>("Updated Successfully!", HttpStatus.CREATED);

	}

	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public ExchangeValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {

		ExchangeValue exchangeVlaue = repository.findByFromAndTo(from, to);

		exchangeVlaue.setPort(Integer.parseInt(enviroment.getProperty("local.server.port")));

		logger.info("{}", exchangeVlaue);

		return exchangeVlaue;

	}

}
