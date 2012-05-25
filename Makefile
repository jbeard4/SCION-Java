#java -cp ~/Downloads/rhino1_7R3/js.jar:. org.mozilla.javascript.tools.shell.Main -debug consumeSCION.js
#java -cp ~/Downloads/rhino1_7R3/js.jar:. org.mozilla.javascript.tools.debugger.Main -debug SCION.js 

get-deps:
	#update git submodules
	cd js-lib/SCION; git pull && git submodule init && git submodule update && git submodule status;
	#rhino
	mkdir -p lib
	wget ftp://ftp.mozilla.org/pub/mozilla.org/js/rhino1_7R3.zip
	unzip rhino1_7R3.zip
	mv rhino1_7R3/js.jar lib/
	rm -rf rhino1_7R3 rhino1_7R3.zip

	#SCION is already checked out for us thanks to git-submodules

	#npm install stitch

build :
	mkdir build

#use node and stitch to combine everything
#append extra stuff to the end
build/SCION.js : build js-src/appendToSCION.js
	node js-src/build/stitch.js 
	cat js-src/appendToSCION.js >> build/SCION.js

#compile js to a big class using jsc
build/com/inficon/scion/SCION.class : build/SCION.js
	java -cp lib/js.jar org.mozilla.javascript.tools.jsc.Main -extends java.lang.Object -package com.inficon.scion build/SCION.js

#compile java 
build/com/inficon/scion/SCXML.class : build/com/inficon/scion/SCION.class
	javac -d build/ -classpath lib/js.jar:build src/com/inficon/scion/SCXML.java

build/Test.class : build/com/inficon/scion/SCXML.class
	javac -d build/ -classpath lib/js.jar:build test/TestSCXML.java

run-test : build/Test.class
	java -classpath lib/js.jar:build TestSCXML

#TODO: jar everything

#aliases
scion.js : build/SCION.js
scion.class : build/com/inficon/scion/SCION.class
scxml.class : build/com/inficon/scion/SCXML.class
test.class : build/Test.class
clean : 
	rm -rf build

.PHONY : scion.js scion.class scxml.class clean run-test
