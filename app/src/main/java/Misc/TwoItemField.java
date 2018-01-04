package Misc;

/**
 * Created by ChrisBehan on 5/22/2017.
 */

public class TwoItemField {
    private String label;
    private String data;

    public TwoItemField(){

    }

    public TwoItemField(String label, String data) {
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
