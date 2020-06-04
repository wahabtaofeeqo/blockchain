/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Transaction {
    
    public String transactionID;
    public PublicKey sender;
    public PublicKey receiver;
    
    public float amount;
    public byte[] signature;
    
    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();
    
    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float amount, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receiver = to;
        this.amount = amount;
        this.inputs = inputs;
    }
    
    public String transactionHash() {
        
        sequence++;
        return Utils.sha256(Utils.stringFromKey(sender) + Utils.stringFromKey(receiver) + Float.toString(amount) + sequence);
    }
    
    public void signData(PrivateKey key) {
        String data = Utils.stringFromKey(sender) + Utils.stringFromKey(receiver) + Float.toString(amount);
        signature = Utils.applySignature(key, data);
    }
    
    public boolean verifySignatue(PublicKey key) {
        
         String data = Utils.stringFromKey(sender) + Utils.stringFromKey(receiver) + Float.toString(amount);
        return Utils.verifySignature(key, data, signature);
    }
}