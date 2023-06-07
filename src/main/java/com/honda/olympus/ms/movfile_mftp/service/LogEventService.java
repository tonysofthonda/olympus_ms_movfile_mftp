package com.honda.olympus.ms.movfile_mftp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.honda.olympus.ms.movfile_mftp.client.LogEventClient;
import com.honda.olympus.ms.movfile_mftp.domain.Event;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class LogEventService 
{
	
	@Autowired
	private LogEventClient logEventClient;
	
	
	public HttpStatus logEvent(Event event) {
		try {
			ResponseEntity<String> response = this.logEventClient.logEvent(event);
			return response.getStatusCode();
		}
		catch (HttpClientErrorException exception) {
			log.error("### LogEvent request error: {}", exception.getResponseBodyAsString());
			return exception.getStatusCode();
		}
		catch (RestClientException exception) {
			log.error("### Unable to connect to the LogEvent endpoint !");
			return null;
		}
	}
	
}
