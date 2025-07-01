package com.projeto.Ecommerce.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class CriptografiaService {

    public String criptografar(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public boolean comparar(String senhaDigitada, String senhaCriptografada) {
        return BCrypt.checkpw(senhaDigitada, senhaCriptografada);
    }
}