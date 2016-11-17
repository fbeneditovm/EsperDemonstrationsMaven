package com.cor.cep.util;

/**
 *
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
     * @return the kelvin
     */
    public int getKelvin() {
        return kelvin;
    }

    /**
     * @param kelvin the kelvin to set
     */
    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
        this.celsius = kelvin-273;
        Double f = this.celsius*1.8+32;
        this.fahrenheit = f.intValue();
    }

    /**
     * @return the celsius
     */
    public int getCelsius() {
        return celsius;
    }

    /**
     * @param celsius the celsius to set
     */
    public void setCelsius(int celsius) {
        this.celsius = celsius;
        this.kelvin = celsius+273;
        Double f = celsius*1.8+32;
        this.fahrenheit = f.intValue();
    }

    /**
     * @return the fahrenheit
     */
    public int getFahrenheit() {
        return fahrenheit;
    }

    /**
     * @param fahrenheit the fahrenheit to set
     */
    public void setFahrenheit(int fahrenheit) {
        this.fahrenheit = fahrenheit;
        Double c = (fahrenheit-32)/1.8;
        this.celsius = c.intValue();
        this.kelvin = celsius+273;
    }
    
    public static Temperatures getFromC(int celsius){
        Temperatures tps = new Temperatures();
        tps.setCelsius(celsius);
        return tps;
    }
}
