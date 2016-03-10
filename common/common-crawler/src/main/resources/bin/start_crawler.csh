#!/usr/csh -w
#
set i = 0;
set x = 0;
set name = ($*)
set command=''

foreach var ($argv)
    if("$argv[$i]" =~ "-cmd") then
        set x = 1
        break
    endif
        set i=`expr $i + 1`
end

if( $x == 1) then
        set q=`expr $i + 1`
        set command = "$argv[$q]"
        echo Running command with -Dexec.command=$command property
        set k = `expr $q + 1`
        set name = ($argv[1-$i] $argv[$k-])
endif

# Start GHRSST Reconciliation
java -Dexec.command="$command" gov.nasa.horizon.common.crawler.spider.util.GenericCrawler $name
