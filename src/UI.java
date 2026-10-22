import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// Main class
public class UI {

    // Main driver method
    public boolean createFrame() {
        // Creating instance of JFrame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create all items in JFrame
        JButton button = new JButton("Press to download video");
        HintTextField url = new HintTextField("Insert URL");
        String choices[] = { "Video", "mp3", "Playlist" };
        JComboBox<String> select = new JComboBox<>(choices);
        JProgressBar progress = new JProgressBar(0, 100);
        JLabel exitcode = new JLabel("");

        // x axis, y axis, width, height
        button.setBounds(150, 200, 220, 50);
        url.setBounds(150, 160, 220, 30);
        select.setBounds(150, 260, 220, 30);
        progress.setBounds(150, 300, 220, 20);
        exitcode.setBounds(150, 300, 220, 20);

        // adding components in JFrame
        frame.add(button);
        frame.add(url);
        frame.add(select);
        frame.add(progress);
        frame.add(exitcode);

        // 400 width and 500 height
        frame.setSize(500, 600);

        // using no layout managers
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        // making the frame visible
        frame.setVisible(true);
        // Make the progress bar invisible until needed
        progress.setVisible(false);
        progress.setIndeterminate(false);
        progress.setStringPainted(false);
        exitcode.setVisible(false);

        // Listens for the download button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitcode.setVisible(false);
                progress.setVisible(true);
                button.setEnabled(false);
                // System.out.println("Button pressed");
                String item = String.valueOf(select.getSelectedItem());
                Pattern download = Pattern.compile("\\[download\\]");
                try {
                    // For downloading a video
                    if (item.equals("Video")) {
                        // System.out.println("Video");
                        // Sets as background task
                        new SwingWorker<Integer, String>() {
                            @Override
                            // Background action
                            protected Integer doInBackground() throws Exception {
                                ProcessBuilder downVid = new ProcessBuilder("yt-dlp", url.getText());
                                downVid.redirectErrorStream(true);
                                Process process = downVid.start();
                                // Line reader
                                try (var reader = new java.io.BufferedReader(
                                        new java.io.InputStreamReader(process.getInputStream()))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        publish(line);
                                    }
                                }
                                // Exit background
                                return process.waitFor();
                            }

                            // Print during process
                            @Override
                            protected void process(java.util.List<String> lines) {
                                for (String line : lines) {
                                    Matcher matcher = download.matcher(line);
                                    boolean match = matcher.find();
                                    if (match) {
                                        // System.out.println(line);
                                        String[] parts = line.split(" ");
                                        // System.out.println(parts[2]);
                                        if (!parts[2].equals("of") && !parts[1].equals("Destination:")) {
                                            String[] num = parts[2].split("\\.");
                                            // System.out.println(num[0]);
                                            if (isNumeric(num[0])) {
                                                int percentage = Integer.parseInt(num[0]);
                                                progress.setValue(percentage);
                                            }
                                        }
                                    }

                                }
                            }

                            // Print exit code
                            @Override
                            protected void done() {
                                button.setEnabled(true);
                                progress.setValue(0);
                                progress.setVisible(false);
                                try {
                                    int code = get();
                                    if (code != 0) {
                                        exitcode.setText("Download failed\nExit code: " + code);
                                    } else {
                                        exitcode.setText("Download succesful");
                                    }
                                    exitcode.setVisible(true);
                                    // System.out.println("Exit code: " + get());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }.execute();

                    } else if (item.equals("mp3")) {
                        // System.out.println("mp3");
                        new SwingWorker<Integer, String>() {
                            @Override
                            // Background action
                            protected Integer doInBackground() throws Exception {
                                ProcessBuilder downMp3 = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3",
                                        url.getText());
                                downMp3.redirectErrorStream(true);
                                Process process = downMp3.start();
                                // Line reader
                                try (var reader = new java.io.BufferedReader(
                                        new java.io.InputStreamReader(process.getInputStream()))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        publish(line);
                                    }
                                }
                                // Exit background
                                return process.waitFor();
                            }

                            // Print during process
                            @Override
                            protected void process(java.util.List<String> lines) {
                                for (String line : lines) {
                                    Matcher matcher = download.matcher(line);
                                    boolean match = matcher.find();
                                    if (match) {
                                        // System.out.println(line);
                                        String[] parts = line.split(" ");
                                        // System.out.println(parts[2]);
                                        if (!parts[2].equals("of") && !parts[1].equals("Destination:")) {
                                            String[] num = parts[2].split("\\.");
                                            // System.out.println(num[0]);
                                            if (isNumeric(num[0])) {
                                                int percentage = Integer.parseInt(num[0]);
                                                progress.setValue(percentage);
                                            }
                                        }
                                    }

                                }
                            }

                            // Print exit code
                            @Override
                            protected void done() {
                                button.setEnabled(true);
                                progress.setValue(0);
                                progress.setVisible(false);
                                try {
                                    int code = get();
                                    if (code != 0) {
                                        exitcode.setText("Download failed\nExit code: " + code);
                                    } else {
                                        exitcode.setText("Download succesful");
                                    }
                                    exitcode.setVisible(true);
                                    // System.out.println("Exit code: " + get());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }.execute();

                    } else if (item.equals("Playlist"))

                    {
                        // System.out.println("Playlist");

                        new SwingWorker<Integer, String>() {
                            @Override
                            // Background action
                            protected Integer doInBackground() throws Exception {
                                ProcessBuilder playlist = new ProcessBuilder("yt-dlp", "-yes-playlist", url.getText());
                                playlist.redirectErrorStream(true);
                                Process process = playlist.start();
                                // Line reader
                                try (var reader = new java.io.BufferedReader(
                                        new java.io.InputStreamReader(process.getInputStream()))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        publish(line);
                                    }
                                }
                                // Exit background
                                return process.waitFor();
                            }

                            // Print during process
                            @Override
                            protected void process(java.util.List<String> lines) {
                                for (String line : lines) {
                                    Matcher matcher = download.matcher(line);
                                    boolean match = matcher.find();
                                    if (match) {
                                        // System.out.println(line);
                                        String[] parts = line.split(" ");
                                        // System.out.println(parts[2]);
                                        if (!parts[2].equals("of") && !parts[1].equals("Destination:")) {
                                            String[] num = parts[2].split("\\.");
                                            // System.out.println(num[0]);
                                            if (isNumeric(num[0])) {
                                                int percentage = Integer.parseInt(num[0]);
                                                progress.setValue(percentage);
                                            }
                                        }
                                    }

                                }
                            }

                            // Print exit code
                            @Override
                            protected void done() {
                                button.setEnabled(true);
                                progress.setValue(0);
                                progress.setVisible(false);
                                try {
                                    int code = get();
                                    if (code != 0) {
                                        exitcode.setText("Download failed\nExit code: " + code);
                                    } else {
                                        exitcode.setText("Download succesful");
                                    }
                                    exitcode.setVisible(true);
                                    // System.out.println("Exit code: " + get());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }.execute();
                    }
                } catch (Exception a) {
                    System.out.println(a);
                }

            }
        });

        return true;
    }

    public static boolean isNumeric(String str) {
        if (str != null && !str.equals("") && !str.equals(" ")) {
            return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
        }
        return false;
    }
}