/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.raspberrypicolorserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sauln
 */
public class Conexion extends Thread implements ISendBroadcastMessage {

    public ServerSocket servidor;
    IOnShitReceived iOnShitReceived;
    ArrayList<ClientDirectConection> clients;

    public Conexion(IOnShitReceived iOnShitReceived) {
        this.clients = new ArrayList<>();
        this.iOnShitReceived = iOnShitReceived;
        try {
            servidor = new ServerSocket(4444);

        } catch (IOException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket cliente = servidor.accept();
                clients.add(new ClientDirectConection(cliente, iOnShitReceived, this));
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void desconectar() {
        try {
            clients.forEach((client) -> client.close());
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void BroadcastMessage(String message) {
        clients.forEach((client) -> client.send(message));
    }

    public class ClientDirectConection extends Thread {

        public BufferedReader entrada;
        public DataOutputStream salida;
        public InputStreamReader e;
        public Socket cliente;
        IOnShitReceived iOnShitReceived;
        ISendBroadcastMessage iSendBroadcastMessage;

        public ClientDirectConection(Socket cliente,
                IOnShitReceived iOnShitReceived, 
                ISendBroadcastMessage iSendBroadcastMessage) {
            this.iSendBroadcastMessage = iSendBroadcastMessage;
            this.cliente = cliente;
            this.iOnShitReceived = iOnShitReceived;

            try {
                e = new InputStreamReader(cliente.getInputStream());
                entrada = new BufferedReader(e);
                salida = new DataOutputStream(cliente.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String mensaje = entrada.readLine();
                    String broadcastResponse = iOnShitReceived.MessageReceived(mensaje);
                    if(broadcastResponse != null){
                        iSendBroadcastMessage.BroadcastMessage(broadcastResponse);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        public void send(String message){
            try {
                salida.writeUTF(message);
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void close() {
            try {
                cliente.close();
            } catch (IOException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
