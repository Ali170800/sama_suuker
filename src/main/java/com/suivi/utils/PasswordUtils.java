package com.suivi.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hasher le mot de passe
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Vérifier le mot de passe (ordre strict : 1. Clair, 2. Hash de la base)
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false; // Évite les crashs si le format en base est invalide
        }
    }
}