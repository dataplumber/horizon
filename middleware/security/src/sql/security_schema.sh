#!/bin/sh

if [ $# = "0" ]; then
  ant -f build.xml
  exit 1
fi

ant -f build.xml $*
