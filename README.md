中島研プロジェクト研究


=======

ディレクトリ説明

/code 

/kinecdrone2
PC１台でdrone制御の（前記の内容）

/net_test
processingのソケット通信テスト

/OculusSDK
Oculus制御するSDK（11/28現在未使用）

/Processing
/Processing/Examples/Oculus riftにオキュラス用の動画生成するサンプル。

/vr.js
Oculusのセンサー値を読み取るためのプラグイン

/three.js
/three.js/example/js/effects/OculusRiftEffect.js　が動画をオキュラス用に変換するサンプル

/processing.js(1.4.1)
processingをjsで動かす


=======

チームアップル

## メンバー

- 池内
- 大塚
- 飯島
- 山部

## 制作物
- キネクドロン(仮)
KinecDrone //体感型飛行  


ヘッドマウントディスプレイとキネクトとAR.Droneを用いて空を飛んでるようなアプリを作る。  
キネクトで手と足を認識して、手を羽ばたかせるとAR.Droneが上昇したり、体全体で操作する。  
AR.Droneのカメラ映像をリアルタイムにHMDに表示させる。

### 予定
- AR.DroneをPCで操作できる簡単なプログラムの作成。
- キネクトを用いて寝そべった状態の人の手と足を認識し何個かのジェスチャー操作が選択されるようにする。
- https://docs.google.com/spreadsheet/ccc?key=0AvoRXDOzrJFTdGl5LVNaWmVQUG1qTzdQYWlOeDU3T1E&usp=sharing#gid=1

### アイディア

- クリックタブルにする。今までの操作をより短縮し、使いやすくする。
- 拡張。自分の感覚を拡張する。  
- プロジェクター,HMD,ARdron,kinect.

### 制作方針
ProcessingdeでKinectもAR.Droneも制御する

### 10/12改善点
- HMDを用いて首の回転とAR.Droneの回転の同期
- 扇風機などをもちいてAR.Droneの感覚の共有
- 羽ばたきによる上昇の実装
- センサーの追加をするか考察
