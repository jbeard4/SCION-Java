//this gets appended to the top-level modules automatically by Makefile
var scion = require('scion');

function pathToModel(path){
    var model;
    scion.pathToModel(path,function(err,m){
        if(err) throw err;
        model = m; 
    });
    return model;
} 

//SCION exposes these APIs as async,
//but in Java world, they are all assumed to actually be blocking.
function urlToModel(url){
    var model;
    scion.urlToModel(String(url),function(err,m){
        if(err) throw err;
        model = m;
    });
    return model;
} 

function documentStringToModel(s){
    var model;
    scion.documentStringToModel(s,function(err,m){
        if(err) throw err;
        model = m;
    });
    return model;
} 

function documentToModel(doc){
    var model;
    scion.documentToModel(doc,function(err,m){
        if(err) throw err;
        model = m;
    });
    return model;
} 

function createScionInterpreter(model){
    return new scion.SCXML(model);
}

function startInterpreter(interpreter){
    return interpreter.start();
}

function genEvent(interpreter,name,data){
    return interpreter.gen(String(name),data);
}

function registerListener(interpreter,listener){
    return interpreter.registerListener(listener);
}

function unregisterListener(interpreter,listener){
    return interpreter.unregisterListener(listener);
}

function addActionHandler(namespace,localName,actionString){
    var actionTags = scion.ext.actionCodeGeneratorModule.gen.actionTags;
    var namespaceObject = actionTags[String(namespace)] || (actionTags[String(namespace)] = {});
    namespaceObject[String(localName)] = eval(String(actionString));
}

function setEvaluationContext(scxml,o){
    scxml.opts.evaluationContext = o;
}
