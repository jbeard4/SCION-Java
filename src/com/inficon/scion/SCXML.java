package com.inficon.scion;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import com.inficon.scion.SCION;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

public class SCXML {
    private static final SCION scion = new SCION();
    private Scriptable interpreter;

    /**
    * Accepts a path to an SCXML file and returns a Scriptable model object, which can be passed 
    * to the SCXML constructor.
    */
    public static Scriptable pathToModel(String path){
        return fileToModel(new File(path));
    }

    /**
    * Accepts an SCXML file and returns a Scriptable model object, which can be passed 
    * to the SCXML constructor.
    */
    public static Scriptable fileToModel(File file){
        //parse the file
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(file);
            return documentToModel(dom);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

        return null;
    }

    /**
    * Accepts an org.w3c.dom.Document object and returns a Scriptable model object, which can be passed 
    * to the SCXML constructor.
    */
    public static Scriptable documentToModel(Document doc){
        return (Scriptable) SCXML.scion.xmlDocToJsonML(doc);
    }

    /**
    * Accepts an String containing an unparsed XML document and returns a Scriptable model object,
    * which can be passed to the SCXML constructor.
    */
    public static Scriptable documentStringToModel(String docString){
		try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new java.io.StringReader(docString));
            Document doc = db.parse(is);

            return (Scriptable) SCXML.scion.xmlDocToJsonML(doc);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
        return null;
    }

    //constructor
    public SCXML(Scriptable model){
        this.interpreter = (Scriptable) scion.createScionInterpreter(model);
    }   

    public SCXML(String path){
        this.interpreter = (Scriptable) scion.createScionInterpreter(pathToModel(path));
    }   

    public SCXML(File file){
        this.interpreter = (Scriptable) scion.createScionInterpreter(fileToModel(file));
    }   

    public SCXML(Document doc){
        this.interpreter = (Scriptable) scion.createScionInterpreter(documentToModel(doc));
    }   

    //starts the interpreter, returns a string list of state names
    //TODO: should make him a Set<String>
    /**
    * Starts the interpreter; should  be called before the first time gen is called, and 
    * should only be called once in an SCXML object's lifespan.
    */
    public Set<String> start(){
        return new HashSet<String>((List<String>) scion.startInterpreter(this.interpreter));
    }

    /**
    * Sends an event to the interpreter, which will prompt the interpreter
    * to take a macrostep as described in <a href="https://github.com/jbeard4/SCION/wiki/Scion-Semantics">SCION Semantics</a>.
    */
    public Set<String> gen(String eventName, Object eventData){
        return new HashSet<String>((List<String>) scion.genEvent(this.interpreter,eventName,eventData));
    }
}
