package tagging;

public class TaggingToolServer {

  public static void main(String[] args) {
    //Class that can be used to start the HTTP controller.
    System.out.println("TaggingToolServer is starting...");
    TaggingTool tool = new TaggingTool();

    System.out.println("Starting HTTP controller...");
    HTTPController controllerHttp = new HTTPController(tool);
    controllerHttp.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      tool.terminate();
    }));

    System.out.println("Successfully started.");

  }
}
