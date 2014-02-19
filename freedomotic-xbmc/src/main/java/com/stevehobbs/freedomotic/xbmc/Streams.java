/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stevehobbs.freedomotic.xbmc;

import com.freedomotic.app.Freedomotic;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author steve
 */
public class Streams {
    //access to the streams needed in various places together with error handling
    //eg for when xbmc systems are switched on/off
    Socket requestSocket;
    BufferedOutputStream outStream;
    BufferedInputStream inStream; // Using input stream as supposed to be faster than buffered???
    
    public Streams(String host, Integer port){

        try {
            requestSocket = new Socket(host, port);
            outStream = new BufferedOutputStream(requestSocket.getOutputStream());
            outStream.flush();
            inStream = new BufferedInputStream(requestSocket.getInputStream(),8096); //use big buffer for input
            Freedomotic.logger.severe(host + " : streams set up"); //leave in for now
        }
        
        catch(IOException ioException){
            //Freedomotic.logger.severe(str + " : IO exception setting up stream"); //leave in for now
            //error, close socket & stream and set stream = null to force get new stream,
            closeAllStreams(); //not sure this is good??
           
        }
       // Successfully listening to xbmc json socket
}
    
    public InputStream getInputStream(){
        return inStream;
    }
    
   public OutputStream getOutputStream(){
        return outStream;
    } 
   public Socket getSocket(){
        return requestSocket;
    } 
   public void closeAllStreams() {
       //try & close anything thats open so we can start again
       //need to check on resource usage etc
        InputStream myInputStream = getInputStream();
        if (myInputStream != null) {
            try{
                myInputStream.close();
        }
        catch(IOException ioException){}
        }
        OutputStream myOutputStream = getOutputStream();
        if (myOutputStream != null) {
            try{
                myOutputStream.close();
        }
        catch(IOException ioException){}
        }
        Socket mySocket = getSocket();
        if (mySocket!=null) {
            try{
                mySocket.close();
        }
        catch(IOException ioException){}
        }
    }
}   
