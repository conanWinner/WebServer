/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.webserver.util;

import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author conanWinner
 */
//////////// LIST => JSON
public class EndcodeJson {

    public EndcodeJson() {
    }

    private static Gson gson = new Gson();

    public static <T> String endcodeJson(T input) {

        String ans = gson.toJson(input);

        return ans;
    }

}
