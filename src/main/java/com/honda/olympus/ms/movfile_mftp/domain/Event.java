package com.honda.olympus.ms.movfile_mftp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event 
{
	private String source;
	private Integer status;
	private String msg;
	private String file;
}
