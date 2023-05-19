package media_player;

import java.io.*;
import java.util.Map;
import javazoom.jlgui.basicplayer.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;

public class SimplePlayer  extends JFrame implements ActionListener, BasicPlayerListener {
	// オブジェクト生成
    JFrame frame = new JFrame();
    JFileChooser chooser = new JFileChooser();
    BasicPlayer player = new BasicPlayer();
    BasicController control = (BasicController) player;
    File playFile;
    JButton btnOpen, btnPlay;
    JLabel label;

    // コンストラクタ
    SimplePlayer() {
        frame.setTitle("MP3 Player"); //  タイトル
        frame.setLayout(new BorderLayout());
        // display("" + player.getStatus());

        // ボタンを生成
        btnOpen = new JButton("読み込み"); // 開くボタン
        btnOpen.setActionCommand("open");
        btnOpen.addActionListener(this);

        btnPlay = new JButton("再生"); // 再生ボタン
        btnPlay.setActionCommand("play/pause");
        btnPlay.addActionListener(this);

        label = new JLabel("Set Music");
        label.setHorizontalAlignment(JLabel.CENTER); // 中央に表示

        // モジュール設置
        frame.add(btnOpen, BorderLayout.NORTH); // 開くボタンをセットする
        frame.add(btnPlay, BorderLayout.SOUTH); // 開くボタンをセットする
        frame.add(label, BorderLayout.CENTER); // 文字列表示

        // frame.pack(); // レイアウトの自動最適化
        frame.setSize(800, 800); // ウインドウサイズの設定
        frame.setVisible(true); // ウインドウの可視性を設定

        // プレイヤーの設定を反映させる
        player.addBasicPlayerListener(this);
        
        label.setTransferHandler(new DropFileHandler());

        // ウインドウを閉じるときの処理
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0); // 終了
            }
        });

    }

    // 再生
    public void play() {
        try {
            // ファイルを再生、音量、パンなどを設定
            control.play();
            // display(control.toString());
            control.setGain(0.50);
            control.setPan(0.0);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    // 一時停止
    public void pause() {
        try {
            control.pause();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    // 再開
    public void resume() {
        try {
            control.resume(); // 一時停止したところから再生
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    // BasicPlayerをcontrollerを通じて制御する
    public void setController(BasicController controller) {
        display("Controll: " + controller.toString());
    }

    // 音量、パン、プレイヤーの再生などの現在の状況を取得
    public void stateUpdated(BasicPlayerEvent event) {
        display("stateUpdated: " + event.toString());
    }

    // サウンドの進捗状況を取得
    public void progress(int read, long time, byte[] data, Map map) {
        // display("progress :" + map.toString());
        // display("" + player.getStatus());
    }

    // 開かれたサウンドファイルの詳細を取得
    public void opened(Object object, Map map) {
        display("opened :" + map.toString());
    }

    // コンソールに文字列を出力
    public void display(String str) {
        if (str != null)
            System.out.println(str);
    }
    
    private class DropFileHandler extends TransferHandler {

		/**
		 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
		 */
		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				// ドロップ操作でない場合は受け取らない
		        return false;
		    }

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// ドロップされたのがファイルでない場合は受け取らない
		        return false;
		    }

			return true;
		}

		/**
		 * ドロップされたファイルを受け取る
		 */
		@Override
		public boolean importData(TransferSupport support) {
			// 受け取っていいものか確認する
			if (!canImport(support)) {
		        return false;
		    }

			// ドロップ処理
			Transferable t = support.getTransferable();
			try {
				// ファイルを受け取る
				List<File> files = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
				File file = files.get(0);

				// テキストエリアに表示するファイル名リストを作成する	
				
				player.open(file);
				label.setText(file.getName());
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
    }

    // ボタンアクション
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand() == "open") { // open
            int choice = chooser.showOpenDialog(this);
      
            try {
            	
                if (choice == JFileChooser.APPROVE_OPTION) {
                	
                    playFile = chooser.getSelectedFile(); // 選択されたファイルのパスを取得
                    FileReader fr = new FileReader(playFile); // ファイルパスから読み込み
                    // frame.setTitle(playFile.getName()); // タイトルを選ばれたファイルの名前に変更
                    label.setText(playFile.getName()); // 選ばれたファイルの名前を表示
                    fr.close(); // ファイルの解放

                    // 指定されたサウンドファイルを開く
                    control.open(playFile);
                    
                }
            } catch (Exception e) {
                display("ファイルダイアログの展開に失敗しました。");
            }
        } else if (event.getActionCommand() == "play/pause") {
            switch (player.getStatus()) {
                case BasicPlayer.OPENED: // 再生
                    play();
                    display("再生");
                    btnPlay.setText("一時停止");
                    break;
                case BasicPlayer.PLAYING: // 一時停止
                    pause();
                    display("一時停止");
                    btnPlay.setText("再生");
                    break;
                case BasicPlayer.PAUSED: // 再開
                    resume();
                    display("再開");
                    btnPlay.setText("一時停止");
                    break;
                default:
                    break;
            }
        }
    }
}


