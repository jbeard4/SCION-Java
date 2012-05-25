package com.inficon.scion;

import java.util.List;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import com.inficon.scion.SCION;

public class SCXML {
    private static final SCION scion = new SCION();
    private Scriptable interpreter;

    //utility function
    //TODO: define utility functions for File, Document, Document String
    public static Scriptable convertPathToSCXMLDocToModel(String path){
        Scriptable model = (Scriptable) SCXML.scion.createModelFromPath(path);
        return model;
    }

    //constructor
    public SCXML(Scriptable model){
        this.interpreter = (Scriptable) scion.createScionInterpreter(model);
    }   

    //starts the interpreter, returns a string list of state names
    //TODO: should make him a Set<String>
    public List<String> start(){
        return (List<String>) scion.startInterpreter(this.interpreter);
    }

    public List<String> gen(String eventName, Object eventData){
        return (List<String>) scion.genEvent(this.interpreter,eventName);
    }
}
