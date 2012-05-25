#clone SCION
js-lib/SCION : 
	git clone --recursive git://github.com/jbeard4/SCION.git js-lib/SCION

#get rhino
lib/js.jar : 
	mkdir -p lib
	wget ftp://ftp.mozilla.org/pub/mozilla.org/js/rhino1_7R3.zip
	unzip rhino1_7R3.zip
	mv rhino1_7R3/js.jar lib/
	rm -rf rhino1_7R3 rhino1_7R3.zip

node_modules/stitch :
	mkdir -p node_modules
	npm install stitch

#any other deps
get-deps: js-lib/SCION lib/js.jar node_modules/stitch

clean-deps : 
	npm rm stitch
	rm lib/js.jar
	rm -rf js-lib/SCION

build :
	mkdir build

#use node and stitch to combine everything
#append extra stuff to the end
build/SCION.js : build js-src/appendToSCION.js node_modules/stitch js-lib/SCION
	node js-src/build/stitch.js 
	cat js-src/appendToSCION.js >> build/SCION.js

#compile js to a big class using jsc
build/com/inficon/scion/SCION.class : build/SCION.js lib/js.jar
	java -cp lib/js.jar org.mozilla.javascript.tools.jsc.Main -extends java.lang.Object -package com.inficon.scion build/SCION.js

#compile java 
build/com/inficon/scion/SCXML.class : build/com/inficon/scion/SCION.class lib/js.jar
	javac -d build/ -classpath lib/js.jar:build src/com/inficon/scion/SCXML.java

build/Test.class : build/com/inficon/scion/SCXML.class lib/js.jar
	javac -d build/ -classpath lib/js.jar:build test/TestSCXML.java

run-test : build/Test.class lib/js.jar
	java -classpath lib/js.jar:build TestSCXML

#TODO: jar everything

#aliases
scion.js : build/SCION.js
scion.class : build/com/inficon/scion/SCION.class
scxml.class : build/com/inficon/scion/SCXML.class
test.class : build/Test.class
clean : 
	rm -rf build

.PHONY : scion.js scion.class scxml.class clean run-test get-deps clean-deps
