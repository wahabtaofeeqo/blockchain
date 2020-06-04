/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import jdk.nashorn.internal.parser.JSONParser;

/**
 *
 * @author user
 */
public class Block {
    
    private int index;
    private long timestamp;
    private String data;
    
    private int nonce;
    public String previousHash;
    public String currentHash;
    
    Block(String data, String hash) {
      
        this.data = data;
        this.previousHash = hash;
        this.timestamp = new Date().getTime();
        this.currentHash = this.calculateHash();
    }
    
    public String calculateHash() {
       
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String s = Long.toString(this.timestamp) + Integer.toString(nonce) + data + this.previousHash;
            
            StringBuilder sb = new StringBuilder();
            
            byte bytes[] = md.digest(s.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xff & bytes[i]);
               
                sb.append(hex);
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void mineBlock(int difficulty) {
    
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!this.currentHash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.currentHash = this.calculateHash();
        }
        
        System.out.println("New Block Mined!");
    }
}