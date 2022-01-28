package taggingtest;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.*;
import tagging.TaggingMill;
import tagging.TaggingTool;
import tagging._ast.ASTTagging;
import tagging._cocos.TaggingCoCoChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class AbstractTest {
        protected static final String EXAMPLES_PATH = "src/test/resources/";
        protected final static String VALID_PATH = EXAMPLES_PATH + "valid/";
        protected final static String INVALID_PATH = EXAMPLES_PATH + "invalid/";
        protected TaggingTool tgTool;
        protected TaggingCoCoChecker checker;

        public AbstractTest() {
            Log.enableFailQuick(false);
        }

        @BeforeAll
        public static void initMill(){
        TaggingMill.init();
        }

    @BeforeEach
        public void setup() {
            this.tgTool = new TaggingTool();

            this.checker = new TaggingCoCoChecker();

            initCoCoChecker();

            Log.getFindings().clear();
        }

        protected abstract void initCoCoChecker();
        protected abstract List<String> getErrorCodeOfCocoUnderTest();

        public void testCorrectExamples(String modelName) {
            ASTTagging tagging = tgTool.loadModel(VALID_PATH + modelName);
            checker.checkAll(tagging);
            String msgs = Log.getFindings().stream().map(Finding::getMsg).collect(Collectors.joining(System.lineSeparator()));
            Assertions.assertEquals(0, Log.getErrorCount(), msgs);
            Assertions.assertEquals(0,
                    Log.getFindings()
                            .stream()
                            .map(Finding::buildMsg)
                            .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
                            .count(),
                    msgs);
        }

        protected void testCocoViolation(String modelDirectory, int errorCount, int logFindingsCount) {
            ASTTagging tagging = tgTool.loadModel(INVALID_PATH + modelDirectory);
            checker.checkAll(tagging);
            Assertions.assertEquals(errorCount, Log.getErrorCount());
            Assertions.assertEquals(logFindingsCount,
                    Log.getFindings()
                            .stream()
                            .map(Finding::buildMsg)
                            .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
                            .count());
        }

        public void testValidToolCombination (String modelFile, String configurationFile){
            //List<ASTMCQualifiedName> activatedComponents = tgTool.findActivatedComponents(VALID_PATH + modelFile, VALID_PATH + configurationFile);
            tgTool.updateIotManager(VALID_PATH + modelFile, VALID_PATH + configurationFile);
            Assertions.assertTrue(true);
        }

        public void testInvalidToolCombination (String modelFile){
            boolean result;
            result = tgTool.isFeatureDeployable(INVALID_PATH + modelFile);
            Assertions.assertFalse(result);
        }
    }
