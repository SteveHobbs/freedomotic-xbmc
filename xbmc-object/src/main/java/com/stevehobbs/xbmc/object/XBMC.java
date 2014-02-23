/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stevehobbs.xbmc.object;

import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.objects.ListBehaviorLogic;
import com.freedomotic.objects.impl.ElectricDevice;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.CommandPersistence;
import java.util.ArrayList;
import java.util.List;


public class XBMC extends ElectricDevice {

    //NOTE: transitions from one object state to another are done executing a generic action
    //the aim of this class is to map state changes to generic actions, performed by the plugin 
    //linked to this object protocol.
    
    //set of behaviors (the possible states of this object)
    protected final static String BEHAVIOR_XBMCPLAYER0 = "xbmcplayer0";
    protected final static String BEHAVIOR_XBMCPLAYER1 = "xbmcplayer1";
    protected final static String BEHAVIOR_XBMCPLAYER2 = "xbmcplayer2";

    private final List<ListBehaviorLogic> players = new ArrayList<ListBehaviorLogic>();

    @Override
    public void init() {
        //linking this xml behavior to a state change listener
        final ListBehaviorLogic xbmcPlayer0 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_XBMCPLAYER0));
        xbmcPlayer0.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                //when an object state change is requested
                changeXbmcPlayerState(0, fireCommand, params);
            }
        });
        players.add(0, xbmcPlayer0);
        registerBehavior(xbmcPlayer0);

        //linking this xml behavior to a state change listener
        ListBehaviorLogic xbmcPlayer1 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_XBMCPLAYER1));
        xbmcPlayer1.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                changeXbmcPlayerState(1, fireCommand, params);
            }
        });
        players.add(1, xbmcPlayer1);
        registerBehavior(xbmcPlayer1);

        //linking this xml behavior to a state change listener
        ListBehaviorLogic xbmcPlayer2 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_XBMCPLAYER2));
        xbmcPlayer2.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                changeXbmcPlayerState(2, fireCommand, params);
            }
        });
        players.add(2, xbmcPlayer2);
        registerBehavior(xbmcPlayer2);

        //initialize the object 
        super.init();
    }

    //forces the related plugin to execute a command and then updates the object state 
    //according to the command execution result
    public void changeXbmcPlayerState(int playerId, boolean fireCommand, Config params) {
        //a real plugin action is requested
        if (fireCommand) {
            //add two useful properties to be readed by the xbmc plugin
            params.put("player.name", players.get(playerId).getName());
            params.put("player.action", params.getProperty("value"));
            //returns true if the command is executed succesfully, false otherwise
            if (executeCommand("set xbmcplayer" + playerId, params)) {
                setXbmcPlayer(playerId, params.getProperty("value"));
            }
        //a trigger needs just to update the object state (no real commands to execute)
        } else {
            setXbmcPlayer(playerId, params.getProperty("value"));
        }
    }

    //changes only the object representation
    public void setXbmcPlayer(int id, String value) {
        if (!players.get(id).getSelected().equals(value)) {
            players.get(id).setSelected(value);
            setIcon();
            setChanged(true);
        }
    }

    private void setIcon() {
        getPojo().setCurrentRepresentation(1);
        if (players.get(1).getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(2);
        } else if (players.get(1).getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(3);
        } else if (players.get(0).getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(4);
        } else if (players.get(0).getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(5);
        } else if (players.get(2).getSelected().equals("Play")) {
            getPojo().setCurrentRepresentation(6);
        } else if (players.get(2).getSelected().equals("Pause")) {
            getPojo().setCurrentRepresentation(7);
        }
    }

    @Override
    protected void createCommands() {
        //create commands using upper level objects definitions
        super.createCommands();
        
        Command playSong = new Command();
        playSong.setName("play current song");
        playSong.setDescription("starts playing current song");
        playSong.setReceiver("app.events.sensors.behavior.request.objects");
        playSong.setProperty("object",getPojo().getName());
        playSong.setProperty("behavior", BEHAVIOR_XBMCPLAYER1);
        playSong.setProperty("value", "Play");
        
        //TODO: add more commands to be used in automations like "if party mode turns on then play current song"
        
        CommandPersistence.add(playSong);
    }

    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
