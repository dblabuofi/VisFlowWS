/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.helper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mycompany.autointerfacews.mydata.MyStatus;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author jupiter
 */
public class WebResourceImageDownloader {

    @Inject
    @Named("driver")
    WebDriver driver;

    public WebResourceImageDownloader() {
    }

    public List<String> downloadKEGGPathwayImage(List<List<String>> inputs, String location, MyStatus status) {
        List<String> outputs = new ArrayList<>();
        try {
            List<String> arrayInput = inputs.stream()
                    .map(t -> t.get(0))
                    .collect(Collectors.toList());
            String url = "http://rest.kegg.jp/get/";
            int index = 0;
            for (String path : arrayInput) {
                if (!path.startsWith("hsa")) {
                    path = "hsa" + path;
                }
                status.addMessage("downloading image for " + path);
//                                 if (index++ == 2) break;
                System.out.println("*%%%%%%%%%&&&&&&&&&&&&&&&&&&&");
                System.out.println(url + path + "/image");
                driver.get(url + path + "/image");
                WebElement img = driver.findElement(By.tagName("img"));
                String src = img.getAttribute("src");
                URL imageurl = new URL(src);
                System.out.println(imageurl);
                BufferedImage bufImgOne = ImageIO.read(imageurl);
                ImageIO.write(bufImgOne, "png", new File(location + path + ".png"));
                outputs.add(path + ".png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputs;
    }

    public List<String> downloadKEGGPathwayImageByName(List<String> fileNames, String location) {
        List<String> outputs = new ArrayList<>();
        try {
            String url = "http://rest.kegg.jp/get/";
            int index = 0;
            for (String path : fileNames) {
                if (!path.startsWith("hsa")) {
                    path = "hsa" + path;
                }
                driver.get(url + path + "/image");
                WebElement img = driver.findElement(By.tagName("img"));
                String src = img.getAttribute("src");
                URL imageurl = new URL(src);
                System.out.println(imageurl);
                BufferedImage bufImgOne = ImageIO.read(imageurl);
                ImageIO.write(bufImgOne, "png", new File(location + path + ".png"));
                outputs.add(path + ".png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputs;
    }

    public List<String> download(String fileName, String location, MyStatus status) {

        List<List<String>> inputs = MyFileReader.readFile(fileName, " ");
        List<String> outputs = new ArrayList<>();
        try {
            List<String> arrayInput = inputs.stream()
                    .map(t -> t.get(0))
                    .collect(Collectors.toList());
            String url = "http://rest.kegg.jp/get/";
            int index = 0;
            for (String path : arrayInput) {
                status.addMessage("downloading image for " + path);
//                                 if (index++ == 1) break;
                path = path.replace("path:", "");
                driver.get(url + path + "/image");
                WebElement img = driver.findElement(By.tagName("img"));
                String src = img.getAttribute("src");
                URL imageurl = new URL(src);
                System.out.println(imageurl);
                BufferedImage bufImgOne = ImageIO.read(imageurl);
                ImageIO.write(bufImgOne, "png", new File(location + path + ".png"));
                outputs.add(path + ".png");
            }

        } catch (Exception e) {

        }
        return outputs;
    }

    public void download(String url, String fileUrl) {
        try {
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(10000, TimeUnit.SECONDS);
            WebElement img = driver.findElement(By.tagName("img"));
            String src = img.getAttribute("src");
            URL imageurl = new URL(src);
            System.out.println(imageurl);
            BufferedImage bufImgOne = ImageIO.read(imageurl);
            ImageIO.write(bufImgOne, "png", new File(fileUrl));
        } catch (Exception e) {
        }
    }

}
