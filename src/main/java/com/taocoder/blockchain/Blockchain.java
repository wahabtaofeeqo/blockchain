/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class Blockchain {
    
    static ArrayList<Block> chain = new ArrayList<>();
    private static final int DIFFICULTY = 2;
    
    public static boolean validateChain() {
        Block currentBlock;
        Block previousBlock;
        
        for (int i = 1; i < chain.size(); i++) {
            currentBlock = chain.get(i);
            previousBlock = chain.get(i - 1);
            
            if(!currentBlock.currentHash.equals(currentBlock.calculateHash())) {
                
                System.err.println("Hash of the current block not match");
                return false;
            }
            
            if(!previousBlock.currentHash.equals(currentBlock.previousHash)) {
                System.err.println("Previous Hash not match");
                return false;
            }
            
            String target = new String(new char[DIFFICULTY]).replace('\0', '0');
            if(!currentBlock.currentHash.substring(0, DIFFICULTY).equals(target)){
                System.err.println("Block has not been mined!");
                return false;
            }
        }
        
        return true;
    }
    
    public void addBlock(Block b) {}
    
    public static void main(String[] args) {
        
        
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        
        Wallet a = new Wallet();
        Wallet b = new Wallet();
        
        System.err.println("Wallet A public and private Key");
        System.out.println(Utils.stringFromKey(a.getPrivateKey()));
        System.out.println(Utils.stringFromKey(a.getPublicKey()));
        
        System.err.println("Transaction ===");
        Transaction t = new Transaction(a.getPublicKey(), b.getPublicKey(), 10, null);
        t.signData(a.getPrivateKey());
        
        System.err.println("Verify Transaction ===");
        System.out.println(t.verifySignatue(a.getPublicKey()));;
        
//        chain.add(new Block("Hi im the first block", "0"));
//        
//        System.out.println("Trying to Mine Block 1");
//        chain.add(new Block("Hi im the second block", chain.get(chain.size() - 1).currentHash));
//        chain.get(1).mineBlock(DIFFICULTY);
//        
//        System.out.println("Trying to Mine Block 2");
//        chain.add(new Block("Hi im the second block", chain.get(chain.size() - 1).currentHash));
//        chain.get(2).mineBlock(DIFFICULTY);
//        
//        System.out.println("\nBlockchain is Valid: " +  validateChain());
//        
//        String json = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
//        
//        System.out.println("\nBlockchain: ");
//        System.out.println(json);
    }
}