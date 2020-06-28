package rad.irs.payroll.Payroll;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class irs {

    private String backroundLocation;
    private ArrayList<Data> data;

    public String getBackroundLocation() {
        return backroundLocation;
    }

    public void setBackroundLocation(String backroundLocation) {
        this.backroundLocation = backroundLocation;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }
}
