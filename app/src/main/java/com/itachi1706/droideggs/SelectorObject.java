package com.itachi1706.droideggs;

/**
 * Created by Kenneth on 1/6/2015
 * for DroidEggs in package com.itachi1706.droideggs
 */
public class SelectorObject {

    private String name, required, range;
    private int minSDK;

    public SelectorObject(String name, String range, String required, int minSDK){
        this.name = name;
        this.required = required;
        this.range = range;
        this.minSDK = minSDK;
    }

    public String getName() {
        return name;
    }

    public String getRequired() {
        return required;
    }

    public String getRange() {
        return range;
    }

    public int getMinSDK() {
        return minSDK;
    }
}
