package rad.irs.payroll.Payroll.Controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rad.irs.payroll.Payroll.ResourceNotFoundException;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;

@Controller
@RequestMapping(path = "/reports")
public class Report {

    private static final Logger logger = LoggerFactory.getLogger(Report.class);

    @Value("${report.fileLocation.json}")
    String jsonLocation;
    @Value("${report.save.baseLocation}")
    String saveLocation;

    @GetMapping(path = "/pvx")
    public @ResponseBody
    String Pvx_Pdf_Printer() {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try (FileReader fileReader = new FileReader(jsonLocation)) {
            //Read JSON file
            Object obj = jsonParser.parse(fileReader);

            jsonObject = (JSONObject) obj;

        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException {}",
                         e);
            throw new ResourceNotFoundException("Could not read the json");
        } catch (IOException e) {
            logger.error("IOException {}",
                         e);
            throw new ResourceNotFoundException("Could not handle the json");
        } catch (ParseException e) {
            logger.error("ParseException {}",
                         e);
            throw new ResourceNotFoundException("Could not parse the json");
        }
        try {
            String page = jsonObject.get("page").toString();
            File bmpFile = new File(page);

            BufferedImage image = ImageIO.read(bmpFile);
            Graphics graphics = image.getGraphics();
            graphics.setColor(Color.BLACK);
            JSONArray data = (JSONArray) jsonObject.get("values");

            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            for (int i = 0; i < data.size(); i++) {

                JSONObject datum = (JSONObject) data.get(i);
                String key = datum.get("data").toString();
                int fontSize = datum.containsKey("size") ? Integer.parseInt(datum.get("size").toString()) : 60;
                Font font = new Font("Courier",
                                     Font.PLAIN,
                                     fontSize);
                graphics.setFont(font);
                FontMetrics metrics = graphics.getFontMetrics(font);
                int x = Integer.parseInt(datum.get("x").toString());
                int y = Integer.parseInt(datum.get("y").toString());
                if (datum.get("format").toString().equalsIgnoreCase("CURRENCY")) {
                    if (!key.contains(".")) {
                        key += ".00";
                    }
                    if(key.indexOf('.')==0)
                    {
                        key = "0"+key;
                    }
                    double tempDouble = Double.parseDouble(key);

                    String[] split = key.split("\\.");
                    String temp = myFormat.format(Double.parseDouble(split[0]));
                    if (Math.abs(tempDouble) >= 1) {
                        graphics.drawString(temp,
                                            x - metrics.stringWidth(temp) - 10,
                                            y);
                    }
                    graphics.drawString(split[1],
                                        x + 25,
                                        y);
                } else {
                    if (datum.containsKey("style") && datum.get("style").toString().equalsIgnoreCase("center")) {
                        x -= metrics.stringWidth(key) / 2;
                    }
                    graphics.drawString(key,
                                        x,
                                        y);
                }
            }
            String targetFilePath = saveLocation +
                                    "/" +
                                    jsonObject.get("company") +
                                    "/" +
                                    jsonObject.get("form") +
                                    "/" +
                                    jsonObject.get("year") +
                                    "/" +
                                    jsonObject.get("qtr");
            File dir = new File(targetFilePath);
            if (!dir.exists()) dir.mkdirs();
            ImageIO.write(image,
                          "jpg",
                          new File(targetFilePath + "/" + jsonObject.get("filename") + ".jpg"));
            try {
//                print(targetFilePath + "/" + jsonObject.get("filename") + ".jpg");
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResourceNotFoundException();
            }


        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        return "done";
    }


    public void print(String imageFileName) throws IOException, PrinterException {
        final PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        final BufferedImage image = ImageIO.read(new File(imageFileName));
        final PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setJobName("MyApp: " + imageFileName);
        printJob.setPrintService(printService);
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex == 0) {
                    final Paper paper = pageFormat.getPaper();
                    paper.setImageableArea(0.0,
                                           0.0,
                                           pageFormat.getPaper().getWidth(),
                                           pageFormat.getPaper().getHeight());
                    pageFormat.setPaper(paper);
                    graphics.translate((int) pageFormat.getImageableX(),
                                       (int) pageFormat.getImageableY());
                    graphics.drawImage(image,
                                       0,
                                       0,
                                       (int) pageFormat.getPaper().getWidth(),
                                       (int) pageFormat.getPaper().getHeight(),
                                       null);
                    return PAGE_EXISTS;
                } else {
                    return NO_SUCH_PAGE;
                }
            }
        });
        printJob.print();
    }
}
