# DT Watercooler Manager
This repository contains all elements needed to set up a minimal network automation demo on a local machine:
- A Netconf device simulation as a Java .jar file that represents the controlled device
- A docker-compose file for downloading an image of the ONAP[1] SDN[2] controller and launching it in a docker container
- The source code (as well as an executable .jar file) for a management application that steers the Netconf
  device via the SDNC. It also serves a simple web frontend for operation and usage of the device.
  
The simulated scenario is a watercooler with a tank that is filled through an operator-controlled pipe.
The task of the operator is to always ensure that the customer has access to fresh water.
The customer can tap different quantities of water from the tank. More details about the background for
this setup can be found in *xxxx*.

[1] [ONAP](https://www.onap.org/), Open Network Automation Platform; an Open Source project of the Linux Foundation

[2] SDN, Software Defined Networking. There are many explanatory sources, e.g. [Wikipedia](https://en.wikipedia.org/wiki/Software-defined_networking).

## The Watercooler Device

In this demo, the watercooler device represents a network element providing a service to an
imaginary customer (serving fresh water). To function properly, it needs to be controlled by
the automated network management provide by the SDNC and its "application layer", which is
represented by the watercooler manager application. The controlled device and the controller must
have a common language or protocol that allows for exchanging information and configuration data
between the machines. In this case, we are using [Netconf](https://en.wikipedia.org/wiki/NETCONF).
Netconf uses [YANG](https://en.wikipedia.org/wiki/YANG) models to describe all capabilities,
configuration and operational data, notifications and remote commands that a Netconf controlled
device supports. By retrieving the YANG models of a Netconf device, the controller has all
information needed to manage it. Even though Netconf is widespread, there are other options that
will not be discussed here.

The source code and further information for the Netconf controlled watercooler device is available
in another [github repository](https://github.com/51enra/netconf-watercooler). It bases upon the
[lighty.io netconf simulator](https://github.com/PANTHEONtech/lighty-netconf-simulator).

To start the watercooler device simulator, open a terminal window and go to the root directory
of this repository. Enter
`java -jar jars/dt-watercooler-device-1.0.0-SNAPSHOT-jar-with-dependencies.jar`.
You will see the startup log messages of the watercooler device and you can continue
observing the device's log messages in this window. The device can be stopped by hitting
``<ctrl>-c``.

### Additional Information

In Netconf terminology, the watercooler device operates as a Netconf server, while the SDNC provides
the Netconf client functionality in our case. Besides steering it via the SDNC, it is also possible to interact
directly with the watercooler device using a Netconf client application.

It may also be instructive to take a look at the watercooler device's YANG model and to
review how the functionality is implemented.

Further information on this can be found in the watercooler device's
[github repository](https://github.com/51enra/netconf-watercooler). Instead of using the
´.jar´ file mentioned above, you can run the watercooler device out of an IDE to gain additional insights.

## The SDN Controller

In a realistic software defined network, the task of the SDN controller is to provide a common interface
to network elements of different types and potentially from various vendors comprising the network.
On this *"southbound"* interface, the SDNC may have to communicate via different protocols (e.g. Netconf, OpenFlow),
depending on the specific network elements to be controlled. The network may consist of thousands of devices.

In the *"northbound"* direction, the SDNC provides an interface that abstracts the specific details of the network
elements to a certain degree. It allows the application layer to "program" the network dynamically in the most suitable way
to optimally provide the various services that can be realized through the combination of network elements.

In this demonstration, there is only a single network element to be controlled (the watercooler device). Nevertheless,
we keep the SDNC in the implementation to stay closer to the realistic scenario. We are using an open source
SDNC implementation provided as a docker image by the ONAP community (see above).

At the core of the ONAP SDNC is the [OpenDaylight platform](https://www.opendaylight.org/), wrapped with ONAP specific
functionality and with a range of pre-activated features. Regarding functionality, OpenDaylight would be absolutely
sufficient for our project. However, when using plain OpenDaylight, we would at least have to activate the required
Netconf support features and build an own image. Because ONAP as well as OpenDaylight are open-source projects, it is
of course possible to modify functionality and build own images, but this is beyond the scope here. The simplest
solution is taking the ready-for-use ONAP SDNC image.

The task of the SDNC is, in our case, to receive REST requests via its northbound interface from the watercooler
manager and issue the corresponding Netconf requests via its southbound interface towards the watercooler device.

To launch the SDNC, docker (or similar) should be available on your machine. Then you can simply run the docker-compose
file from a terminal having open the root directory of this repository: ``docker-compose up -d``. When launching for the first time, you should
see the ``onap/sdnc-image`` being pulled, which may take a while. After the SDNC container has been started, open the
page ``http://localhost:8181/apidoc/explorer/index.html`` in a web browser. It may take a few minutes before the SDNC
is ready to answer. Reload if the page is not yet available. Eventually, you will get a login request. The username is
`admin` and the password is also `admin`. The OpenApi documentation page of the SDNC REST interface (northbound) will open.

If you want to stop the SDNC, enter ``docker-compose down`` in your terminal.

### Additional Information

* The ONAP SDNC is documented in the
  [ONAP Wiki](https://wiki.onap.org/display/DW/Software+Defined+Network+Controller+Project). Unfortunately, it is
  often difficult to find specific desired information in the ONAP documentation.
* There is comprehensive documentation of the OpenDaylight project available. In the current context, for example the
  [Netconf support](https://docs.opendaylight.org/projects/netconf/en/latest/index.html) is particularly relevant.
* As a side note, the [YANG tools](https://docs.opendaylight.org/en/latest/developer-guides/yang-tools.html)
  written in the context of the OpenDaylight project are also employed by the Lighty.io Netconf simulator used by the
  watercooler device. The YANG tools parse the YANG models in the background, provide in-memory storage for their
  parameters and make the various parameters, requests and other elements accessible through Java code.
  
### SDNC Interface Testing

You can retrieve information from the SDNC by sending requests via the OpenApi documentation page.
You can also trigger the SDNC to communicate with the Netconf device by using requests on the OpenApi documentation page.
Later on, the watercooler manager will handle the communication with the SDNC. If you are currently not interested in
the details of the SDNC interfaces, you can skip this part. In that case, please continue reading in the section below
on the watercooler manager.

**Prerequisites:** The watercooler device has been started. The SDNC container is running, and you have opened the
OpenApi documentation page in your browser.

1. Find the section "network-topology" on the web page and expand it by clicking the arrow at the right-hand side.
2. Find the blue entry that says "GET - network-topology - topology" in small print at the end of the line. Here,
    you can send a REST GET request to a specific endpoint of the SDNC API that shows the network topology
   information currently available to the SDNC. Multiple separate topologies can be managed by the
   SDNC. They are distinguished by topology IDs.
3. Expand the request by clicking anywhere in the blue area. For Netconf devices, there is a pre-defined topology
   with the id "topology-netconf". We will retrieve information about this topology now. Click on "Try it out",
   then enter "topology-netconf" in the entry field for the topology id and click on execute. As no netconf device
   is known to the SDNC yet, the topology is empty. You can see this further below in the field "Response body":
```
<topology xmlns="urn:TBD:params:xml:ns:yang:network-topology">
  <topology-id>topology-netconf</topology-id>
</topology>
```  
4. Next, we connect the SDNC with the watercooler device, which is called mounting the Netconf device. Scroll down
   to the orange entry that says "PUT - network-topology - node" in small print at the end of the line. Expand it by
   clicking in the orange area, click "Try it out", enter "topology-netconf" as topology id and "watercooler" as node id.
   Make sure that the dropdown field to the right of the text "Request body" says "application/json".
   Delete everything in the field "node_config" and paste the following. Replace ``X.X.X.X`` by the IPv4 address of
   your computer. 
 ```
   {
    "node": [
        {
          "node-id": "watercooler",
          "username": "admin",
          "password": "admin",
          "host": "X.X.X.X",
          "port": 17830,
          "tcp-only": false
        }
       ]
    }
 ```
5.  When you scroll down to the responses area, you should see a response code 201, which confirms that
    the device has been successfully added to the topology. In the terminal window showing the log of the watercooler
    device, you should see a line saying `Session admin@/X.X.X.X:Y authenticated`. For ``X.X.X.X``, it should show
    the IPv4 address as above and for `Y` a random port number. If this didn't work, the SDNC and the watercooler
    device cannot communicate for some reason.
6.  Go back to the browser window, scroll up to the GET request "GET - network-topology - topology" and repeat step 2.
    Select "application/json" in the dropdown field in the Responses section of the blue area (only
    for easier comparison with the response reproduced below; the xml response format is 
    equivalent). Now, the response should be (with several more capabilities listed in the place
    of ``....``):
    ```
    {
      "network-topology:topology": [
        {
          "topology-id": "topology-netconf",
          "node": [
              {
                "node-id": "watercooler",
                "netconf-node-topology:port": 17830,
                "netconf-node-topology:available-capabilities": {
                "available-capability": [
                    {
                    "capability": "urn:ietf:params:netconf:capability:candidate:1.0",
                    "capability-origin": "device-advertised"
                    },
                    ....
                    {
                    "capability": "(urn:dt-network-automation-demo:watercooler?revision=2022-03-02)watercooler",
                    "capability-origin": "device-advertised"
                    }
                ]
              },
              "netconf-node-topology:tcp-only": false,
              "netconf-node-topology:username": "admin",
              "netconf-node-topology:password": "admin",
              "netconf-node-topology:connection-status": "connected",
              "netconf-node-topology:host": "X.X.X.X"
            }
          ]
        }
      ]
    }
    ```
    Each capability corresponds to a YANG model describing some behavior of our device. The ``watercooler``
    capability describes the specific properties of our device. All other capabilities describe the base
    behavior of netconf servers as far as they are supported by the simulator.
    
7. At the top of the web page, you find a black bar. At the right, there is a dropdown menu labeled
   "Select controller/mounted resources of specific RestConf version". This allows to switch between
   the REST APIs of the SDN controller itself (where we are now), and the REST APIS for the different mounted
   devices. When you expand
   the dropdown menu, you will see four entries, two for the SDNC API and two for the watercooler
   device API. The two versions per API represent slightly different flavors; the difference is not
   of interest here. Select the entry "topology-netconfnode resources -
   RestConf RFC 8040". If you receive an error message, refresh the page.

8. Via the API that is displayed now, control of the watercooler device is possible.
   The SDNC will translate every REST request received on this API into the corresponding Netconf
   request and will send it to the watercooler device.
   It will process the response, and provide a response to the REST request depending on the Netconf
   response received. The watercooler manager described below sends requests to this API. As
   a simple example, we will retrieve the configuration and operational data from the watercooler device.
   Expand the menu entry "watercooler" by clicking on the arrow at the right. Expand the blue area labeled
   "GET - watercooler - watercooler - watercooler" [3] in small print. As before, expand the blue
   area, click on "Try it out", select "application/json" and "execute". the response should look as
   follows:
   ```
   {
    "watercooler:watercooler": {
        "refillRate": 0,
        "watercoolerManufacturer": "iHub",
        "overflowIndicator": "off",
        "fillLevel": 10,
        "watercoolerModelNumber": "WatercoolerDeluxe22"
    }
   }
   ```
   This is the translation to JSON of the Netconf response received from the watercooler device. When you send
   this request while the watercooler device is being controlled by the watercooler manager, you may see a
   different refill rate, fill level or overflow indicator status. The elements "watercoolerManufacturer" and
   "watercoolerModelNumber" are "vendor information" set by the watercooler device itself and cannot be modified
   by the client.

[3] The three repetition of "watercooler" are caused by our naming. The first is the node id that we
defined when mounting the device. The second is the name of the Netconf capability we are
addressing, see above at step 6. It corresponds to a specific YANG model. The third is the name of a
"data container" which is defined inside the YANG model and which contains the data we are looking for.

## The Watercooler Manager

The watercooler manager represents the application layer of our demo. In general, the application layer
includes applications providing the business logic, network automation engines, interfaces
to third-party systems, operation and management systems, customer self-service etc. In our simplistic
scenario, we have a single Java app that includes a control loop for interaction with the watercooler
device via the SDNC. It also serves a web page that represents at the same time the "customer" and the
"operator" user interface.

The source code of the watercooler manager is available in this repository.
To have full flexibility, it should be opened in an IDE and run from there. Further details on this,
as well as an overview of the code structure, is provided further below. To try out the network
automation demo, it is sufficient to start the application from its `.jar` file as described next.

**Prerequisites:** The watercooler device has been started. The SDNC container is running.

In a new terminal window, open the root path of this repository. Then execute
` java -jar jars/dt-watercooler-manager-1.0.0-SNAPSHOT.jar --netconf.ipaddress=X.X.X.X`
where `X.X.X.X` is the IPv4 address of your computer in the local network. You should see the startup log
messages in the terminal window. Make sure that your IPv4 address appears in the log message
"Watercooler device IP address: X.X.X.X". The application blocks the terminal window
until you terminate it with ``<ctrl>-c``.

Now go to your web browser and open the page ``http://localhost:8086/home``. This is
the frontend of the watercooler manager. The customer elements are
grouped in the upper part; the operator elements in the lower part. The left-hand side
shows status information about the watercooler device when the manager is 
connected to the device. To connect, click on "mount" in the operator section
(the SDNC as well as the watercooler device must be running). The "green light" next to the "mount"
button indicates that the mounting is completed. Directly after mounting, the watercooler manager will
start its control loop that periodically checks the status of the watercooler device.

The "operational elements" shown on the web page can now be used to control the watercooler device:
- The fill level bar shows the amount of water currently stored by the watercooler.
- Refilling can be done by the "operator" by setting a fillrate manually or by starting the "auto-refill".
  - In "auto-refill" mode, the watercooler manager will define the fill rate depending on the current fill level.
    The fill rate will be reduced when the tank becomes fuller and will be set to 0 before an overflow occurs.
  - When a manual fill rate is set and not set back to 0 before the tank is full (99%), the "overflow light" will turn on.
- The "customer" can tap different quantities of water from the tank by pressing the respective buttons. A successful
  tap will be acknowledged by a "green light". The fill level will go down accordingly. If the fill level is not
  sufficient for the desired quantity, a "red light" will be shown, and the fill level is not reduced.
- The "error light" indicates that the communication with the SDNC failed, e.g. because the SDNC is down, or that the
  SDNC returned an error message because it could not reach the previously mounted watercooler device. Note that there
  will be no error indication when a wrong watercooler device ip address is used, because the SDNC does not return an
  error message. However, the mount process will not complete.

### Further Exploration

In the following, you will find some hints that may be helpful for expanding or modifying the functionality of the
watercooler manager. It is a [Spring Boot](https://spring.io/projects/spring-boot) application. Many tutorials and
coding hints for Spring Boot can be found in the Internet. Spring or Spring Boot will not be discussed here.

- For more detailed logging, remove the comments from the two corresponding lines in the 
  `src/main/resourcesapplication.properties` file.
- If you enable `logging.level.reactor.netty.http.client=DEBUG`, every single request and response exchanged with the SDNC
  will be logged in detail. When the control loop runs, it is configured to send a request every second. The period
  can be extended by modifying the constant `CONTROL_LOOP_PERIOD_MS` in the class `WatercoolerService`. Alternatively,
  the control loop can be completely disabled to try single requests.
- The web page elements are kept up to date by a JavaScript snippet that runs every 500 ms. This can be disabled or
  delayed via the line `setInterval("refreshElements();", 500);` in `src/main/resources/static/js/refresh.js`.
  
Short overview of the classes of the watercooler manager application:
- `WebClientConfig`: Sets up the beans with the parameters for access to the SDNC REST interface and through it to the
   watercooler Netconf device.
- `HomeController`: Delivery of the web page and button logic. The button-press and manual refill rate information is
  transfered in the `UserInputDTO`. 
- `RefreshController`: Delivery of the current status from the `WatercoolerService` to the JavaScript. Status information
  is transferred in the `RefreshDTO`.
- `SdncApiService`: Handles the REST API towards the SDNC.
- `WatercoolerService`: Provides all services to manage the watercooler device, runs the control loop and provides
  current status information about the watercooler device.
- The classes in the directory ``model`` provide the containers for the data of the different JSON structures
  in the requests and responses exchanged with the SDNC.