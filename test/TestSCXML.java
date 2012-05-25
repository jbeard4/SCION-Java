import com.inficon.scion.SCXML;
import org.mozilla.javascript.Scriptable;
import java.util.List;

public class TestSCXML {
    public static void main(String[] args){
        Scriptable model = SCXML.convertPathToSCXMLDocToModel("/home/jbeard/workspace/scion/scxml-test-framework/test/basic/basic1.scxml");
        System.out.println(model);
        SCXML scxml = new SCXML(model);
        System.out.println(scxml);
        List<String> initialConfiguration = scxml.start();
        System.out.println(initialConfiguration);

        for(String stateId : initialConfiguration){
            System.out.println(stateId);
        }

        List<String> nextConfiguration = scxml.gen("t",null);
        System.out.println(nextConfiguration);
        for(String stateId : nextConfiguration){
            System.out.println(stateId);
        }
    }
}
