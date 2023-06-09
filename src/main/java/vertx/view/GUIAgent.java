package vertx.view;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import utils.*;
import vertx.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class GUIAgent extends AbstractVerticle {

    private final Controller controller;
    private final JFrame frame = new JFrame();
    private final JList<AnalyzedFile> rankingList = new JList<>();
    private final JList<String> distributionList = new JList<>();
    private final JButton btnStart = new JButton("Start");
    private final JButton btnStop = new JButton("Stop");
    private Result result;
    private String rootAgentID;

    public void start(){
        EventBus eb = vertx.eventBus();

        eb.consumer("root-agent-id", message -> {
            this.rootAgentID = message.body().toString();
        });

        eb.consumer("mid-report", message -> {
            DefaultListModel<AnalyzedFile> rankingModel = new DefaultListModel<>();
            rankingModel.addAll(result.getRanking());

            SwingUtilities.invokeLater(() -> rankingList.setModel(rankingModel));

            DefaultListModel<String> distributionModel = new DefaultListModel<>();
            distributionModel.addAll(result.getDistribution().entrySet().stream()
                    .map(e -> e.getKey() + " : " + e.getValue()).
                    collect(Collectors.toList()));

            SwingUtilities.invokeLater(() -> distributionList.setModel(distributionModel));
        });

        eb.consumer("computation-ended", message -> {
            this.btnStart.setEnabled(true);
            this.btnStop.setEnabled(false);
        });
    }

    public GUIAgent(Controller controller){
        this.controller = controller;

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.frame.setTitle("Source Tracker");
        this.frame.setSize(800, 500);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);

        final JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        final JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        final JLabel lblDirectory = new JLabel("Start directory: ");
        final JTextField txtDirectory = new JTextField(20);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblNFiles = new JLabel("Number of files: ");
        final JTextField txtNFiles = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblIntervals = new JLabel("Number of intervals: ");
        final JTextField txtIntervals = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        final JLabel lblLastInterval = new JLabel("Last interval: ");
        final JTextField txtLastInterval = new JTextField(3);
        txtDirectory.setMinimumSize(txtDirectory.getPreferredSize());

        btnStop.setEnabled(false);

        btnStart.addActionListener(e -> {
            if(txtDirectory.getText().isEmpty()){
                Toast.makeToast(frame, "Insert path of initial directory.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtNFiles.getText().isEmpty() || !Strings.isNumeric(txtNFiles.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct number of files.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtIntervals.getText().isEmpty() || !Strings.isNumeric(txtIntervals.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct number of intervals.", new Color(255,0,0,170), 3);
                return;
            }
            if(txtLastInterval.getText().isEmpty() || !Strings.isNumeric(txtLastInterval.getText()) || Integer.parseInt(txtNFiles.getText()) <= 0){
                Toast.makeToast(frame, "Insert correct last interval value.", new Color(255,0,0,170), 3);
                return;
            }

            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            this.rankingList.setModel(new DefaultListModel<>());
            this.distributionList.setModel(new DefaultListModel<>());

            this.controller.processEvent(() -> {
                this.result = this.controller.analyzeSources(new SetupInfo(
                        txtDirectory.getText(),
                        Integer.parseInt(txtNFiles.getText()),
                        Integer.parseInt(txtIntervals.getText()),
                        Integer.parseInt(txtLastInterval.getText())), vertx);
            });
        });

        btnStop.addActionListener(e -> {
            vertx.eventBus().publish("stop", "");
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        });

        final JPanel resultsPanel = new JPanel();

        this.rankingList.setSize(100, 50);
        this.distributionList.setSize(100, 50);

        inputPanel.add(lblDirectory);
        inputPanel.add(txtDirectory);
        inputPanel.add(lblNFiles);
        inputPanel.add(txtNFiles);
        inputPanel.add(lblIntervals);
        inputPanel.add(txtIntervals);
        inputPanel.add(lblLastInterval);
        inputPanel.add(txtLastInterval);

        controlPanel.add(btnStart);
        controlPanel.add(btnStop);

        resultsPanel.add(new JScrollPane(rankingList));
        resultsPanel.add(new JScrollPane(distributionList));

        mainPanel.add(inputPanel);
        mainPanel.add(controlPanel);
        mainPanel.add(resultsPanel);

        this.frame.setContentPane(mainPanel);
        this.frame.setVisible(true);
    }


}
