# Remote Viewfinder
Remote Viewfinder for a DSLR camera.

## Overview
Uses a [JNAerated library and simple API](https://github.com/L28E/libgphoto2-jna) for native access to libgphoto2, forked from [here](https://github.com/angryelectron/libgphoto2-jna). 

The intent is for a WAR to be run off a headless Raspberry Pi. The camera is connected to the Pi, and the Pi will serve as an access point so devices can connect to it, and look though the viewfinder. This is a built-in feature of new DSLR's. This project is meant to be a simple, explorative, cross-brand solution.
