scionjs = build/js/SCION.js
scxmlclass = build/class/com/inficon/scion/SCXML.class
scionclass = build/class/com/inficon/scion/SCION.class
testclass = build/test/Test.class
scionjar = build/jar/scion.jar

all : jar 

#clone SCION
js-lib/SCION : 
	git submodule update --init --recursive

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


#use node and stitch to combine everything
#append extra stuff to the end
$(scionjs)  : js-src/appendToSCION.js node_modules/stitch js-lib/SCION
	mkdir -p build/js
	node js-src/build/stitch.js 
	cat js-src/appendToSCION.js >> $(scionjs)

#compile js to a big class using jsc
$(scionclass) : $(scionjs) lib/js.jar
	mkdir -p build/class
	java -cp lib/js.jar org.mozilla.javascript.tools.jsc.Main -opt 9 -extends java.lang.Object -package com.inficon.scion $(scionjs)
	mkdir -p build/class/com/inficon/scion/
	mv build/js/com/inficon/scion/SCION* build/class/com/inficon/scion/

#compile java 
$(scxmlclass) : $(scionclass) lib/js.jar
	javac -d build/class/ -classpath lib/js.jar:build/class \
		src/com/inficon/scion/SCXML.java \
		src/com/inficon/scion/SCXMLListener.java

$(testclass) : $(scxmlclass) lib/js.jar
	mkdir -p build/test
	javac -d build/class/ -classpath lib/js.jar:build/class test/TestSCXML.java

run-test : $(testclass) lib/js.jar
	mkdir -p build/jar
	java -classpath lib/js.jar:build/class TestSCXML

$(scionjar) : $(scxmlclass) $(scionclass)
	mkdir -p build/jar
	cd build/class && jar cf scion.jar com/inficon/scion/SCXML.class \
		com/inficon/scion/SCXMLListener.class \
		com/inficon/scion/SCION1.class \
		com/inficon/scion/SCION.class \
		&& mv scion.jar ../jar/

build/doc : src/com/inficon/scion/SCXML.java
	mkdir -p build/doc
	javadoc -d build/doc/ -classpath build/class/:lib/js.jar  src/com/inficon/scion/SCXML.java

#aliases
scion.js : $(scionjs)
scion.class : $(scionclass)
scxml.class : $(scxmlclass)
test.class : $(testclass)
jar : $(scionjar)
doc : build/doc
clean : 
	rm -rf build

.PHONY : scion.js scion.class scxml.class clean run-test get-deps clean-deps jar doc all
