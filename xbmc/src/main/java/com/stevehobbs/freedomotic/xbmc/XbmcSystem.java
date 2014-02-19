/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stevehobbs.freedomotic.xbmc;


/**
 *
 * @author steve
 */
public class XbmcSystem {

    private String xbmcName;
    private String xbmcHost;
    private Integer xbmcPort;
    private String xbmcLocation;
    private Thread xbmcThread;
    private String xbmcPower;
    private String xbmcMethod;
    private String xbmcPlayer;
    
    public XbmcSystem(String xbmcName, String xbmcHost, int xbmcPort, String xbmcLocation) {
        
        setXbmcName(xbmcName);
        setXbmcHost(xbmcHost);
        setXbmcPort(xbmcPort);
        setXbmcLocation(xbmcLocation);
        setXbmcThread(null);
        setXbmcPower("false");
        setXbmcMethod("");
        setXbmcPlayer("");
                
    }

     public void setXbmcName(String xbmcName) {
        this.xbmcName = xbmcName;
    }
     
     public String getXbmcName() {
        return xbmcName;
    }
     
    public void setXbmcHost(String xbmcHost) {
        this.xbmcHost = xbmcHost;
    }
     
     public String getXbmcHost() {
        return xbmcHost;
     }
     
     public void setXbmcLocation(String xbmcLocation) {
        this.xbmcLocation = xbmcLocation;
    }
     
     public String getXbmcLocation() {
        return xbmcLocation;
     }
     
    public void setXbmcPort(int xbmcPort) {
        this.xbmcPort = xbmcPort;
    }

    public int getXbmcPort() {
        return xbmcPort;
    }
    
    public void setXbmcThread(Thread xbmcThread) {
        this.xbmcThread = xbmcThread;
    }

    public Thread getXbmcThread() {
        return xbmcThread;
    }
    
    public void setXbmcPower(String xbmcPower) {
        this.xbmcPower = xbmcPower;
    }
   
     public String getXbmcPower() {
        return xbmcPower;
     }
     
     public void setXbmcMethod(String xbmcMethod) {
        this.xbmcMethod = xbmcMethod;
    }
   
     public String getXbmcMethod() {
        return xbmcMethod;
     }
     
      public String getXbmcPlayer() {
        return xbmcPlayer;
     }
     
     public void setXbmcPlayer(String xbmcPlayer) {
        this.xbmcPlayer = xbmcPlayer;
    }
}
