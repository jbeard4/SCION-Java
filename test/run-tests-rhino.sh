#!/bin/bash
npm install request #in case we don't have it

#start the server
java -cp lib/js.jar:build/class/ org.mozilla.javascript.tools.shell.Main -debug test/rhino-test-server.js &

#keep the pid (so we can kill it later)
serverpid=$!

sleep 1

#run the client
node js-lib/SCION/test/scxml-test-framework/lib/test-client.js js-lib/SCION/test/scxml-test-framework/test/*/*.scxml
status=$?

#kill the server
kill $serverpid

if [ "$status" = '0' ]; then echo SUCCESS; else echo FAILURE; fi;

