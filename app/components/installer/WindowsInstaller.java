/*
 * Copyright (c) 2014-2015 University of Ulm
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package components.installer;

import de.uniulm.omi.cloudiator.sword.api.remote.RemoteConnection;
import de.uniulm.omi.cloudiator.sword.api.remote.RemoteException;
import models.Tenant;
import models.VirtualMachine;
import play.Logger;
import play.Play;
import util.logging.Loggers;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * todo clean up class, do better logging
 */
public class WindowsInstaller extends AbstractInstaller {

    private static final Logger.ALogger LOGGER = Loggers.of(Loggers.INSTALLATION);
    private final String homeDir;
    private static final String JAVA_EXE = "jre8.exe";
    private static final String SEVEN_ZIP_ARCHIVE = "7za920.zip";
    private static final String SEVEN_ZIP_DIR = "7zip";
    private static final String SEVEN_ZIP_EXE = "7za.exe";
    private static final String VISOR_BAT = "startVisor.bat";
    private static final String AXE_BAT = "startAxeAggregator.bat";
    private static final String RMI_BAT = "startRmiRegistry.bat";
    private static final String KAIROSDB_BAT = "startKairos.bat";
    private static final String LANCE_BAT = "startLance.bat";
    private static final String JAVA_DOWNLOAD =
        Play.application().configuration().getString("colosseum.installer.windows.java.download");
    private static final String SEVEN_ZIP_DOWNLOAD =
        Play.application().configuration().getString("colosseum.installer.windows.7zip.download");
    private final String user;
    private final String password;
    private final Tenant tenant;



    public WindowsInstaller(RemoteConnection remoteConnection, VirtualMachine virtualMachine,
        Tenant tenant) {
        super(remoteConnection, virtualMachine);

        this.user = virtualMachine.loginName().get();
        checkArgument(virtualMachine.loginPassword().isPresent(),
            "Expected login password for WindowsInstaller");
        this.password = virtualMachine.loginPassword().get();

        this.homeDir = "C:\\Users\\" + this.user;
        this.tenant = tenant;

    }

    @Override public void initSources() {

        //java
        this.sourcesList.add("powershell -command (new-object System.Net.WebClient).DownloadFile('"
            + WindowsInstaller.JAVA_DOWNLOAD + "','" + this.homeDir + "\\"
            + WindowsInstaller.JAVA_EXE + "')");
        //7zip
        this.sourcesList.add("powershell -command (new-object System.Net.WebClient).DownloadFile('"
            + WindowsInstaller.SEVEN_ZIP_DOWNLOAD + "','" + this.homeDir + "\\"
            + WindowsInstaller.SEVEN_ZIP_ARCHIVE + "')");
        //download visor
        this.sourcesList.add("powershell -command (new-object System.Net.WebClient).DownloadFile('"
            + WindowsInstaller.VISOR_DOWNLOAD + "','" + this.homeDir + "\\"
            + WindowsInstaller.VISOR_JAR + "')");
        //download kairosDB
        this.sourcesList.add("powershell -command (new-object System.Net.WebClient).DownloadFile('"
            + WindowsInstaller.KAIROSDB_DOWNLOAD + "','" + this.homeDir + "\\"
            + WindowsInstaller.KAIROSDB_ARCHIVE + "')");
        //lance
        this.sourcesList.add("powershell -command (new-object System.Net.WebClient).DownloadFile('"
            + WindowsInstaller.LANCE_DOWNLOAD + "','" + this.homeDir + "\\"
            + WindowsInstaller.LANCE_JAR + "')");


    }

    @Override public void installJava() throws RemoteException {

        LOGGER.debug("Installing Java...");
        this.remoteConnection.executeCommand(
            "powershell -command " + this.homeDir + "\\jre8.exe /s INSTALLDIR=" + this.homeDir
                + "\\" + WindowsInstaller.JAVA_DIR);

        //Set JAVA envirnonment vars, use SETX for setting the vars for all future session use /m for machine scope
        remoteConnection.executeCommand(
            "SETX PATH %PATH%;" + this.homeDir + "\\" + WindowsInstaller.JAVA_DIR + "\\bin /m");
        remoteConnection.executeCommand(
            "SETX JAVA_HOME " + this.homeDir + "\\" + WindowsInstaller.JAVA_DIR + " /m");

        LOGGER.debug("Java successfully installed!");


    }

