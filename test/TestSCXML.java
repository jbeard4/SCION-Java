import com.inficon.scion.SCXML;
import org.mozilla.javascript.Scriptable;
import java.util.Set;

public class TestSCXML {
    public static void main(String[] args){
        //Scriptable model = SCXML.convertPathToSCXMLDocToModel("/home/jbeard/workspace/scion/scxml-test-framework/test/basic/basic1.scxml");
        //System.out.println(model);
        SCXML scxml = new SCXML("/home/jbeard/workspace/scion/scxml-test-framework/test/basic/basic1.scxml");
        System.out.println(scxml);
        Set<String> initialConfiguration = scxml.start();
        System.out.println(initialConfiguration);

        for(String stateId : initialConfiguration){
            System.out.println(stateId);
        }

        Set<String> nextConfiguration = scxml.gen("t",null);
        System.out.println(nextConfiguration);
        for(String stateId : nextConfiguration){
            System.out.println(stateId);
        }
    }
}
