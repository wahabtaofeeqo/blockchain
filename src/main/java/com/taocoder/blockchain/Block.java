/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import jdk.nashorn.internal.parser.JSONParser;

/**
 *
 * @author user
 */
public class Block {
    
    public int index;
    public long timestamp;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    
    private String merkleRoot;
    private int nonce;
    public String previousHash;
    public String currentHash;
    
    Block(String hash) {
      
        this.previousHash = hash;
        this.timestamp = new Date().getTime();
        
        this.currentHash = this.calculateHash();
    }
    
    public String calculateHash() {
       
        String ch = Utils.sha256(previousHash + Long.toString(timestamp) + Integer.toString(nonce) + merkleRoot);
        return ch;
    }
    
    public void mineBlock(int difficulty) {
    
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!this.currentHash.substring(0, difficulty).equals(target)) {
            this.nonce++;
            this.currentHash = this.calculateHash();
        }
        
        System.out.println("New Block Mined!");
    }
    
    public boolean addTransaction(Transaction transaction) {
        
        if(transaction == null) return false;
        
        if(!previousHash.equals("0")) {
            if(!transaction.processTransaction()) {
                System.out.println("#Transaction process failed.");
                return false;
            }
        }
        
        transactions.add(transaction);
        System.out.println("#Transaction processed and successfully added to Block");
        return true;
    }
}