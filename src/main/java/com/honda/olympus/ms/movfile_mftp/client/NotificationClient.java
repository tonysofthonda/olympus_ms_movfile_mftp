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
public class NotificationClient 
{
	
	private String url;                                    
	
	
	public NotificationClient(
		@Value("${ms.notification.protocol}") String protocol, 
		@Value("${ms.notification.host}") String host, 
		@Value("${ms.notification.port}") int port,
		@Value("${ms.notification.path}") String path) throws MalformedURLException 
	{
		url = new URL(protocol, host, port, path).toString();
		log.info("# ms.notification url: {}", url);
	}
	
	
	public ResponseEntity<String> sendNotification(Event event) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
