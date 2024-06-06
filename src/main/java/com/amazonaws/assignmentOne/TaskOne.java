// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache License, Version 2.0.

package com.amazonaws.assignmentOne;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TaskOne {

    static boolean addDataUser = true;
    static boolean addDataMusic = true;

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider("default"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        /**
         * Create user table and data
         */
        String tableName = "login";

        // Creating table
        try {
            System.out.println("Attempting to create " + tableName + " table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("email", KeyType.HASH)), // Partition key
                    Arrays.asList(new AttributeDefinition("email", ScalarAttributeType.S)), // String
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        } catch (Exception e) {
            addDataUser = false;
            System.err.println("Unable to create " + tableName + " table: ");
            System.err.println(e.getMessage());
        }

        /**
         * Create Music table and data
         */
        Table table = dynamoDB.getTable("login");
        JsonParser parser = new JsonFactory().createParser(new File("user.json"));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;

        if(addDataUser) {
            // adding data
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next();

                String email = currentNode.path("email").asText();
                String user_name = currentNode.path("user_name").asText();
                String password = currentNode.path("password").asText();

                try {
                    table.putItem(new Item().withPrimaryKey("email", email, "user_name", user_name)
                            .withString("password", password));
                    System.out.println("PutItem succeeded: " + email + " " + user_name);

                } catch (Exception e) {
                    System.out.println("Unable to add movie: " + email + " " + user_name);
                    System.err.println(e.getMessage());
                    break;
                }
            }
            parser.close();
        }

        /**
         * Create music table
         */
        tableName = "music";
        try {
            System.out.println("Attempting to create " + tableName + " table; please wait...");
            Table musicTable = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("web_url", KeyType.HASH)), // Partition key
                    Arrays.asList(new AttributeDefinition("web_url", ScalarAttributeType.S)), // String
                    new ProvisionedThroughput(10L, 10L));
            musicTable.waitForActive();
            System.out.println("Success.  Table status: " + musicTable.getDescription().getTableStatus());

        } catch (Exception e) {
            addDataMusic = false;
            System.err.println("Unable to create " + tableName + " table: ");
            System.err.println(e.getMessage());
        }
        /**
         * adding music data to table
         */
        table = dynamoDB.getTable("music");
        parser = new JsonFactory().createParser(new File("a1.json"));

        rootNode = new ObjectMapper().readTree(parser);
        JsonNode songsNode = rootNode.path("songs");

        iter = songsNode.iterator();
        if (addDataMusic) {
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next();

                String title = currentNode.path("title").asText();
                String artist = currentNode.path("artist").asText();
                String year = currentNode.path("year").asText();
                String web_url = currentNode.path("web_url").asText();
                String image_url = currentNode.path("img_url").asText();

                try {
                    table.putItem(new Item().withPrimaryKey("web_url", web_url)
                            .withString("title", title)
                            .withString("artist", artist)
                            .withString("year", year)
                            .withString("image_url", image_url));
                    System.out.println("PutItem succeeded: " + title + " " + artist + " " + year);

                } catch (Exception e) {
                    System.out.println("Unable to add music: " + title + " " + artist + " " + year);
                    System.err.println(e.getMessage());
                    break;
                }
            }
            parser.close();
        }
        // Create user subscriptions
        tableName = "subscriptions";
        try {
            System.out.println("Attempting to create " + tableName + " table; please wait...");
            Table subscriptionTable = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("email", KeyType.HASH), // Partition key
                            new KeySchemaElement("web_url", KeyType.RANGE)), // Sort key
                    Arrays.asList(new AttributeDefinition("email", ScalarAttributeType.S), // String
                            new AttributeDefinition("web_url", ScalarAttributeType.S)), // String
                    new ProvisionedThroughput(10L, 10L));
            subscriptionTable.waitForActive();
            System.out.println("Success.  Table status: " + subscriptionTable.getDescription().getTableStatus());

        } catch (Exception e) {
            addDataMusic = false;
            System.err.println("Unable to create " + tableName + " table: ");
            System.err.println(e.getMessage());
        }

        String email = "s3780640@student.rmit.edu.au";
        String web_url = "https://raw.githubusercontent.com/davidpots/songnotes_cms/master/public/songs/78-the-tallest-man-on-earth-1904";
        String web_url2 = "https://raw.githubusercontent.com/davidpots/songnotes_cms/master/public/songs/119-dave-matthews-40";
        try {
            table = dynamoDB.getTable("subscriptions");
            table.putItem(new Item().withPrimaryKey("email", email, "web_url", web_url));
            System.out.println("PutItem succeeded: " + email + " " + web_url );

            table = dynamoDB.getTable("subscriptions");
            table.putItem(new Item().withPrimaryKey("email", email, "web_url", web_url2));
            System.out.println("PutItem succeeded: " + email + " " + web_url2 );

        } catch (Exception e) {
            System.out.println("Unable to add subscription: " + email );
            System.err.println(e.getMessage());
        }
    }
}
