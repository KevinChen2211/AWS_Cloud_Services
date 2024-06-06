package com.amazonaws.assignmentOne;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TaskTwo {

    public static void main(String[] args) throws Exception {
        Regions clientRegion = Regions.US_EAST_1;
        String bucketName = "s3780646-assignment1-task2";

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(clientRegion)
                    .build();

            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Creating " + bucketName + ",at location: " + bucketLocation);
            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }


        ArrayList<String>urls = getImageUrl();
        downloadImages(urls);

        try{
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();
            System.out.println("Uploading into S3 Bucket: " + bucketName);
            for (String url : urls) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                String pathToImage = "images/";
                // Upload images
                PutObjectRequest request = new PutObjectRequest(bucketName, fileName, new File(pathToImage + fileName));
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("image/jpg");
                metadata.addUserMetadata("title", fileName.substring(0, fileName.lastIndexOf('.')));
                request.setMetadata(metadata);
                s3Client.putObject(request);
            }

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getImageUrl() throws Exception{
        ArrayList<String> urls = new ArrayList<>();
        JsonParser parser = new JsonFactory().createParser(new File("a1.json"));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        JsonNode songsNode = rootNode.path("songs");

        Iterator<JsonNode> iter = songsNode.iterator();
        ObjectNode currentNode;
        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();
            urls.add(currentNode.path("img_url").asText());
        }
        Set<String> set = new HashSet<>(urls);
        ArrayList<String> result = new ArrayList<>(set);
        return result;
    }

    private static void downloadImages(ArrayList<String> urls){
        String destinationDir = "./images/";
        File directory = new File(destinationDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        System.out.println("Downloading Images locally");
        // Download images
        for (String url : urls) {
            try {
                // Extract the image file name from the URL
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                // Open a connection to the URL
                URL imageUrl = new URL(url);
                try (InputStream inputStream = imageUrl.openStream()) {
                    // Open a FileOutputStream to write the image
                    try (OutputStream outputStream = new FileOutputStream(destinationDir + fileName)) {
                        // Read from the input stream and write to the output stream
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error downloading image from " + url + ": " + e.getMessage());
            }
        }
    }
}

