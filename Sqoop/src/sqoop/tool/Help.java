package org.apache.sqoop.tool;

import java.util.Set;

import org.apache.sqoop.SqoopOptions;
import org.apache.sqoop.cli.ToolOptions;

/**
 * Tool that explains the usage of Sqoop.
 */
public class HelpTool extends BaseSqoopTool {

  public HelpTool() {
    super("help");
  }

  /**
   * @param str the string to right-side pad
   * @param num the minimum number of characters to return
   * @return 'str' with enough right padding to make it num characters long.
   */
  private static String padRight(String str, int num) {
    StringBuilder sb = new StringBuilder();
    sb.append(str);
    for (int count = str.length(); count < num; count++) {
      sb.append(" ");
    }

    return sb.toString();
  }

  /**
   * Print out a list of all SqoopTool implementations and their
   * descriptions.
   */
  private void printAvailableTools() {
    System.out.println("usage: sqoop COMMAND [ARGS]");
    System.out.println("");
    System.out.println("Available commands:");

    Set<String> toolNames = getToolNames();

    int maxWidth = 0;
    for (String tool : toolNames) {
      maxWidth = Math.max(maxWidth, tool.length());
    }

    for (String tool : toolNames) {
      System.out.println("  " + padRight(tool, maxWidth+2)
          + getToolDescription(tool));
    }

    System.out.println("");
    System.out.println(
        "See 'sqoop help COMMAND' for information on a specific command.");
  }


  @Override
  /** {@inheritDoc} */
  public int run(SqoopOptions options) {

    if (this.extraArguments != null && this.extraArguments.length > 0) {
      if (hasUnrecognizedArgs(extraArguments, 1, extraArguments.length)) {
        return 1;
      }

      SqoopTool subTool = SqoopTool.getTool(extraArguments[0]);
      if (null == subTool) {
        System.out.println("No such tool: " + extraArguments[0]);
        System.out.println(
            "Try 'sqoop help' for a list of available commands.");
        return 1;
      } else {
        ToolOptions toolOpts = new ToolOptions();
        subTool.configureOptions(toolOpts);
        subTool.printHelp(toolOpts);
        return 0;
      }
    } else {
      printAvailableTools();
    }

    return 0;
  }

  @Override
  public void printHelp(ToolOptions opts) {
    System.out.println("usage: sqoop " + getToolName() + " [COMMAND]");
  }
}