    private void install7Zip() throws RemoteException {
        LOGGER.debug("Unzipping 7zip...");
        this.remoteConnection.executeCommand(
            "powershell -command & { Add-Type -A 'System.IO.Compression.FileSystem'; [IO.Compression.ZipFile]::ExtractToDirectory('"
                + this.homeDir + "\\" + WindowsInstaller.SEVEN_ZIP_ARCHIVE + "', '" + this.homeDir
                + "\\" + WindowsInstaller.SEVEN_ZIP_DIR + "'); }");
        LOGGER.debug("7zip successfully unzipped!");
    }

    @Override
    public void startRmiRegistry() throws RemoteException {

        LOGGER.debug(String.format("Starting RMIRegistry started on vm %s", virtualMachine));

        //id of the visor schtasks
        String rmiJobId = "rmi-registry-job";

        //change environment variable
        this.remoteConnection.executeCommand(
                "SETX CLASSPATH %CLASSPATH%;" + this.homeDir + "\\" + WindowsInstaller.AXE_JAR +
                ";" + this.homeDir + "\\" + WindowsInstaller.LANCE_JAR + "; /m");

        //create a .bat file to start visor, because it is not possible to pass schtasks paramters using overthere
        String startCommand =
                this.homeDir + "\\" + WindowsInstaller.JAVA_DIR + "\\bin\\rmiregistry.exe";
        this.remoteConnection
                .writeFile(this.homeDir + "\\" + WindowsInstaller.RMI_BAT, startCommand, false);

        //set firewall rules
        this.remoteConnection.executeCommand(
                "powershell -command netsh advfirewall firewall add rule name = 'RMI Registry Port' dir = in action = allow protocol=TCP localport="
                        + Play.application().configuration()
                        .getString("colosseum.installer.abstract.lance.rmiPort"));


        //create schtaks
        this.remoteConnection.executeCommand("schtasks.exe " +
                "/create " +
                "/st 00:00  " +
                "/sc ONCE " +
                "/ru " + this.user + " " +
                "/rp " + this.password + " " +
                "/tn " + rmiJobId +
                " /tr \"" + this.homeDir + "\\" + WindowsInstaller.RMI_BAT + "\"");
        this.waitForSchtaskCreation();
        //run schtask
        this.remoteConnection.executeCommand("schtasks.exe /run /tn " + rmiJobId);


        LOGGER.debug(String.format("RMIRegistry was successfully started on vm %s", virtualMachine));
    }

    @Override public void installVisor() throws RemoteException {

        LOGGER.debug("Setting up and starting Visor");

        //create properties file
        this.remoteConnection.writeFile(this.homeDir + "\\" + WindowsInstaller.VISOR_PROPERTIES,
            this.buildDefaultVisorConfig(), false);

        //id of the visor schtasks
        String visorJobId = "visor";

        //create a .bat file to start visor, because it is not possible to pass schtasks paramters using overthere
        String startCommand =
            "java -jar " + this.homeDir + "\\" + WindowsInstaller.VISOR_JAR + " -conf "
                + this.homeDir + "\\" + WindowsInstaller.VISOR_PROPERTIES;
        this.remoteConnection
            .writeFile(this.homeDir + "\\" + WindowsInstaller.VISOR_BAT, startCommand, false);

        //set firewall rules
        this.remoteConnection.executeCommand(
            "powershell -command netsh advfirewall firewall add rule name = 'Visor Rest Port' dir = in action = allow protocol=TCP localport="
                + Play.application().configuration()
                .getString("colosseum.installer.abstract.visor.config.restPort"));
        this.remoteConnection.executeCommand(
            "powershell -command netsh advfirewall firewall add rule name = 'Visor Telnet Port' dir = in action = allow protocol=TCP localport="
                + Play.application().configuration()
                .getString("colosseum.installer.abstract.visor.config.telnetPort"));


        //create schtaks
        this.remoteConnection.executeCommand("schtasks.exe " +
            "/create " +
            "/st 00:00  " +
            "/sc ONCE " +
            "/ru " + this.user + " " +
            "/rp " + this.password + " " +
            "/tn " + visorJobId +
            " /tr \"" + this.homeDir + "\\" + WindowsInstaller.VISOR_BAT + "\"");
        this.waitForSchtaskCreation();
        //run schtask
        this.remoteConnection.executeCommand("schtasks.exe /run /tn " + visorJobId);

        LOGGER.debug("Visor started successfully!");

    }

