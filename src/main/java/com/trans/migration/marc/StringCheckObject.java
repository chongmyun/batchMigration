package com.trans.migration.marc;

public class StringCheckObject {
    private int function_value = 0;
    private String value = "";

    /**
     * 초기화 함수
     */
    public void init(){
        this.function_value = 0;
        this.value = "";
    }

    /**
     * @return the function_value
     */
    public int getFunction_value() {
        return function_value;
    }

    /**
     * @param function_value the function_value to set
     */
    public void setFunction_value(int function_value) {
        this.function_value = function_value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
