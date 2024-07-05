package org.example;

import game.Game;
import javax.swing.*;
import java.awt.*;

import game.Game;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;



public class Agent {
    private Game gamePanel;
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private MultiLayerNetwork model;

    public Agent(Game gamePanel) {
        this.gamePanel = gamePanel;

        initializeNeuralNetwork();
        new Thread(this::runRLAgent).start();
    }
    private void initializeNeuralNetwork() {
        int numInputs = 11;
        int numOutputs = 4;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(24).activation(Activation.RELU).build())
                .layer(1, new DenseLayer.Builder().nIn(24).nOut(24).activation(Activation.RELU).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(24).nOut(numOutputs).build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
    }
    public void runRLAgent() {
        for (int episode = 0; episode < 1000; episode++) {
            gamePanel.reset();
            boolean gameOver = false;
            int steps = 0;
            while (!gameOver && steps < 1000) {
                double[] state = gamePanel.getState();
                int action = chooseAction(state);
                double reward = gamePanel.performAction(action);
                gameOver = gamePanel.isGameOver();

                double[] newState = gamePanel.getState();
                updateQValues(state, action, reward, newState, gameOver);

                steps++;

                gamePanel.repaint();
                try {
                    Thread.sleep(1); // Control the game speed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Debug output
                if (steps % 10 == 0) {
                    LOG.info("Episode: " + episode + ", Step: " + steps + ", Score: " + gamePanel.getScore());
                }
            }
            LOG.info("Episode " + episode + " completed. Score: " + gamePanel.getScore());
        }

    }

    private int chooseAction(double[] state) {
        if (Math.random() < 0.1) {
            return (int) (Math.random() * 4);
        } else {
            INDArray input = Nd4j.create(new double[][]{state});  // Create a 2D array
            INDArray output = model.output(input);
            return output.argMax(1).getInt(0);
        }
    }
    private void updateQValues(double[] state, int action, double reward, double[] newState, boolean gameOver) {
        double learningRate = 0.1;
        double discountFactor = 0.99;

        INDArray inputState = Nd4j.create(new double[][]{state});  // Create a 2D array
        INDArray qValues = model.output(inputState);

        INDArray inputNewState = Nd4j.create(new double[][]{newState});  // Create a 2D array
        INDArray nextQValues = model.output(inputNewState);

        double maxNextQ = gameOver ? 0 : nextQValues.maxNumber().doubleValue();
        double targetQ = reward + discountFactor * maxNextQ;

        double[] updatedQValues = qValues.toDoubleVector();
        updatedQValues[action] = updatedQValues[action] + learningRate * (targetQ - updatedQValues[action]);

        INDArray target = Nd4j.create(new double[][]{updatedQValues});  // Create a 2D array
        model.fit(inputState, target);
    }
}
