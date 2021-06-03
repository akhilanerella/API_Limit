package com.apiLimter.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.util.Duration;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class CallsperCust {
	private final Bucket bucket;
	
	 public  CallsperCust() {
	
	 Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, java.time.Duration.ofHours(1)));
     this.bucket = Bucket4j.builder()
         .addLimit(limit)
         .build();
	 }
	
	  @PostMapping(value = "/api/name/calls")
    public  ResponseEntity<String> CallsLimit(@RequestBody Dimensions dim ) {
		 ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
		 
		 if(consumptionProbe.isConsumed()){
	    	
	    	 return ResponseEntity.ok()
	    	          .header("X-Rate-Limit-Remaining", Long.toString(consumptionProbe.getRemainingTokens())).build();
	    }else
	    	
   
	    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
		        .header("X-Rate-Limit-Retry-After-Milliseconds",
		                Long.toString(TimeUnit.NANOSECONDS.toMillis(consumptionProbe.getNanosToWaitForRefill())))
		            .build(); 
	    }
	  
	 
	}


