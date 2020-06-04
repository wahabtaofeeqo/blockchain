/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.security.PublicKey;

/**
 *
 * @author user
 */
public class TransactionOutput {
    
    public String id;
    public PublicKey reciepient;
    public float amount;
    public String transactionId;
    
    public TransactionOutput(PublicKey reciepient, float amount, String tid) {
        this.reciepient = reciepient;
        this.amount = amount;
        this.transactionId = tid;
        this.id = Utils.sha256(Utils.stringFromKey(reciepient) + Float.toString(amount) + tid);
    }
    
    public boolean isMine(PublicKey key) {
        return (reciepient == key);
    }
}
