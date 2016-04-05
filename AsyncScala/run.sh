#!/bin/sh
sbt -Dconfig.file="AsyncScala/conf/dev.conf" ";project AsyncScala;~run"