    @Override public void installKairosDb() throws RemoteException {

        LOGGER.debug("Extract, setup and start KairosDB...");
        //extract kairosdb
        this.remoteConnection.executeCommand(
            "powershell -command " + this.homeDir + "\\" + WindowsInstaller.SEVEN_ZIP_DIR + "\\"
                + WindowsInstaller.SEVEN_ZIP_EXE + " e " + this.homeDir + "\\"
                + WindowsInstaller.KAIROSDB_ARCHIVE + " -o" + this.homeDir);
        String kairosDbTar = WindowsInstaller.KAIROSDB_ARCHIVE.replace(".gz", "");
        this.remoteConnection.executeCommand(
            "powershell -command " + this.homeDir + "\\" + WindowsInstaller.SEVEN_ZIP_DIR + "\\"
                + WindowsInstaller.SEVEN_ZIP_EXE + " x " + this.homeDir + "\\" + kairosDbTar + " -o"
                + this.homeDir);

        //set firewall rule
        this.remoteConnection.executeCommand(
            "powershell -command netsh advfirewall firewall add rule name = 'Kairos Port' dir = in action = allow protocol=TCP localport="
                + Play.application().configuration()
                .getString("colosseum.installer.abstract.visor.config.kairosPort"));

        //create a .bat file to start kairosDB, because it is not possible to pass schtasks paramters using overthere
        String startCommand =
            this.homeDir + "\\" + WindowsInstaller.KAIRROSDB_DIR + "\\bin\\kairosdb.bat run ";
        this.remoteConnection
            .writeFile(this.homeDir + "\\" + WindowsInstaller.KAIROSDB_BAT, startCommand, false);

        //start kairosdb in backround
        String kairosJobId = "kairosDB";
        this.remoteConnection.executeCommand("schtasks.exe /create " +
            "/st 00:00  " +
            "/sc ONCE " +
            "/ru " + this.user + " " +
            "/rp " + this.password + " " +
            "/tn " + kairosJobId + " " +
            "/tr \"" + this.homeDir + "\\" + WindowsInstaller.KAIROSDB_BAT + "\"");
        this.waitForSchtaskCreation();
        this.remoteConnection.executeCommand("schtasks.exe /run /tn " + kairosJobId);
        LOGGER.debug("KairosDB successfully started!");


    }

