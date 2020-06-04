/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taocoder.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

/**
 *
 * @author user
 */
public class Utils {
    
    public static String sha256(String key) {
        try {
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
          
            byte bytes[] = md.digest(key.getBytes(StandardCharsets.UTF_8));
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
    
    public static byte[] applySignature(PrivateKey key, String input) {
        Signature s;
        
        byte[] output = new byte[0];
        try {
            s = Signature.getInstance("ECDSA", "BC");
            s.initSign(key);
            
            byte[] b = input.getBytes();
            s.update(b);
            
            output = s.sign();
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new RuntimeException(e);
        }
        
        return output;
    }
    
    public static boolean verifySignature(PublicKey key, String data, byte[] signature) {
        try {
            Signature s = Signature.getInstance("ECDSA", "BC");
            s.initVerify(key);
            
            s.update(data.getBytes());
            return s.verify(signature);
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String stringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
