package com.honda.olympus.ms.movfile_mftp.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message 
{
	
	@NotNull(message = "{message.status}")
	@Min(value = 0, message = "{message.status}")
	@Max(value = Short.MAX_VALUE, message = "{message.status}")
	private Integer status; 
	
	@NotBlank(message = "{message.msg}")
	private String msg; 
	
	private String file;
	
}