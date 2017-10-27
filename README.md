<h1 align="center">
  <img src="https://raw.githubusercontent.com/eru-scada/eru/develop/Eru/src/main/resources/images/Logo124x124.png" />
  Eru
  <br>
</h1>

<h4 align="center">The open JavaFX SCADA</h4>

| Development  | Master |  Chat  |  License  |
|:------:|:---------:|:---------:|:---------:|
| [![Build Status](https://travis-ci.org/assemblits/eru.svg?branch=develop)](https://travis-ci.org/assemblits/eru)  | [![Build Status](https://travis-ci.org/assemblits/eru.svg?branch=master)](https://travis-ci.org/assemblits/eru)  | [![Join the chat at https://gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby](https://badges.gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby.svg)](https://gitter.im/eru_An_open_linux_SCADA_based_on_JavaFX/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) | [![Apache 2.0 License](https://img.shields.io/badge/license-GPL3.0-green.svg)](https://github.com/assemblits/eru/blob/develop/LICENSE) |

<h1 align="center">
  <img src="https://raw.githubusercontent.com/eru-scada/eru/develop/Eru/src/main/resources/images/eru-screen-of-work.png" />
</h1>

## Resume
Eru allows easy monitoring of a process or machine operation by displaying a pictorial graphic that shows the operation of the process. It allows an operator to manipulate the process and system components in an easy intuitive manner. 

There are 4 main components in Eru:
  - Connections.
  - Devices.
  - Tags.
  - Displays.

  Basically, in the normal workflow, you:
  1) Create a "connection" to communicate with remote devices (Like PLC, Solar Panels, Engine controllers, Residence 
  automation (Doors, Lights, Windows, etc) etc).
  2) Create a "device" that uses that connection and organize addresses to read (E.g. in the windows device we want 
  to read  the current height of the window so we have to add an Address called *height* in the window device).
  3) Create a "tag" to represent the actual value of the Address of the device. (Using the same example, 
  we adjust the value obtained from the window to "meters" adding a factor and scale).
  4) Create a "displayName" to see the current height of the window (tag) graphically.

## Key Features

* Tag based
  - Tags to read devices. You can scale the value using offset or factors, masks, and custom scripts.
* JavaFX based
  - Use State of Art gauge creation techniques.
  - Remote clients using http tunnels.
* Open source.
* Dark/Light using CSS.
* Embedded Modbus (Serial or TCP). You do not need to get additional Modbus handler... It is embedded in Eru.
* Save the historic preview as PDF.
* Full screen mode
* Cross platform
  - Windows, Mac and Linux ready.
  
## How To Use

To clone and run this application, you'll need [Git](https://git-scm.com) and 
[Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
(which comes with [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)) installed on your computer. 
From your command line:

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
