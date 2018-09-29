package edu.cornell.ncrn.ced2ar.api.data;

import org.springframework.web.multipart.MultipartFile;

/**
 *File upload helper class
 * 
 *@author Cornell University, Copyright 2012-2015
 *@author Ben Perry
 *
 *@author Cornell Institute for Social and Economic Research
 *@author Cornell Labor Dynamics Institute
 *@author NCRN Project Team 
 */
public class FileUpload {

	private MultipartFile files;

	/**
	 * Get the file
	 * @return the multipart file
	 */
	public MultipartFile getFile() {
		return files;
	}

	/**
	 * Set the file
	 * @param f the multipart file
	 */
	public void setFile(MultipartFile f) {
		this.files = f;
	}
}