# Server setup

This document serves as a guide to proxying a Java application through to an IIS server on Windows Server. Feel free to
experiment with this guide and alter it. Personally, I'm more experienced with Linux servers; so there's undoubtedly
better ways to do this -- Chris.

## Software Requirements

The following will need to be installed on the server:

- [Java](http://openjdk.java.net)
- [NSSM](https://nssm.cc)
- [Microsoft Web Platform Installer](https://www.microsoft.com/web/downloads/platform.aspx)

## Setup Guide

- Install the software requirements listed above
- Add Java and Nssm executable to Windows PATH (optional but helps)

### IIS Setup

- Enable IIS if it's not already done. This is done from Windows Server Manager
    - Go to 'manage'
    - 'Add roles and features'
    - Follow the instruction wizard to install relevant IIS components
- Download and install the [Microsoft Web Platform Installer(https://www.microsoft.com/web/downloads/platform.aspx)
    - From this,
      install [Application Request Routing](https://www.iis.net/downloads/microsoft/application-request-routing). This
      will enable us to proxy our Java application from port 8080 (or whatever port) to 80

![ARR](resources/images/arr_screnshot.png)
<p style="text-align: center">
  <em>
    Screenshot of installing the Application Request Routing plugin. 
  </em>
</p>

- From IIS, open the Web Platform Installer (red icon)
    - Go to the "applications" tab and search for "Application Request Routing" (ARR) and choose version 3.0
    - Follow install instructions. **You may need to exit and reopen IIS for the ARR menu(s) to appear.**
- Delete the default site on IIS
- Create a blank site. You shouldn't need to change any values in the form
    - You need to give it a path -- however -- it doesn't really matter where you put it
    - Create an empty folder somewhere and point it there.
    - Make sure to bind the site to a specific hostname otherwise you can access it by IP as well.
- From the main server menu, click into ARR.
- On the right-hand side, click "server proxy settings"
    - Enable the proxy
    - Scroll to the field called "reverse proxy" and put the hostname and port of the application to proxy

![image](resources/images/server_proxy.png)
![image](resources/images/server_proxy_2.png)

### Java App Setup

- Place the JAR file of your application somewhere. A good place may just be the place where you pointed IIS to keep
  things simple.
- Create a windows service to start your app on restart / keep it running. Can be done via nssm.
- Run the following command from powershell

```powershell
nssm install <service_name> java -jar <path_to_jar> <additional_arguments>
```

For foodel, the following command was used:

```powershell
nssm install foodelserver java -jar C:\Foodel2\server.jar
```

The server won't start unless the service is edited slightly further. I belive it's because the startup directory needs
to be specified. You can launch a GUI to do this:

```powershell
nssm edit foodelserver
```

![img](resources/images/nssm.png)
![img](resources/images/nssm_io.png)

If you experience any issues, you can delete the service with the following:

```powershell
nssm remove <service_name>
```

You will need to stop the service before removing it.

To start / stop / pause the service, you can do this through the services application. Just search for "services" from
the start menu. Search for your service in the list and you should be able to start it etc from there.

![screenshot of services](resources/images/services_screenshot.png)
<p style="text-align: center">
  <em>
    screenshot of services
  </em>
</p>
