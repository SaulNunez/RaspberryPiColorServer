/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.raspberrypicolorserver;

import java.awt.Color;

/**
 *
 * @author saul
 */
public interface IOnDataReceived {
    public void OnStateSet(boolean turnedOn);
    public void OnColorReceived(Color c);
}
