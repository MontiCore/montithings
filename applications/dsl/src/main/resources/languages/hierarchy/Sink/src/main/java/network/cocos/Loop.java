// (c) https://github.com/MontiCore/monticore
package network.cocos;

import de.se_rwth.commons.logging.Log;
import network._cocos.NetworkASTNetCoCo;
import network._ast.ASTConnection;
import network._ast.ASTKnoten;
import network._ast.ASTNet; 
import org.assertj.core.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that all lines, i.e. strings, within a tab have the same length.
 *
 * @author (last commit) kirchhof
 * @version 1.0, 20.03.19
 * @since 1.0
 */
public class Loop implements NetworkASTNetCoCo {

  @VisibleForTesting
  public static final String NO_LOOP =
      "0xY0100 there i no loop in the network";

  @Override public void check(ASTNet node) {
    boolean looping = false;
    for(ASTKnoten k : node.getKnotenList()){
        List<String> reachable = new ArrayList<String>();
        List<ASTConnection> used = new ArrayList<ASTConnection>();
        reachable.add(k.getName());

        boolean changed = true;
        while(changed){
            changed = false;
            for(ASTConnection c : node.getConnectionList()){
            
                if(!used.contains(c)){
                    String k1 = c.getKnoten1Name();
                    String k2 = c.getKnoten2Name();
                    if (reachable.contains(k1)){
                        used.add(c);
                        if(reachable.contains(k2)){
                            looping = true;
                            break;
                        }else{
                            reachable.add(k2);
                            changed = true;
                        }
                    }else if (reachable.contains(k2)){
                        used.add(c);
                        if(reachable.contains(k1)){
                            looping = true;
                            break;
                        }else{
                            reachable.add(k1);
                            changed = true;
                        } 
                    }   
                }
            }
        }
    }
    if(!looping){
        Log.error(NO_LOOP);
    }

  }
}