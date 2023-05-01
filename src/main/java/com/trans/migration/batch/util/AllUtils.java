package com.trans.migration.batch.util;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component("AllUtils")
public class AllUtils {

    public String getObjectFieldNames(Class c,boolean containKey){

        if(c == null) return "";

        String names = "";
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            Nullable annotation = field.getAnnotation(Nullable.class);
            if(annotation == null){
                if(containKey == true){
                    if (names.equals("")) {
                        names += field.getName();
                    } else {
                        names += "," + field.getName();
                    }
                }else{
                    if(field.getName().equals("partitionKey") == false) {
                        if (names.equals("")) {
                            names += field.getName();
                        } else {
                            names += "," + field.getName();
                        }
                    }
                }
            }
        }

        return names;
    }
}
