package com.datadisplay.Tests;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.datadisplay.DataDisplay;
import com.datadisplay.ImageView;

public class TestImage {

	public static void main(String[] args) {
		
		DataDisplay dd = new DataDisplay();
		ImageView iv = dd.showImageView();
		try {
			iv.showImage(ImageIO.read(new File("cat_img.png"))); // NOTE: image not include in files
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}

}