    @Override public void installLance() throws RemoteException {
        LOGGER.error("Setting up Lance...");

        LOGGER.error("Opening Firewall ports for Lance...");
        this.remoteConnection.executeCommand(
            "powershell -command netsh advfirewall firewall add rule name = 'Lance RMI' dir = in action = allow protocol=TCP localport="
                + Play.application().configuration()
                .getString("colosseum.installer.abstract.lance.rmiPort"));
        this.remoteConnection.executeCommand(
            "powershell -command netsh advfirewall firewall add rule name = 'Lance Server' dir = in action = allow protocol=TCP localport="
                + Play.application().configuration()
                .getString("colosseum.installer.abstract.lance.serverPort"));

        //create a .bat file to start Lance, because it is not possible to pass schtasks paramters using overthere
        String startCommand = " java " +
            " -Dhost.ip.public=" + this.virtualMachine.publicIpAddress().get().getIp() +
            " -Dhost.ip.private=" + this.virtualMachine.privateIpAddress(true).get().getIp() +
            " -Djava.rmi.server.hostname=" + this.virtualMachine.publicIpAddress().get().getIp() +
            " -Dhost.vm.id=" + this.virtualMachine.getUuid() +
            " -Dhost.vm.cloud.tenant.id=" + this.tenant.getUuid() +
            " -Dhost.vm.cloud.id=" + this.virtualMachine.cloud().getUuid() +
            " -jar " + this.homeDir + "\\" + WindowsInstaller.LANCE_JAR;
        this.remoteConnection
            .writeFile(this.homeDir + "\\" + WindowsInstaller.LANCE_BAT, startCommand, false);

        //start lance in backround
        String lanceJobId = "lance";
        this.remoteConnection.executeCommand("schtasks.exe " +
            "/create " +
            "/st 00:00  " +
            "/sc ONCE " +
            "/ru " + this.user + " " +
            "/rp " + this.password + " " +
            "/tn " + lanceJobId + " " +
            "/tr \"" + this.homeDir + "\\" + WindowsInstaller.LANCE_BAT + "\"");
        this.waitForSchtaskCreation();
        this.remoteConnection.executeCommand("schtasks.exe /run /tn " + lanceJobId);
        LOGGER.debug("Lance successfully started!");

    }

    @Override
    public void installAxe() throws RemoteException {
        LOGGER.debug("Setting up and starting Visor");

        //id of the visor schtasks
        String axeJobId = "axe-aggregator";

        //create a .bat file to start visor, because it is not possible to pass schtasks paramters using overthere
        String startCommand =
                "java -jar " + this.homeDir + "\\" + WindowsInstaller.AXE_JAR +
                        " -rmiPort=\"" + Play.application().configuration().getString("colosseum.installer.abstract.axe.config.rmiPort") + "\"" +
                        " -localDomainKairosIP=\"" + Play.application().configuration().getString("colosseum.installer.abstract.axe.config.localDomainKairosIP") + "\"" +
                        " -localDomainKairosPort=\"" + Play.application().configuration().getString("colosseum.installer.abstract.axe.config.localDomainKairosPort") + "\"" +
                        " -defaultKairosPort=\"" + Play.application().configuration().getString("colosseum.installer.abstract.axe.config.defaultKairosPort") + "\"";
        this.remoteConnection
                .writeFile(this.homeDir + "\\" + WindowsInstaller.AXE_BAT, startCommand, false);

        //set firewall rules
        this.remoteConnection.executeCommand(
                "powershell -command netsh advfirewall firewall add rule name = 'Axe Rmi Reg Port' dir = in action = allow protocol=TCP localport="
                        + Play.application().configuration()
                        .getString("colosseum.installer.abstract.axe.config.rmiPort"));

        //create schtaks
        this.remoteConnection.executeCommand("schtasks.exe " +
                "/create " +
                "/st 00:00  " +
                "/sc ONCE " +
                "/ru " + this.user + " " +
                "/rp " + this.password + " " +
                "/tn " + axeJobId +
                " /tr \"" + this.homeDir + "\\" + WindowsInstaller.AXE_BAT + "\"");
        this.waitForSchtaskCreation();
        //run schtask
        this.remoteConnection.executeCommand("schtasks.exe /run /tn " + axeJobId);

        LOGGER.debug("Axe started successfully!");
    }

    @Override public void installAll() throws RemoteException {

        LOGGER.debug("Starting installation of all tools on WINDOWS...");

        this.initSources();
        this.downloadSources();

        this.installJava();

        this.startRmiRegistry();

        this.installLance();

        this.install7Zip();
        this.installKairosDb();

        this.installVisor();
        this.installAxe();
    }

    private void waitForSchtaskCreation() {
        //Sleep 5 seconds to make sure the schtask creation is finished
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Error while waiting for schtask to be created!", e);
        }
    }
}
