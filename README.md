<h1 align="center">
  <img src="https://raw.githubusercontent.com/marlontrujillo1080/eru/new-toolboxes/lib/graphic-design/project-icon.png" />
  Eru Server
  <br>
</h1>

<h4 align="center">An open linux SCADA based on JavaFX.</h4>

## Resume
  This app has 4 main things:
  - Connections.
  - Devices.
  - Tags.
  - Users.

  Basically, in the normal workflow, you:
  1) Create a "connection" to communicate with remote devices (Like PLC, Solar Panels, Engine controllers, Residence automation (Doors, Lights, Windows, etc) etc).
  2) Create a "device" that uses that connection and organize addresses to read (E.g. in the windows device we want to read  the height of the window, so we have to add an address called height in the window device).
  3) Create a "tag" to represent the actual value of the address of the device. (Using the same example, we adjust the value obtained from the window to "meters" adding a factor and scale).
  4) Create a "user" this is to be sure that you have permissions to read the value (in construction).
  5) Create a "display" to see visualizations linked to the tag. (To draw the windows and see graphically if is closed or open) (in construction)

## Dependencies
  * Database
    - Postgresql installed on localhost with the following features:
      - Username = postgres (Default)
      - Password = postgres
      - Port     = 5432 (Default)
      - A database called "eru" in the public schema.
  * Modbus:
    - To be able to connect to the modbus devices (no necessary to run the app) install the drivers on the java lib. (TODO:more details)

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
# Clone this repository
$ git clone https://github.com/marlontrujillo1080/eru
```
You can use IntelliJ IDE to have the same experience of the developers...

## Credits

This software uses code from several open source packages.

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Open Dolphin](http://open-dolphin.org/dolphin_website/Home.html)
- [Medusa Project](https://github.com/marlontrujillo1080/Medusa)
- [Groovy](http://groovy-lang.org/)
- [Rafael Robles](https://github.com/Rafaelsk)
- [Lucio Guerchi](https://github.com/luHub)
- [Ronald Vera](https://www.linkedin.com/in/ronald-vera-2185b382/)

## In real world
[![Take a look!](https://img.youtube.com/vi/8DUAf9TrJuI/0.jpg)](https://www.youtube.com/watch?v=FHph2jrS0EU=47s)