/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ifrn.coapac.utils;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author luan
 */
public class Main {
    public static void main(String[] args) throws IOException {
        TesteAPI api = new TesteAPI("20141064010220", "Soad555333");
        String dados = api.buscarDados();
        System.out.println(dados);
        String vinculo = dados.subSequence(dados.indexOf("{\"matri"), dados.indexOf("}}"))+"}";
  
        Gson gson = new Gson();
	HashMap<String, String> meus_dados = gson.fromJson(vinculo, HashMap.class); 
        //System.out.println(meus_dados.get("curso"));
    }   
}
