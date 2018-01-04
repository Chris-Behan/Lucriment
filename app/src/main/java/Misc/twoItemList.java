package Misc;

import java.util.ArrayList;

/**
 * Created by ChrisBehan on 6/28/2017.
 */

public class twoItemList {
    public ArrayList<TwoItemField> twoItemFields;
    public twoItemList(){

    }
    public twoItemList(ArrayList<TwoItemField> t){
        twoItemFields = t;
    }

    public ArrayList<TwoItemField> getTwoItemFields() {
        return twoItemFields;
    }

    public void setTwoItemFields(ArrayList<TwoItemField> twoItemFields) {
        this.twoItemFields = twoItemFields;
    }
}
