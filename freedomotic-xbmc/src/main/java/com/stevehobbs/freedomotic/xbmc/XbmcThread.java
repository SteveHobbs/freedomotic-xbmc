package com.stevehobbs.freedomotic.xbmc;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonEncoding;
import com.freedomotic.app.Freedomotic;
import com.freedomotic.events.ProtocolRead;
import java.io.IOException;
import java.io.*;

/**
 *
 * @author steve
 */
public class XbmcThread implements Runnable {
    private XbmcSystem myXbmcSystem;
    OutputStream myOutputStream = null;
    InputStream myInputStream = null;
    
    public XbmcThread(XbmcSystem myXbmcSystem) {
        this.myXbmcSystem = myXbmcSystem;
    }
    
     @Override
     public void run() {
        
        Boolean firstPass = true;
        
        do{
            try{
                while (myInputStream == null) {
                    if (firstPass == false) {
                        Thread.sleep(4000); // only needed for retry logic
                    }
                    firstPass = false;
                    Streams myStreams = new Streams(myXbmcSystem.getXbmcHost(),myXbmcSystem.getXbmcPort());
                    myInputStream = myStreams.getInputStream();
                    myOutputStream = myStreams.getOutputStream();
                    if(myOutputStream != null) { // Ping for power
                        sendJsonPing();
                    }
                }
            } 
            catch(InterruptedException ieException){
                 //Freedomotic.logger.severe(myXbmcSystem.getXbmcHost()+" : ieException from set up streams main loop"); //Not sure what to do here
                 myInputStream = null; //Stream has gone away get get new one
            }
            catch(IOException ioException){
                //Freedomotic.logger.severe(myXbmcSystem.getXbmcHost()+" : ioException from set up streams main loop"); //lost the stream probably, xbmc closed? 
                myInputStream = null; //Stream has gone away get get new one
            }
            
            try {
                if(myInputStream != null) {
                    parseJson(myInputStream,myOutputStream); // check incomming json messages
                }
            } 
            catch (IOException ioException) {
                //Freedomotic.logger.severe(myXbmcSystem.getXbmcHost()+" : ioException from parseJson main loop"); //lost the stream probably, xbmc closed?
                myInputStream = null; //Stream has gone away get get new one
            }
        } while (true);
     }
     
     
    private void parseJson(InputStream inJsonStream, OutputStream outJsonStream) throws IOException {

        JsonFactory factory = new JsonFactory();
        JsonParser jsonParser = factory.createJsonParser(inJsonStream);
        jsonParser.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE); //stop jasonParser.close from closing stream
        
/* use the streaming json parse approach, faster?
 */
       while (true) {
           
            try {
                parseJsonElement(jsonParser);
                processJson();
            }
            
            catch(IOException ioException){ //lost the stream probably
                //Freedomotic.logger.severe(myXbmcSystem.getXbmcHost()+" : IO Exception in parseJson"); //lost the stream probably, xbmc closed?
                throw new IOException( "lost connection or bad data");
            }
       }
    }
    
    private void parseJsonElement (JsonParser jsonParser) throws IOException {

        JsonToken currentToken;
        String debugString = "";
        Integer countObject = 0;
        Integer countField = 0;
           
        do {
            if (jsonParser.isClosed()) {
                throw new IOException( "lost connection or bad data");
            }
            try {
                currentToken = jsonParser.nextToken(); // move to next 'token
                countField = countField + 1;
                lookupJsonField(jsonParser.getCurrentName(),jsonParser.getText(),countField);// lookup to see if we want the content
                debugString = debugString+ "||" + jsonParser.getCurrentName() + " | " + jsonParser.getText() + " | " + countField;
                if (currentToken == JsonToken.END_OBJECT) {
                    countObject = countObject-1;
                } else if (currentToken == JsonToken.START_OBJECT) {
                    countObject = countObject+1;                    
                }
            }
            
            catch(IOException ioException){
                throw new IOException( "lost connection or bad data");//lost the stream probably
            }
            
        } while (countObject != 0);
        System.out.println("Json = :: " + debugString); 

    }

    
    
    private void lookupJsonField (String currentFieldName , String jsonField, Integer countField) throws IOException {
        
        if (currentFieldName == null) {
        }
        else if (currentFieldName.equalsIgnoreCase("method")) {       
            if (jsonField.equals("System.OnQuit")) { // XBMC system closing down in an orderly way
                myXbmcSystem.setXbmcPower("false");
            } else if (jsonField.equals("Player.OnPlay")) {
                myXbmcSystem.setXbmcMethod("Play");
            } else if (jsonField.equals("Player.OnPause")) {
                myXbmcSystem.setXbmcMethod("Pause");
            } else if (jsonField.equals("Player.OnStop")) { // Something stopped playing, xbmc includes the type, not the player
                myXbmcSystem.setXbmcMethod("Stop");
            }
                
        } else if (currentFieldName.equalsIgnoreCase("type")) { 
                if (jsonField.equalsIgnoreCase("song")) {
                    myXbmcSystem.setXbmcPlayer("0");
                } else if (jsonField.equalsIgnoreCase("movie")) {
                    myXbmcSystem.setXbmcPlayer("1");
                } else if (jsonField.equalsIgnoreCase("episode")) {
                    myXbmcSystem.setXbmcPlayer("1");
                }  else if (jsonField.equalsIgnoreCase("unknown")) {
                    myXbmcSystem.setXbmcPlayer("1");
                } else if (jsonField.equalsIgnoreCase("picture")) {
                    myXbmcSystem.setXbmcPlayer("2");
                }                                              
        
        } else if (currentFieldName.equalsIgnoreCase("playerid")) { 
                myXbmcSystem.setXbmcPlayer(jsonField);
        
        } else if ((countField == 3 ) && (currentFieldName.equalsIgnoreCase("id"))) {  // id in this position means a response from xbmc
            if (jsonField.equalsIgnoreCase("pong")) {
                myXbmcSystem.setXbmcPower("true");
            }
        }
        
    }       

    
    private void processJson ()  {
        ProtocolRead event; 
        
 //       Freedomotic.logger.severe("xbmcPower = " + myXbmcSystem.getXbmcPower() + " | xbmcMethod = " + myXbmcSystem.getXbmcMethod() + " | xbmcPlayer = " + myXbmcSystem.getXbmcPlayer()); 
   
        if ((!myXbmcSystem.getXbmcMethod().equalsIgnoreCase("")) && (!myXbmcSystem.getXbmcPlayer().equalsIgnoreCase(""))) {
            event = new ProtocolRead(this, "xbmc", myXbmcSystem.getXbmcHost());
            event.addProperty("object.name", "XBMC-" + myXbmcSystem.getXbmcName());
            event.addProperty("object.class", "XBMC");
            event.addProperty("function", "player" + myXbmcSystem.getXbmcPlayer());
            event.addProperty("xbmcplayer" + myXbmcSystem.getXbmcPlayer(),myXbmcSystem.getXbmcMethod());
            Freedomotic.sendEvent(event);
        } 
        if (true) {  // always send a power status?
            event = new ProtocolRead(this, "xbmc", myXbmcSystem.getXbmcHost());
            event.addProperty("powered",myXbmcSystem.getXbmcPower());
            event.addProperty("object.name", "XBMC-"+myXbmcSystem.getXbmcName());
            event.addProperty("object.class", "XBMC");
            event.addProperty("function", "power"); 
            Freedomotic.sendEvent(event);
        }
        myXbmcSystem.setXbmcMethod("");
        myXbmcSystem.setXbmcPlayer("");
    }
    
    private void sendJsonPing() throws IOException {  // used for power detection on startup
        JsonFactory factory = new JsonFactory();
 
        JsonGenerator jsonGenerator = factory.createJsonGenerator(myOutputStream);
        jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET); //stop jasonGenerator.close from closing stream
        
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("jsonrpc", "2.0");
        jsonGenerator.writeStringField("method", "JSONRPC.Ping");
        jsonGenerator.writeStringField("id", "pong");
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
   
   }
    
     private void getActivePlayers() throws IOException {  // works but not needed?
        JsonFactory factory = new JsonFactory();
 
        JsonGenerator jsonGenerator = factory.createJsonGenerator(myOutputStream);
        jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET); //stop jasonGenerator.close from closing stream
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("jsonrpc", "2.0");
        jsonGenerator.writeStringField("method", "Player.GetActivePlayers");
        jsonGenerator.writeStringField("id", "GetActivePlayers");
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        
     }
 
     private void getPlayerStatus() throws IOException {   // WIP - may need it for startup condition?
        JsonFactory factory = new JsonFactory();
 
        JsonGenerator jsonGenerator = factory.createJsonGenerator(myOutputStream);
        jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET); //stop jasonGenerator.close from closing stream
        
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("jsonrpc", "2.0");
        jsonGenerator.writeStringField("method", "Player.GetProperties");
        jsonGenerator.writeStringField("id", "PlayerGetProperties");
        jsonGenerator.writeArrayFieldStart("params");
        jsonGenerator.writeStringField("playerid", "1");
        jsonGenerator.writeArrayFieldStart("properties");
        jsonGenerator.writeString("speed");
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
        
     }
}