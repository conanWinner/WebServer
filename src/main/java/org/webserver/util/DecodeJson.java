/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.webserver.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.lang.reflect.Type;

/**
 *
 * @author conanWinner
 */
/////////////// JSON => LIST
public class DecodeJson {

    public DecodeJson() {
    }
    private static Gson gson = new Gson();

    public static <T> List<T> decodeJson(String json, Type type){
        
//        Type listType = new TypeToken<List<T>>(){}.getType();
        
        return gson.fromJson(json, type);

    }
    
    
}
