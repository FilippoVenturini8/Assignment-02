package vertx;

import io.vertx.core.Vertx;
import utils.SetupInfo;
import utils.Strings;
import vertx.controller.Controller;
import vertx.model.Model;
import vertx.view.ConsoleAgent;

import java.util.Scanner;

public class VertxCLIMain {
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        Model model = new Model();
        Controller controller = new Controller(model);

        SetupInfo setupInfo = getSetupInfo();

        vertx.deployVerticle(new ConsoleAgent(controller, setupInfo));
    }

    private static SetupInfo getSetupInfo(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Root directory: ");
        final String dir = scanner.nextLine();

        String tmp;
        do{
            System.out.print("Number of files to visualize: ");
            tmp = scanner.nextLine();
        }while (!Strings.isNumeric(tmp) || Integer.parseInt(tmp) <= 0);
        final Integer nFiles = Integer.parseInt(tmp);

        do{
            System.out.print("Number of intervals: ");
            tmp = scanner.nextLine();
        }while (!Strings.isNumeric(tmp) || Integer.parseInt(tmp) <= 0);
        final Integer nIntervals = Integer.parseInt(tmp);

        do{
            System.out.print("Last interval max: ");
            tmp = scanner.nextLine();
        }while (!Strings.isNumeric(tmp) || Integer.parseInt(tmp) <= 0);
        final Integer lastInterval = Integer.parseInt(tmp);

        return new SetupInfo(dir, nFiles, nIntervals, lastInterval);
    }
}
