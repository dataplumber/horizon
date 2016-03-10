#!/bin/csh -f


java -Dlog4j.configuration=$LOGGER  gov.nasa.dmas.common.httpfetch.oceandata.ModisDataFetcher $*

