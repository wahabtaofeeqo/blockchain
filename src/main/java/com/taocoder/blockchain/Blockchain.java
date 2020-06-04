/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author user
 */
public class Blockchain {
    
    
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); //list of all unspent transactions.
    static ArrayList<Block> blockchain = new ArrayList<>();
    
    public static float minimumTransaction = 0.1f;
    private static final int DIFFICULTY = 3;
    
    public static Wallet a;
    public static Wallet b;
    public static Transaction genesisTransaction;
    
    public static boolean validateChain() {
        
        Block currentBlock;
        Block previousBlock;
        String target = new String(new char[DIFFICULTY]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
        
        
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            
            if(!currentBlock.currentHash.equals(currentBlock.calculateHash())) {
                
                System.err.println("Hash of the current block not match");
                return false;
            }
            
            if(!previousBlock.currentHash.equals(currentBlock.previousHash)) {
                System.err.println("Previous Hash not match");
                return false;
            }
            
            
            if(!currentBlock.currentHash.substring(0, DIFFICULTY).equals(target)){
                System.err.println("Block has not been mined!");
                return false;
            }
            
            TransactionOutput to;
            
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction transaction = currentBlock.transactions.get(t);
                
                if(!transaction.verifySignatue()) {
                    System.out.println("#Signature on Transaction (" + t + ") is Invalid");
                    return false;
                }
                
                if(transaction.getInputValue() != transaction.getOutputValue()) {
                    System.out.println("#Inputs are not equal Outputs on Transaction (" + t + ")");
                    return false;
                }
                
                for(TransactionInput input: transaction.inputs) {
                    to = tempUTXOs.get(input.transactionOutputId);
                    
                    if(to == null) {
                        System.out.println("#Reference input on Transaction(" + t + ") is missing");
                        return false;
                    }
                    
                    if(input.UTXO.amount != to.amount) {
                        System.out.println("#Reference input on Transaction(" + t + ") is Invalid");
                        return false;
                    }
                    
                    tempUTXOs.remove(input.transactionOutputId);
                }
                
                for(TransactionOutput output: transaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }
                
                if(transaction.outputs.get(0).reciepient != transaction.receiver) {
                    System.out.println("Transaction(" + t + ") Reciepient has been changed!");
                    return false;
                }
                
                if(transaction.outputs.get(1).reciepient != transaction.sender) {
                    System.out.println("Transaction(" + t + ") Reciepient has been changed!");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public static void addBlock(Block b) {
        b.mineBlock(DIFFICULTY);
        blockchain.add(b);
    }
    
    public static void main(String[] args) {
        
        
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        Wallet a = new Wallet();
        Wallet b = new Wallet();
        
        Wallet coinbase = new Wallet();
        
        genesisTransaction = new Transaction(coinbase.getPublicKey(), a.getPublicKey(), 100f, null);
        genesisTransaction.signData(coinbase.getPrivateKey());
        genesisTransaction.transactionID = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver, genesisTransaction.amount, genesisTransaction.transactionID));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
        
        
        System.out.println("Creating and Mining Block");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);
        
        // Sending
        Block block1 = new Block(genesis.currentHash);
        System.out.println("\nWallet A balance: " + a.getBalance());
        System.out.println("\nSending Funds to Wallet B");
        block1.addTransaction(a.sendFunds(b.getPublicKey(), 40f));
        addBlock(block1);
        
        System.out.println("\nWallet A balance: " + a.getBalance());
        System.out.println("\nWallet B balance: " + b.getBalance());
    }
}