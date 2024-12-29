package com.app.happytails.utils;

import android.net.Uri;

import com.app.happytails.utils.model.GalleryImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageContent {

    static final List<GalleryImage> list = new ArrayList<>();

    public static void loadImages(File file){
        GalleryImage image = new GalleryImage();
        image.picUri = Uri.fromFile(file);
        addImage(image);
    }

    private static void addImage(GalleryImage images){
        list.add(0, images);
    }
    public static void loadSavedImages(File directory){
        list.clear();
        if(directory.exists()){
            File[] files = directory.listFiles();

            for (File file: files){
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));

                if (extension.equals(".jpg") || extension.equals(".png")){
                    loadImages(file);
                }
            }
        }
    }
}
