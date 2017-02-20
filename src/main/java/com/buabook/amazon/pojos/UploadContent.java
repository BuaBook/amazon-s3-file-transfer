package com.buabook.amazon.pojos;

import com.google.common.base.Strings;

/**
 * <h3>Upload Content Object</h3>
 * <p>A simple object to supply file content (as {@link String}) and a file name</p>
 * (c) 2017 Sport Trades Ltd
 * 
 * @author Jaskirat M.S. Rajasansir
 * @version 2.0.0
 * @since 8 Jun 2015
 */
public class UploadContent {

	private final String fileContent;
	
	private final String fileName;
	
	
	/** @throws IllegalArgumentException If either of the supplied parameters is <code>null</code> or empty */
	public UploadContent(String fileContent, String fileName) throws IllegalArgumentException {

		if(Strings.isNullOrEmpty(fileContent))
			throw new IllegalArgumentException("No file content to upload");
		
		if(Strings.isNullOrEmpty(fileName))
			throw new IllegalArgumentException("No file name for content");
		
		this.fileContent = fileContent;
		this.fileName = fileName;
	}
	

	public String getFileContent() {
		return fileContent;
	}

	public String getFileName() {
		return fileName;
	}

}
