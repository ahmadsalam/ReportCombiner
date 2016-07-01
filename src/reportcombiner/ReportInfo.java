/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportcombiner;

import java.util.Date;

/**
 *
 * @author Salam
 */
public class ReportInfo implements Comparable{
    String clientAddress;
    String clientGuid;
    Date requestTime;
    String serviceGuid;
    int retriesRequest;
    int packetsRequested;
    int packetsServiced;
    int maxHoleSize; 
    
    public ReportInfo(String clientAddress, String clientGuid, Date requestTime,
            String serviceGuid, int retriesRequest, int packetsRequested,
            int packetsServiced, int maxHoleSize)
    {
        this.clientAddress = clientAddress;
        this.clientGuid = clientGuid;
        this.requestTime = requestTime;
        this.serviceGuid = serviceGuid;
        this.retriesRequest = retriesRequest;
        this.packetsRequested = packetsRequested;
        this.packetsServiced = packetsServiced;
        this.maxHoleSize = maxHoleSize;
    }
    
    public String getClientAddress()
    {
        return clientAddress;
    }
    public String getClientGuid()
    {
        return clientGuid;
    }
    public Date getRequestTime()
    {
        return requestTime;
    }
    public String getServiceGuid()
    {
        return serviceGuid;
    }
    public int getRetriesRequest()
    {
        return retriesRequest;
    }
    public int getPacketsRequested()
    {
        return packetsRequested;
    }
    public int getPacketsServiced()
    {
        return packetsServiced;
    }
    public int getMaxHoleSize()
    {
        return maxHoleSize;
    }
    
    @Override
    public int compareTo(Object o) {
        
        return this.requestTime.compareTo(((ReportInfo)o).getRequestTime());
    }

}
