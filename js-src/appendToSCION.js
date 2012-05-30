//this gets appended to the top-level modules automatically by Makefile
var scion = require('core/scion'),
    console = require('rhino/util/console'),
    xml2jsonml = require('rhino/util/xml2jsonml');

function xmlDocToJsonML(doc){

    var scxmlJson = xml2jsonml.xmlDocToJsonML(doc); 

    var annotatedScxmlJson = scion.annotator.transform(scxmlJson);

    var model = scion.json2model(annotatedScxmlJson); 

    return model;
}

function createScionInterpreter(model){
    return new scion.scxml.SimpleInterpreter(model);
}

function startInterpreter(interpreter){
    interpreter.start();
    return interpreter.getConfiguration();
}

function genEvent(interpreter,name,data){
    return interpreter.gen({name : name,data : data});
}
