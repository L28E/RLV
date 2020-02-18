# RLV
Remove LiveView of a DSLR camera.

## Overview
Uses a [JNAerated library and simple API](https://github.com/L28E/libgphoto2-jna) for native access to libgphoto2, forked from [here](https://github.com/angryelectron/libgphoto2-jna). 

The intent is for a JAR to be run off a headless Raspberry Pi, to which the camera is connected. A phone may then be connected to the Pi to see a preview. This is a built-in feature of many new (and expensive) DSLR's. This project is meant to be a simple, explorative, cross-brand solution (for my cheapness).
