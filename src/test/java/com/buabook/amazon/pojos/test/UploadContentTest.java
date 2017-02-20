package com.buabook.amazon.pojos.test;

import org.junit.Test;

import com.buabook.amazon.pojos.UploadContent;

public class UploadContentTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorThrowsExceptionIfNoFileContent() {
		new UploadContent(null, "a-file-name");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructorThrowsExceptionIfNoFileName() {
		new UploadContent("some file content", null);
	}
	
}
