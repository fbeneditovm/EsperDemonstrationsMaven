
package com.cor.cep.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fbeneditovm
 */
public class MethodLib {
    public static Date getDateFromMillisec(Object objTimeMillisec){
        Long timeMillisec = (Long)objTimeMillisec;
        return new Date(timeMillisec);
    }
    
    public static int FfromC(int Celsius){
        Double f = Celsius*1.8+32;
        return f.intValue();
    }
    
    public static Temperatures getTemperaturesFromC(int celsius){
        Temperatures tps = new Temperatures();
        tps.setCelsius(celsius);
        return tps;
    }
  
    public static Map<String, Class> getTemperaturesMetadata() {
    
        Map<String, Class> propertyNames = new HashMap<String, Class>();
        propertyNames.put("Celcius", Integer.class);
        propertyNames.put("Kelvin", Integer.class);
        propertyNames.put("Fahrenheit", Integer.class);
        return propertyNames;
    }
    
    public static Map<String, Object>getTemperatures(int celcius) {
    
        Map<String, Object> tpMap = new HashMap<String, Object>();
        
        tpMap.put("Celcius", celcius);
        tpMap.put("Kelvin", celcius+273);
        tpMap.put("Fahrenheit", FfromC(celcius));
        
        return tpMap;
    }
}
