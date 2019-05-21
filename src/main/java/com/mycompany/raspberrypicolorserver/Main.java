package com.mycompany.raspberrypicolorserver;

import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author saul
 */
public class Main {
    static Color ledColor =  Color.black;
    public static void main(String[] args) {
        System.out.println("Iniciando servidor...");
        Conexion c = new Conexion(new IOnShitReceived() {
            @Override
            public String MessageReceived(String message) {
                String[] keywords = message.split("\\s+");
                System.out.println(message);

                if (keywords.length == 4) {
                    if ("color".equals(keywords[0])) {
                        int r, g, b;
                        r = Integer.parseInt(keywords[1]);
                        g = Integer.parseInt(keywords[2]);
                        b = Integer.parseInt(keywords[3]);

                        Color c = new Color(r, g, b);
                        System.out.println(String.format("Color cambiado a %i %i %i", c.getRed(), c.getGreen(), c.getBlue()));
                        ledColor = c;
                        return message;
                    }
                } else if (keywords.length == 2) {
                    if ("power".equals(keywords[0])) {
                        Boolean turnOn = Boolean.getBoolean(keywords[1]);
                        if(!turnOn){
                            System.out.println("Foco apagado");
                        } else {
                            System.out.println("Foco encendido");
                        }
                        return message;
                    }
                }
                return null;
            }

        });
        c.start();
    }
}
