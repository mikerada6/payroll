package Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/reports")
public class Report {

    @GetMapping(path = "/pvx")
    public @ResponseBody
    String Pvx_Pdf_Printer()
    {
        return "done";
    }
}
