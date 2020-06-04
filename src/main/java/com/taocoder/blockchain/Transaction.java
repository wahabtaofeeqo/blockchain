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
    
    public boolean verifySignatue() {
        
         String data = Utils.stringFromKey(sender) + Utils.stringFromKey(receiver) + Float.toString(amount);
        return Utils.verifySignature(sender, data, signature);
    }
    
    public boolean processTransaction() {
        if(!verifySignatue()) {
            System.out.println("#Transaction Signature Verification failed");
            return false;
        }
        
        for(TransactionInput input: inputs) {
            input.UTXO = Blockchain.UTXOs.get(input.transactionOutputId);
        }
        
        if(getInputValue() < Blockchain.minimumTransaction) {
            System.out.println("#Transaction Input is too small");
            return false;
        }
        
        float balance = getInputValue();
        float remain = balance - amount;
        
        transactionID = transactionHash();
        outputs.add(new TransactionOutput(receiver, amount, transactionID)); // To sender
        outputs.add(new TransactionOutput(sender, remain, transactionID));
        
        for(TransactionOutput out: outputs) {
            Blockchain.UTXOs.put(out.transactionId, out);
        }
        
        for(TransactionInput i: inputs) {
            if(i.UTXO == null) continue;
            Blockchain.UTXOs.remove(i.UTXO.id);
        }
        
        return true;
    }
    
    public float getInputValue() {
        
        float total = 0;
        for(TransactionInput i: inputs) {
            if(i.UTXO == null) continue;
            total += i.UTXO.amount;
        }
        
        return total;
    }
    
    public float getOutputValue() {
        
        float total = 0;
        for(TransactionOutput o: outputs) {
            if(o == null) continue;
            total += o.amount;
        }
        
        return total;
    }
}