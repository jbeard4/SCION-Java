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

function urlToModel(url){
    return scion.urlToModel(url);
} 

function documentStringToModel(s){
    return scion.documentStringToModel(s);
} 

function documentToModel(doc){
    return scion.documentToModel(doc);
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
