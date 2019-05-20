package com.mycompany.raspberrypicolorserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author saul
 */
public class LedSet extends Thread implements IColorInfoObtain {

    final GpioController gpio;
    final PinSetStrobe red, green, blue;

    public LedSet(Pin pinRed, Pin pinGreen, Pin pinBlue) {
        gpio = GpioFactory.getInstance();
        red = new PinSetStrobe(gpio, pinRed, "Red LED");
        blue = new PinSetStrobe(gpio, pinBlue, "Blue LED");
        green = new PinSetStrobe(gpio, pinGreen, "Green LED");
    }

    @Override
    public void SetColor(Color c) {
        red.setClockrate((double) c.getRed() / 256);
        blue.setClockrate((double) c.getBlue() / 256);
        green.setClockrate((double) c.getGreen() / 256);

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
            while (true) {
                try {
                    if (clockrate != 0) {
                        gpioPinDigitalOutput.high();
                        Thread.sleep((long) clockrate);
                        gpioPinDigitalOutput.low();
                        Thread.sleep((long) (1 / clockrate));
                    } else {
                        gpioPinDigitalOutput.low();
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(LedSet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
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

    @Override
    protected void finalize() throws Throwable {
        try {
            if (gpio != null) {
                gpio.shutdown();
            }
        } finally {
            super.finalize();
        }
    }
}
