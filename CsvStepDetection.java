package StepDetection;

import java.io.BufferedReader;
import java.io.FileReader;

public class CsvStepDetection {
  private static String file_input;
  private static int file_type;
  private static StepDetection sd;

  private static final int JUST_AXIAL = 0;
  private static final int FULL_PACKET = 1;

  private static final double TIME_INCREMENT = 10;

  public static void main(String args[]) {
    //System.out.println(args[0]);
    file_input = args[0];
    file_type = Integer.parseInt(args[1]);
    sd = new StepDetection();
    try {
      run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void run() throws Exception {
    String line;
    double axial;
    boolean isStepDetected;
    double time = 0;
    BufferedReader br;

    try {
      br = new BufferedReader(new FileReader(file_input));
      br.readLine();   // skip first datapoint or column headers
    } catch (Exception e) {
      System.out.println("Error opening file!");
      return;
    }

    try {
      while ((line = br.readLine()) != null) {
        if (file_type == JUST_AXIAL) {
          axial = Double.parseDouble(line);
        } else if (file_type == FULL_PACKET) {
          String[] packet = line.split(",");
          axial = Double.parseDouble(packet[2]);
        } else {
          System.out.println("0: Just Axial\n1: Full Packet");
          break;
        }

        time = time + TIME_INCREMENT;
        isStepDetected = sd.detectStep(axial, time);

        if (isStepDetected) {
          System.out.println("STEP DETECTED!");
        }
      }

    } catch (Exception e) {
      System.out.println("Error reading file!");
      return;
      }


    System.out.println("Finished.");
    return;

  }











}
