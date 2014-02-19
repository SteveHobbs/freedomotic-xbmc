/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stevehobbs.xbmc.object;

import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.objects.ListBehaviorLogic;
import com.freedomotic.objects.impl.ElectricDevice;


/**
 *
 * @author Enrico
 */
public class XBMC extends ElectricDevice {
    
    public ListBehaviorLogic xbmcPlayer0;
    public ListBehaviorLogic xbmcPlayer1;
    public ListBehaviorLogic xbmcPlayer2;

    @Override
    public void init() {
              
         //linking this player property with the XBMC behavior defined in the XML
        xbmcPlayer0 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior("xbmcplayer0"));
        xbmcPlayer0.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                if (fireCommand) {
                    setXbmcPlayer0(params.getProperty("value"));  // not sure what this is
                } else {
                    setXbmcPlayer0(params.getProperty("value"));
                }
            }
        });
        registerBehavior(xbmcPlayer0);
        
        //linking this player property with the XBMC behavior defined in the XML
        xbmcPlayer1 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior("xbmcplayer1"));
        xbmcPlayer1.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                if (fireCommand) {
                    setXbmcPlayer1(params.getProperty("value"));
                } else {
                    setXbmcPlayer1(params.getProperty("value"));
                }
            }
        });
        registerBehavior(xbmcPlayer1);
        
        //linking this player property with the XBMC behavior defined in the XML
        xbmcPlayer2 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior("xbmcplayer2"));
        xbmcPlayer2.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                if (fireCommand) {
                    setXbmcPlayer2(params.getProperty("value"));
                } else {
                    setXbmcPlayer2(params.getProperty("value"));
                }
            }
        });
        registerBehavior(xbmcPlayer2);
        super.init();
    }

     public void setXbmcPlayer0(String value) {
        if (!xbmcPlayer0.getSelected().equals(value)) {
            xbmcPlayer0.setSelected(value);
            setIcon();
            setChanged(true);
        }
    }
     
      public void setXbmcPlayer1(String value) {
        if (!xbmcPlayer1.getSelected().equals(value)) {
            xbmcPlayer1.setSelected(value);
            setIcon();            
            setChanged(true);
        }
    }
   
     public void setXbmcPlayer2(String value) {
        if (!xbmcPlayer2.getSelected().equals(value)) {
            xbmcPlayer2.setSelected(value);
            setIcon();
            setChanged(true);
        }
    }  
     
   private void setIcon () {
       getPojo().setCurrentRepresentation(1);
       if (xbmcPlayer1.getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(2);    
       } else if (xbmcPlayer1.getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(3);    
       } else if (xbmcPlayer0.getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(4);
       } else if (xbmcPlayer0.getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(5);    
       } else if (xbmcPlayer2.getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(6);
       } else if (xbmcPlayer2.getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(7);    
       }
   }   
    
    @Override
    protected void createCommands() {
        
    }

    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
