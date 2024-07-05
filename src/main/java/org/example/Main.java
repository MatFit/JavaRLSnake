package org.example;

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

public class Main extends JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private Game gamePanel;
    private MultiLayerNetwork model;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new Game();
        add(gamePanel);
        pack();
        setVisible(true);
        setTitle("Snake - Reinforcement Learning Attempt");
        // Agent creation and start on instanation
        Agent agent = new Agent(gamePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }
}