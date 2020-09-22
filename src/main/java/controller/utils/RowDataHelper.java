package controller.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import controller.MongoDbController;
import controller.Settings;
import model.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.riversun.promise.Func;
import org.riversun.promise.Promise;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RowDataHelper {
    // static variable single_instance of type Singleton
    private static RowDataHelper single_instance = null;

    // static method to create instance of Singleton class
    public static RowDataHelper getInstance() {
        if (single_instance == null)
            single_instance = new RowDataHelper();

        return single_instance;
    }

    public void writeTextsFromCsv(String csvWebSitesFilePath) {
        List<String> linksListPerEx = RowDataHelper.getInstance().readSitesListByType(TextTypeEnum.PersonalExperience, csvWebSitesFilePath);
        List<String> linksListPromo = RowDataHelper.getInstance().readSitesListByType(TextTypeEnum.Promotion, csvWebSitesFilePath);

        for (String link : linksListPromo) {
            RowDataHelper.getInstance().writeTextFromSitesToDb(link, TextTypeEnum.PersonalExperience);
        }

        for (String link : linksListPerEx) {
            RowDataHelper.getInstance().writeTextFromSitesToDb(link, TextTypeEnum.Promotion);
        }
    }

    private List<String> readSitesListByType(TextTypeEnum type, String csvWebSitesFilePath) {
        List<String> linksList = new ArrayList<String>();
        URL csvFile = getClass().getClassLoader().getResource(csvWebSitesFilePath);

        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile.getPath())));

            String[] record;
            int colNumByType = -1;

            if ((record = csvReader.readNext()) != null) {
                if (record[0].equals(type.toString())) {
                    colNumByType = 0;
                } else if (record[1].equals(type.toString())) {
                    colNumByType = 1;
                }
            }

            if (record == null)
                throw new NullPointerException("CSV File format is wrong! record is null");

            if (colNumByType == -1)
                throw new CsvValidationException("CSV File format is wrong! Can't read the columns types");

            while ((record = csvReader.readNext()) != null) {
                linksList.add(record[colNumByType]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linksList;
    }

    public void writeTextFromSitesToDb(String link, TextTypeEnum type) {

        Func getSiteContent = getSiteContent(link, type);

        Func writeToDb = (action, data) -> {
            MongoDbController.getInstance().addText((Text) data);
            action.resolve();
        };

        Promise.resolve()
                .then(new Promise(getSiteContent))
                .then(new Promise(writeToDb))
                .start();
    }

    public Func getSiteContent(String link, TextTypeEnum type) {
        return (action, data) -> {
                new Thread(() -> {
                    try {
                        Document doc = Jsoup.connect(link).get();
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String title = doc.title();
    //                    List<Element> elements = doc.body().select(":not(body,html,title,meta,link,img,script,input,form,a,button)");

    //                    Element maxElement = new Element("div");
    //
    //                    for (Element element : elements) {
    //                        if (maxElement.ownText().length() < element.ownText().length()) {
    //                            maxElement = element;
    //                        }
    //                    }

                        String attributeContent = "";
                        for (Attribute attribute : doc.body().attributes().asList()) {
                            attributeContent += (attribute.getValue() + " ");
                        }


                        action.resolve(new Text(link, doc.body().text(), type));
                    } catch (IOException e) {
                        Logger.log(link + ": " + e.getMessage(), Settings.siteFailLoadLogFileName);
                        e.printStackTrace();
                    }
                }).start();
            };
    }
}
