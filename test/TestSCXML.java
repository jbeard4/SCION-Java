import com.inficon.scion.SCXML;
import com.inficon.scion.SCXMLListener;
import org.mozilla.javascript.Scriptable;
import java.util.Set;
import java.util.List;

public class TestSCXML {
    public static void main(String[] args){

        SCXML scxml = new SCXML("./js-lib/SCION/test/scxml-test-framework/test/basic/basic1.scxml");

        scxml.registerListener( 
            new SCXMLListener(){
                public void onEntry(String stateId){
                    System.out.println("Entering " + stateId);
                }

                public void onExit(String stateId){
                    System.out.println("Exiting " + stateId);
                }

                public void onTransition(String sourceStateId, List<String> targetStateIds){
                    System.out.println("Transitioning from " + sourceStateId + " to ");
                    for(String targetId : targetStateIds){
                        System.out.println("target state id: " + targetId); 
                    }
                }
            }
        );

        System.out.println(scxml);
        Set<String> initialConfiguration = scxml.start();

        System.out.println("initialConfiguration");
        System.out.println(initialConfiguration);

        Set<String> nextConfiguration = scxml.gen("t",null);
        System.out.println("nextConfiguration");
        System.out.println(nextConfiguration);
    }
}
