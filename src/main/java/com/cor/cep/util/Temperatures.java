package com.cor.cep.util;

/**
 * A class with Temperature conversion methods
 * @author fbeneditovm
 */
public class Temperatures {
    private int kelvin;
    private int celsius;
    private int fahrenheit;
    
    Temperatures(){
        kelvin=celsius=fahrenheit=-0;
    }

    /**
     * @return the temperature in kelvin
     */
    public int getKelvin() {
        return kelvin;
    }

    /**
     * @param kelvin the kelvin measure to set
     */
    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
        this.celsius = kelvin-273;
        Double f = this.celsius*1.8+32;
        this.fahrenheit = f.intValue();
    }

    /**
     * @return the temperature in celsius
     */
    public int getCelsius() {
        return celsius;
    }

    /**
     * @param celsius the celsius measure to set
     */
    public void setCelsius(int celsius) {
        this.celsius = celsius;
        this.kelvin = celsius+273;
        Double f = celsius*1.8+32;
        this.fahrenheit = f.intValue();
    }

    /**
     * @return the temperature in fahrenheit
     */
    public int getFahrenheit() {
        return fahrenheit;
    }

    /**
     * @param fahrenheit the fahrenheit measure to set
     */
    public void setFahrenheit(int fahrenheit) {
        this.fahrenheit = fahrenheit;
        Double c = (fahrenheit-32)/1.8;
        this.celsius = c.intValue();
        this.kelvin = celsius+273;
    }
    
    /**
     * get a new Temperatures Object based on a celsius value
     * @param celsius the celsius measure to set
     * @return a Temperatures Object
     */
    public static Temperatures getFromC(int celsius){
        Temperatures tps = new Temperatures();
        tps.setCelsius(celsius);
        return tps;
    }
    
    /**
     * Tests if a certain temperature in celsius is above other certain
     * temperature in kelvin
     * @param c the celsius temperature to test
     * @param k the kelvin temperature to compare to
     * @return a boolean value
     */
    public static boolean isCAboveThresholdK(int c, int k){
        return ((c+273)>k);
    }
}
