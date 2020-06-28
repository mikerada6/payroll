package rad.irs.payroll.Payroll;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class ReportData {

    private String page;
    @JsonDeserialize(as = Data.class)
    private ArrayList<Data> values;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public ArrayList<Data> getData() {
        return values;
    }

    public void setData(ArrayList<Data> data) {
        this.values = data;
    }
}
