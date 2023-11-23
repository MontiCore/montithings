// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.trafos.patterns;

public class GrafanaPanel {
  private final int x;
  private final int y;
  private final String title;
  private final String sqlTable;

  public GrafanaPanel(int x, int y, String title, String sqlTable) {
    this.x = x;
    this.y = y;
    this.title = title;
    this.sqlTable = sqlTable;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String getTitle() {
    return title;
  }

  public String getSqlTable() {
    return sqlTable;
  }
}
