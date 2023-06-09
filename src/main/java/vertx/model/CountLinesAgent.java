package vertx.model;

import utils.Document;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import utils.AnalyzedFile;
import vertx.controller.Controller;

public class CountLinesAgent extends AbstractVerticle {

    private final Document document;
    private final Controller controller;
    public CountLinesAgent(Controller controller, Document document) {
        this.document = document;
        this.controller = controller;
    }

    @Override
    public void start(Promise<Void> startPromise){
        vertx.eventBus().consumer("stop", message -> {
            vertx.undeploy(context.deploymentID());
        });

        this.controller.addAnalyzedFile(new AnalyzedFile(document.getPath(), document.countLines()));
        EventBus eb = vertx.eventBus();
        eb.publish("mid-report", "");
        startPromise.complete();
    }

    private void log(String msg) {
        System.out.println("[COUNT LINES AGENT][" + Thread.currentThread() + "] " + msg);
    }
}