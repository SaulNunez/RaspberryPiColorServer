package com.mycompany.raspberrypicolorserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import java.awt.Color;

/**
 *
 * @author saul
 */
public class LedSet extends Thread implements IColorInfoObtain {
    int pinRed, pinGreen, pinBlue;

    public LedSet(int pinRed, int pinGreen, int pinBlue) {
        this.pinRed = pinRed;
        this.pinGreen = pinGreen;
        this.pinBlue = pinBlue;
        
        Gpio.wiringPiSetup();

        SoftPwm.softPwmCreate(pinRed, 0, 100);
        SoftPwm.softPwmCreate(pinGreen, 0, 100);
        SoftPwm.softPwmCreate(pinBlue, 0, 100);
    }

    @Override
    public void SetColor(Color c) {
        SoftPwm.softPwmWrite(pinRed, (int) ((c.getRed() / 256.0) * 100));
        SoftPwm.softPwmWrite(pinBlue, (int) ((c.getBlue() / 256.0) * 100));
        SoftPwm.softPwmWrite(pinGreen, (int) ((c.getGreen() / 256.0) * 100));
        System.out.println("Colors updated");
    }

    public class PinSetStrobe extends Thread {

        GpioPinDigitalOutput gpioPinDigitalOutput;
        double clockrate;

        public PinSetStrobe(GpioController gpio, Pin pin, String pinName) {
            gpioPinDigitalOutput = gpio.provisionDigitalOutputPin(pin, pinName, PinState.HIGH);
        }

        @Override
        public void run() {

        }

        public void setClockrate(double newRate) {
            if (newRate < 0) {
                newRate = 0;
            } else if (newRate > 1) {
                newRate = 1;
            }
            clockrate = newRate;
        }

        public double getClockrate() {
            return clockrate;
        }
    }
}
