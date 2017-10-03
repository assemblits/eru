<h1 align="center">
  <img src="https://raw.githubusercontent.com/eru-scada/eru/develop/Eru/src/main/resources/images/Logo124x124.png" />
  Eru
  <br>
</h1>

<h4 align="center">The open JavaFX SCADA</h4>

| Development  | Master |  Chat  |
|:------:|:---------:|:---------:|
| [![Build Status](https://travis-ci.org/assemblits/eru.svg?branch=develop)](https://travis-ci.org/assemblits/eru)  | [![Build Status](https://travis-ci.org/assemblits/eru.svg?branch=master)](https://travis-ci.org/assemblits/eru)  | [![Join the chat at https://gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby](https://badges.gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby.svg)](https://gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
  
## Resume
  This app has 4 main things:
  - Connections.
  - Devices.
  - Tags.
  - Users.

  Basically, in the normal workflow, you:
  1) Create a "connection" to communicate with remote devices (Like PLC, Solar Panels, Engine controllers, Residence automation (Doors, Lights, Windows, etc) etc).
  2) Create a "device" that uses that connection and organize addresses to read (E.g. in the windows device we want to read  the height of the window, so we have to add an linkedAddress called height in the window device).
  3) Create a "tag" to represent the actual value of the linkedAddress of the device. (Using the same example, we adjust the value obtained from the window to "meters" adding a factor and scale).
  4) Create a "user" this is to be sure that you have permissions to read the value (in construction).
  5) Create a "display" to see visualizations linked to the tag. (To draw the windows and see graphically if is closed or open) (in construction)

## Dependencies
  * Database
    - H2 database.
  * Modbus:
    - To be able to connect to the serial modbus devices (no necessary to run the app) install the drivers in the java environment. Linux (Debian) example:
      1) Go to: _eru_home_directory / lib / serial / Rxtx-Serial-comms-Drivers /_
      2) Choose your binary build: x86_64 or i386 (based on which version of the JVM you are installing to)
      3) Extract.
      4) For a JDK installation on architecturei386 (For a JDK installation on architecture=x86_64, just change the i386 to amd64)
         * Copy RXTXcomm.jar        ---> <JAVA_HOME>/jre/lib/ext
         * Copy librxtxSerial.so    ---> <JAVA_HOME>/jre/lib/i386/
         * Copy librxtxParallel.so  ---> <JAVA_HOME>/jre/lib/i386/
         
         NOTE: You MUST match your architecture.  You can't install the i386version on a 64-bit version of the JDK and vice-versa.

## Key Features

* Tag based
  - Tags to read devices.
  - Tags to make calc.
  - Tags to have alarms.
  - Tags to record historic values.
* JavaFX based
  - Use State of Art gauge creation techniques.
  - Remote clients using http tunnels.
* Open source.
* Dark/Light using CSS.
* Embedded Modbus (Serial or TCP).
* Save the historic preview as PDF
* App will keep alive in tray for quick usage
* Full screen mode
* Cross platform
  - Windows, Mac and Linux ready.
  
## How To Use

To clone and run this application, you'll need [Git](https://git-scm.com) and [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (which comes with [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)) installed on your computer. From your command line:

```bash
# Go to your workspace
cd ~/Workspace
# Create a folder called "eru" (Recommended)
mkdir eru
# Go there
cd eru/
# Clone this repository
git clone https://github.com/assemblits/eru.git
```
You can use IntelliJ IDE to have the same experience of the developers:

1) Launch IJ.
2) Click import project.
3) Select the Gradle Build file in the eru workspace.
4) Let IJ do the magic.

## Credits

This software uses code from several open source packages like:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Groovy](http://groovy-lang.org/)
- [Spring](https://spring.io/)
- [Gradle](https://gradle.org/)
- [Loombok](https://projectlombok.org/)
- [Log4J](https://logging.apache.org/log4j/)
- [Open Dolphin](http://open-dolphin.org/dolphin_website/Home.html)
- [Medusa Project](https://github.com/marlontrujillo1080/Medusa)
- [Jamod](http://jamod.sourceforge.net/index.html)
- [RXTX for Java](http://mfizz.com/oss/rxtx-for-java). Courtesy of [Mfizz, Inc. ](http://mfizz.com/)

Skills from:
- [Marlon Trujillo](https://github.com/marlontrujillo1080)
- [Gilberto Requena](https://github.com/gilbertojrequena)
- [Rafael Robles](https://github.com/Rafaelsk)
- [Lucio Guerchi](https://github.com/luHub)
- [Ronald Vera](https://www.linkedin.com/in/ronald-vera-2185b382/)

Contributions from:
- [Comelecinca Power Systems](www.comelecinca.com). Specially from [Octavio Casado](https://www.linkedin.com/in/octavio-casado-gonzalez-phd-2b1b2266/)

## In real world
[![Take a look!](https://img.youtube.com/vi/8DUAf9TrJuI/0.jpg)](https://www.youtube.com/watch?v=FHph2jrS0EU=47s)
