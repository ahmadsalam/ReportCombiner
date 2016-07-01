/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportcombiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author Salam
 */
public class ReportCombiner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, ParserConfigurationException, SAXException, java.text.ParseException {
        // TODO code application logic here
        BufferedReader reader = new BufferedReader(new FileReader("reports.csv"));
        String line = reader.readLine(); //read the first row, i.e. columns names
        String[] cols;
        String clientAddress, clientGuid, serviceGuid;
        Date requestTime;
        int retriesRequest, packetsRequested, packetsServiced, maxHoleSize;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        LinkedList<ReportInfo> reportList = new LinkedList<>();
        JSONParser parser = new JSONParser();
        String jsonInput;
        ReportInfo reportInfo;
        while((line = reader.readLine()) != null)
        {
            cols = line.split(",");
            packetsServiced = Integer.parseInt(cols[6]);
            if(packetsServiced == 0) continue;
            clientAddress = cols[0];
            clientGuid = cols[1];
            requestTime = formatter.parse(cols[2]); //TBD
            serviceGuid = cols[3];
            retriesRequest = Integer.parseInt(cols[4]);
            packetsRequested = Integer.parseInt(cols[5]);
            
            maxHoleSize = Integer.parseInt(cols[7]);
            reportInfo = new ReportInfo(clientAddress, clientGuid, requestTime, serviceGuid, retriesRequest, packetsRequested, packetsServiced, maxHoleSize);
            reportList.add(reportInfo);
        }
        
        jsonInput = new String(Files.readAllBytes(FileSystems.getDefault().getPath("reports.json")));
        JSONArray array = (JSONArray)parser.parse(jsonInput);
        JSONObject obj;
        String temp;
        for (int i = 0; i < array.size(); i++) {
            obj = (JSONObject)array.get(i);
            packetsServiced = Integer.parseInt(obj.get("packets-serviced").toString());
            if(packetsServiced == 0) continue;
            clientAddress = (String)obj.get("client-address");
            clientGuid = (String)obj.get("client-guid");
            
            requestTime = new Date( Long.valueOf(obj.get("request-time").toString()));
            serviceGuid = (String)obj.get("service-guid");
            retriesRequest = Integer.parseInt(obj.get("retries-request").toString());
            packetsRequested = Integer.parseInt(obj.get("packets-requested").toString());
            
            maxHoleSize = Integer.parseInt(obj.get("max-hole-size").toString());
            reportInfo = new ReportInfo(clientAddress, clientGuid, requestTime, serviceGuid, retriesRequest, packetsRequested, packetsServiced, maxHoleSize);
            reportList.add(reportInfo);
        }
        
        File xmlFile = new File("reports.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("report");
        for(int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            Element element = (Element)node;
            packetsServiced = Integer.parseInt(element.getElementsByTagName("packets-serviced").item(0).getTextContent());
            if(packetsServiced == 0) continue;
            clientAddress = element.getElementsByTagName("client-address").item(0).getTextContent();
            clientGuid = element.getElementsByTagName("client-guid").item(0).getTextContent();
            requestTime = formatter.parse(element.getElementsByTagName("request-time").item(0).getTextContent());
            serviceGuid = element.getElementsByTagName("service-guid").item(0).getTextContent();
            retriesRequest = Integer.parseInt(element.getElementsByTagName("retries-request").item(0).getTextContent());
            packetsRequested = Integer.parseInt(element.getElementsByTagName("packets-requested").item(0).getTextContent());
            
            maxHoleSize = Integer.parseInt(element.getElementsByTagName("max-hole-size").item(0).getTextContent());
            reportInfo = new ReportInfo(clientAddress, clientGuid, requestTime, serviceGuid, retriesRequest, packetsRequested, packetsServiced, maxHoleSize);
            reportList.add(reportInfo);
        }
        reportList.sort(null);
        //TBD sorting
        HashMap<String, Integer> cache = new HashMap<String, Integer>();
        String sv; //temp service guid
        FileWriter writer = new FileWriter("combined_reports.csv");
        writer.append("client-address,client-guid,request-time,service-guid,retries-request,packets-requested,packets-serviced,max-hole-size\n");
        for(ReportInfo info: reportList)
        {
            writer.append(info.getClientAddress() + "," + info.getClientGuid() + "," 
                    + formatter.format(info.getRequestTime()) + "," + 
                    info.getServiceGuid() + "," + info.getRetriesRequest() + "," 
                    + info.getPacketsRequested() + "," + info.getPacketsServiced()
                    + "," + info.getMaxHoleSize() + "\n");
            if(cache.containsKey(info.getServiceGuid()))
            {
                cache.put(info.getServiceGuid(), cache.get(info.getServiceGuid()) + 1);
            } else {
                cache.put(info.getServiceGuid(), 1);
            }
        }
        writer.flush();
        writer.close();
        System.out.println("\t **** Summary **** \t");
        System.out.println("Service Guide \t\t\t\tnumber of records");
        Iterator<String> iter = cache.keySet().iterator();
        while(iter.hasNext())
        {
            String serviceGuide = iter.next();
            int recordsCount = cache.get(serviceGuide);
            System.out.println(serviceGuide + "\t" + recordsCount);
        }
    }
    
}
