package com.musica;

import javazoom.jl.decoder.JavaLayerException; // Erro: Import não utilizado
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImprovedMusicPlayer {
    private ArrayList<File> playlist = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean playingStatus = false; // Erro: "playingStatus" é uma variável de controle, não precisa ser pública
    private Player player;
    private boolean isPaused = false;

    // Método para adicionar uma música à playlist
    public void addToPlaylist(File file) {
        playlist.add(file);
    }

    // Método para iniciar a reprodução
    public void play() {
        if (!playlist.isEmpty()) { // Verifica se a playlist não está vazia
            playingStatus = true; // Altera o status de reprodução para true
            File currentTrack = playlist.get(currentTrackIndex); // Obtém a música atual

            try {
                if (currentTrack.getName().toLowerCase().endsWith(".mp3")) { // Verifica se a música é um arquivo mp3
                    FileInputStream fileInputStream = new FileInputStream(currentTrack);
                    player = new Player(fileInputStream);

                    new Thread(() -> { // Nova thread para reprodução de mp3
                        try {
                            player.play();
                        } catch (JavaLayerException e) {
                            e.printStackTrace(); // Erro ao reproduzir mp3
                        }
                    }).start();
                } else { // Se não for mp3
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(currentTrack);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();

                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            if (playingStatus) {
                                playNext(); // Toca a próxima música
                            }
                        }
                    });
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | JavaLayerException e) {
                e.printStackTrace(); // Erro ao lidar com a exceção
            }
        } else {
            System.out.println("Playlist vazia. Adicione músicas para reproduzir.");
        }
    }

    // Método para pausar a reprodução
    public void pause() {
        isPaused = !isPaused; // Inverte o estado de pausa

        if (isPaused) {
            if (player != null) {
                player.close(); // Fecha o player
            }
        } else {
            // Retoma a reprodução a partir do ponto onde foi pausada
            play();
        }
    }

    // Método para parar a reprodução
    public void stop() {
        playingStatus = false; // Altera o status de reprodução para false
        if (player != null) {
            player.close(); // Fecha o player

        }
    }

    // Método para tocar a próxima música na playlist
    public void playNext() {
        if (currentTrackIndex < playlist.size() - 1) { // Verifica se não é a última música na playlist
            currentTrackIndex++; // Atualiza o índice da música atual

            // Se a música estiver pausada, não inicie a próxima música imediatamente
            if (!isPaused) {
                play(); // Chama o método de reprodução novamente para a nova música
            }
        } else {
            System.out.println("Fim da playlist.");
            stop(); // Se for a última música, para a reprodução
        }
    }

    // Método principal para iniciar o programa
    public static void main(String[] args) {
        ImprovedMusicPlayer player = new ImprovedMusicPlayer();
        JFrame frame = new JFrame("Music Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton stopButton = new JButton("Stop");
        JButton nextButton = new JButton("Próxima música");
        JButton addButton = new JButton("Adicionar música");

        // Define a ação a ser realizada quando o botão "Play" é pressionado
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.play();
            }
        });

        // Define a ação a ser realizada quando o botão "Pause" é pressionado
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.pause();
            }
        });

        // Define a ação a ser realizada quando o botão "Stop" é pressionado
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.stop();
            }
        });

        // Define a ação a ser realizada quando o botão "Próxima música" é pressionado
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.playNext();
            }
        });

        // Define a ação a ser realizada quando o botão "Adicionar música" é pressionado
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    player.addToPlaylist(selectedFile);
                    System.out.println("Música adicionada à playlist: " + selectedFile.getName());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(stopButton);
        panel.add(nextButton);
        panel.add(addButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}