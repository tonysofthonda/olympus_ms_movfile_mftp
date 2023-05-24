package com.honda.olympus.ms.movfile_mftp.util;


public final class NetUtil 
{
	
	private static final String BASE_SEP = "://";
	private static final String SLASH = "/";
	
	
	private NetUtil() { }
	
	
	public static String concat(String baseUrl, String path) {
		return baseUrl + SLASH + path;
	}
	
	
	public static String fixSlashes(String url) {
		int index = url.indexOf(BASE_SEP) + BASE_SEP.length();
		return url.substring(0, index) + url.substring(index).replaceAll("/{2,}", "/");
	}
	
}

