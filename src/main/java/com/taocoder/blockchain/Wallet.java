/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author user
 */
public class Wallet {
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); //list of all unspent transactions.
    
    public Wallet() {
        generateKeyPair();
    }
    
    private void generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec spec = new ECGenParameterSpec("prime192v1");
            
            generator.initialize(spec, random);
            
            KeyPair kp = generator.genKeyPair();
            privateKey = kp.getPrivate();
            publicKey = kp.getPublic();
            
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    public float getBalance() {
        float total = 0;
        
        for(Map.Entry<String, TransactionOutput> item: Blockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.amount;
            }
        }
        
        return total;
    }
    
    public Transaction sendFunds(PublicKey receipient, float amount) {
        
        if(getBalance() < amount) {
            System.out.println("#Not enough Funds to send");
            return null;
        }
        
        ArrayList<TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        
        for(Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.amount;
            
            inputs.add(new TransactionInput(UTXO.id));
            if(total > amount) break;
        }
        
        Transaction t = new Transaction(publicKey, receipient, amount, inputs);
        t.signData(privateKey);
        
        for(TransactionInput i: inputs) {
            UTXOs.remove(i.transactionOutputId);
        }
        
        return t;
    }
}