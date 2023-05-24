package com.honda.olympus.ms.movfile_mftp.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.ms.movfile_mftp.domain.Event;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class LogEventClient 
{
	
	private String url; 
	
	
	public LogEventClient(@Value("${ms.logevent.url}") String url) {
		this.url = url;
		log.info("# ms.logevent url: {}", url);
	}
	
	
	public ResponseEntity<String> logEvent(Event event) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
