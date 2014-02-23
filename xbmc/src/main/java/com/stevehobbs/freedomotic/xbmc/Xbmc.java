package com.stevehobbs.freedomotic.xbmc;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.app.Freedomotic;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Xbmc extends Protocol {

    final int POLLING_WAIT;
    private static final Logger LOG = Logger.getLogger(Xbmc.class.getName());
    List<XbmcSystem> systemList = new ArrayList<XbmcSystem>();

    public Xbmc() {
        //every plugin needs a name and a manifest XML file
        super("Xbmc", "/xbmc/xbmc-manifest.xml");
        //read a property from the manifest file below which is in
        //FREEDOMOTIC_FOLDER/plugins/devices/it.freedomotic.xbmc/xbmc.xml
        POLLING_WAIT = configuration.getIntProperty("time-between-reads", 2000); // Not sure if needed?
        //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
        //default value if the property does not exist in the manifest
        setPollingWait(POLLING_WAIT); //millisecs interval between hardware device status reads

    }

    @Override
    protected void onStart() {

        String thisHost;
        Integer thisPort;
        Thread thisThread;

        loadXbmcSystems();

        for (XbmcSystem thisXbmcSystem : systemList) {
            thisThread = new Thread(new XbmcThread(thisXbmcSystem), "xbmcPluginThread" + systemList.indexOf(thisXbmcSystem));
            thisXbmcSystem.setXbmcThread(thisThread);
            thisXbmcSystem.getXbmcThread().start();
        }

        LOG.info("XBMC plugin is started");
    }

    @Override
    protected void onShowGui() {
        /**
         * uncomment the line below to add a GUI to this plugin the GUI can be
         * started with a right-click on plugin list on the desktop frontend
         * (it.freedomotic.jfrontend plugin)
         */
        //bindGuiToPlugin(new Xbmc(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {

        String thisHost;
        String thisState;
        Thread thisThread;

        for (XbmcSystem thisXbmcSystem : systemList) {
            thisHost = thisXbmcSystem.getXbmcHost();
            thisState = thisXbmcSystem.getXbmcThread().getState().toString();
            // System.out.println("Host : "+ thisHost + " State : " + thisState); // just checking to see if any die
        }

    }

    @Override
    protected void onStop() {
        LOG.info("XBMC plugin is stopped ");
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        LOG.info("XBMC plugin receives a command called " + c.getName()
                + " with parameters " + c.getProperties().toString());
    }

    @Override
    protected boolean canExecute(Command c) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void loadXbmcSystems() {
        Integer counter;
        String xbmcName;
        String xbmcHost;
        Integer xbmcPort;
        String xbmcLocation;
        String result;
        Integer numberOfSystems;

        numberOfSystems = configuration.getTuples().size();

        for (counter = 0; counter < numberOfSystems; counter++) {
            result = configuration.getTuples().getProperty(counter, "System");
            if (result != null) {
                xbmcHost = configuration.getTuples().getStringProperty(counter, "XBMCHost", "localhost");
                xbmcPort = configuration.getTuples().getIntProperty(counter, "XBMCPort", 9090);
                xbmcName = configuration.getTuples().getStringProperty(counter, "System", "none");
                xbmcLocation = configuration.getTuples().getStringProperty(counter, "Location", "none");
                XbmcSystem xbmcSystem = new XbmcSystem(xbmcName, xbmcHost, xbmcPort, xbmcLocation);
                systemList.add(xbmcSystem);
            }
        }
    }

}
