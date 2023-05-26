package com.honda.olympus.ms.movfile_mftp.client;

import java.net.MalformedURLException;
import java.net.URL;

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
	
	
	public LogEventClient(
		@Value("${ms.logevent.protocol}") String protocol, 
		@Value("${ms.logevent.host}") String host, 
		@Value("${ms.logevent.port}") int port, 
		@Value("${ms.logevent.path}") String path) throws MalformedURLException 
	{
		url = new URL(protocol, host, port, path).toString();
		log.info("# ms.logevent url: {}", url);
	}
	
	
	public ResponseEntity<String> logEvent(Event event) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers);
		return restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
