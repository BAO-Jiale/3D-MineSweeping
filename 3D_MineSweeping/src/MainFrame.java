import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

import com.simsilica.lemur.*;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.text.DocumentModel;

import java.io.*;


public class MainFrame extends SimpleApplication {
    public static GameController gameController=new GameController(new Player(),new Player());
    private MineGenerator mineField;
    public Spatial[][]temp;
    public Status [][]status;

    public void initMineField(int row,int col,int num){
        mineField=new MineGenerator(row,col,num);
        temp=new Spatial[row][col];
        status=mineField.getMineField();
    }

    private TextField textField;
    private DocumentModel document;
    private TextField message;
    private BitmapText hudText;

    @Override
    public void simpleInitApp() {
        initLemur();
        //建背景
        Spatial sky = SkyFactory.createSky(assetManager,
                "Textures/Sky/Bright/BrightSky.dds", // 贴图路径
                SkyFactory.EnvMapType.CubeMap);// 贴图类型
        rootNode.attachChild(sky);

        //set camera
        cam.setLocation(new Vector3f(0,0,50.0f));
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(50.0f);
        flyCam.setZoomSpeed(15.0f);
        flyCam.setDragToRotate(true);

        initHUD();
        //Set lights
        ColorRGBA colorRGBA=new ColorRGBA();
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(colorRGBA);
        rootNode.addLight(ambientLight);
    }

    @Override
    public void simpleUpdate(float tpf) {
        /* Interact with game events in the main loop */
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        /* (optional) Make advanced modifications to frameBuffer and scene graph. */
    }

    public void initLemur(){
        // 初始化Lemur GUI, 加载 'glass' 样式, 将'glass'设置为GUI默认样式
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        // 创建一个Container作为窗口中其他GUI元素的容器
        Container operationWin = new Container();
        guiNode.attachChild(operationWin);
        operationWin.center();
        operationWin.setLocalTranslation(0, (float)(0.5*cam.getHeight()+0.5*operationWin.getSize().getY()),0);
        operationWin.addChild(new Label("Operations"));
        Button cheatBtn = operationWin.addChild(new Button("Cheat"));
        Button pauseBtn = operationWin.addChild(new Button("Pause"));
        Button restartBtn = operationWin.addChild(new Button("Restart"));
        Button newGameBtn = operationWin.addChild(new Button("New Game"));

        Container mainWindow = new Container();
        guiNode.attachChild(mainWindow);
        // 设置窗口在屏幕上的坐标
        // 注意：Lemur的GUI元素是以控件左上角为原点，向右、向下生成的。
        // 然而，作为一个Spatial，它在GuiNode中的坐标原点依然是屏幕的左下角。
        mainWindow.setLocalTranslation((float)(0.5*cam.getWidth()+0.5*mainWindow.getSize().getX()), (float)(0.5*cam.getHeight()+0.5*mainWindow.getSize().getY()), 0);
        mainWindow.scale(2);
        // 添加一个Label控件
        mainWindow.addChild(new Label("Menu"));

        AudioNode mainMenu = new AudioNode(assetManager, "main/resources/Audio/Default Main Menu.wav", AudioData.DataType.Stream);
        mainMenu.setLooping(true); // 循环播放
        mainMenu.setPositional(false);
        mainMenu.setVolume(0.5f);// 音量
        mainWindow.attachChild(mainMenu);
        mainMenu.play();

        // 添加一个Button控件
        Button patternBtn = mainWindow.addChild(new Button("Pattern"));
        Button introductionBtn = mainWindow.addChild(new Button("Intro"));

        Button loadBtn = new Button("Save & Load");
        operationWin.addChild(loadBtn);
        loadBtn.addClickCommands(source -> {
            guiNode.detachChild(mainWindow);

            Container loadWin=new Container();
            guiNode.attachChild(loadWin);
            loadWin.center();
            loadWin.setLocalTranslation(0, (float)cam.getHeight(), 0);
            loadWin.scale(2);
            loadWin.addChild(new Label("Save Or Load"));
            Button save =loadWin.addChild(new Button("Save"));
            Button load =loadWin.addChild(new Button("Load"));
            Button back =loadWin.addChild(new Button("Back To Menu"));

            back.addClickCommands(source1 -> {
                guiNode.detachChild(loadWin);
                guiNode.attachChild(mainWindow);
            });

            // Create a multiline text field with our document model
            textField = loadWin.addChild(new TextField("Input your file No. here"));
            textField.setSingleLine(true);
            document = textField.getDocumentModel();

            // Setup some preferred sizing since this will be the primary
            // element in our GUI
            textField.center();textField.setLocalTranslation((float)0.5*cam.getWidth(), (float)0.5*cam.getHeight(), 0);
            textField.setPreferredWidth((float)0.25*cam.getWidth());
            textField.setPreferredLineCount(10);

            save.addClickCommands(source1 -> {
                final String fileName = textField.getText();System.out.println("fileName :"+fileName);
                writeDataToFile("main\\load\\"+fileName);});
            load.addClickCommands(source1 -> {
                final String fileName = textField.getText();System.out.println("fileName :"+fileName);
                readFileData("main\\load\\"+fileName);mainMenu.stop();initAudio();initScene();gameController.setMine_Left(mineField.getNumber());initHUD();});
        });
        patternBtn.addClickCommands(source -> {
            guiNode.detachChild(mainWindow);

            Container modeWin=new Container();
            guiNode.attachChild(modeWin);
            modeWin.center();
            modeWin.setLocalTranslation(0, (float)cam.getHeight(), 0);
            modeWin.scale(2);
            modeWin.addChild(new Label("Choose Your Mode"));
            Button easy = modeWin.addChild(new Button("Easy"));
            Button medium = modeWin.addChild(new Button("Medium"));
            Button hard = modeWin.addChild(new Button("Hard"));
            Button custom = modeWin.addChild(new Button("Custom"));
            Button back =modeWin.addChild(new Button("Back To Menu"));

            back.addClickCommands(source1 -> {
                guiNode.detachChild(modeWin);
                guiNode.attachChild(mainWindow);
            });
            easy.addClickCommands(source1 -> {
                initMineField(9,9,10);
                guiNode.detachChild(modeWin);
                mainMenu.stop();initAudio();initScene();gameController.setMine_Left(mineField.getNumber());initHUD();
            });
            medium.addClickCommands(source1 -> {
                initMineField(16,16,40);
                guiNode.detachChild(modeWin);
                mainMenu.stop();initAudio();initScene();gameController.setMine_Left(mineField.getNumber());initHUD();
            });
            hard.addClickCommands(source1 -> {
                initMineField(16,30,99);
                guiNode.detachChild(modeWin);
                mainMenu.stop();initAudio();initScene();gameController.setMine_Left(mineField.getNumber());initHUD();
            });
            custom.addClickCommands(source1 -> {
                textField = modeWin.addChild(new TextField("Please input the row,col,minenumber"));
                textField.setSingleLine(true);
                document = textField.getDocumentModel();
                textField.setPreferredWidth(500);
                textField.setPreferredLineCount(10);

                Button ok =modeWin.addChild(new Button("Save"));
                ok.addClickCommands(source2->{
                    String []property = textField.getText().split(",");
                    int [] temp=new int[3];
                    for (int i=0;i<3;i++){temp[i]=Integer.parseInt(property[i]);}
                    initMineField(temp[0],temp[1],temp[2]);
                    guiNode.detachChild(modeWin);
                    mainMenu.stop();initAudio();initScene();gameController.setMine_Left(mineField.getNumber());initHUD();
                });
            });
        });
        introductionBtn.addClickCommands(source -> {
            guiNode.detachChild(mainWindow);

            Container introWin=new Container();
            guiNode.attachChild(introWin);
            introWin.center();
            introWin.setLocalTranslation(0, (float)cam.getHeight(), 0);
            introWin.scale(2);
            introWin.addChild(new Label("About Game"));
            Button rule = introWin.addChild(new Button("Rule"));
            Button we = introWin.addChild(new Button("About Us"));
            Button back =introWin.addChild(new Button("Back To Menu"));

            back.addClickCommands(source1 -> {
                guiNode.detachChild(introWin);
                guiNode.attachChild(mainWindow);
            });
            rule.addClickCommands(source1 -> {
                textField = introWin.addChild(
                        new TextField(
                                """
                                        a.If the score gap between the two sides is greater than the undisclosed number of mines in the game area, the dominant side is determined to win.
                                        b.If both players have the same score when all the mines are revealed, the player with the fewerrors (including the wrong touch and marking errors) wins.
                                        c.If the number of errors is still the same, both sides draw.
                                                 """
                        ));
                textField.setSingleLine(false);
                document = textField.getDocumentModel();
                textField.setPreferredWidth(500);
                textField.setPreferredLineCount(10);
            });
            we.addClickCommands(source1 -> {
                textField = introWin.addChild(new TextField("Developer:GAO Si & BAO Jiale"));
                textField.setSingleLine(true);
                document = textField.getDocumentModel();
                textField.setPreferredWidth(500);
                textField.setPreferredLineCount(10);
            });

        });
        cheatBtn.addClickCommands(source -> {onCheat();});
        pauseBtn.addClickCommands(source -> {onPause();});
        restartBtn.addClickCommands(source -> {onRestart();});
        newGameBtn.addClickCommands(source -> {onNewGame();});

        pauseWin=new Container();
        pauseWin.addChild(new Label("The Game Is Paused"));
        pauseWin.center();pauseWin.scale(2);
        pauseWin.setLocalTranslation((float)0.5*cam.getWidth(), (float)0.5*cam.getHeight(), 0);
    }


    private void initScene(){
        //load all the minefield
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                temp[i][j]=draw(i,j);
                rootNode.attachChild(temp[i][j]);
                addListenerToSpatial(i,j);
            }
        }
    }

    private void initAudio() {
        // 创建一个自然音效（背景），这个音源会一直循环播放。
        AudioNode audioNature = new AudioNode(assetManager, "main/resources/Audio/Background.wav", AudioData.DataType.Stream);
        audioNature.setLooping(true); // 循环播放
        audioNature.setPositional(false);
        audioNature.setVolume(0.5f);// 音量
        // 将音源添加到场景中
        rootNode.attachChild(audioNature);

        audioNature.play(); // 持续播放
    }

    //Some method to load assets
    public Spatial drawGrass(){
        Spatial grass = assetManager.loadModel("main/resources/Models/Grass/Grass.j3o");
        Material matGrass = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matGrass.setColor("Color", new ColorRGBA(0.5f,0.6f,0.4f,0));
        grass.setMaterial(matGrass);
        return grass;
    }
    public Spatial drawMine(){
        return assetManager.loadModel("main/resources/Models/Mine/Mine.j3o");
    }
    public Spatial drawFlag(){
        Spatial flag = assetManager.loadModel("main/resources/Models/Flag/Flag.j3o");
        Material matFlag = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matFlag.setColor("Color", ColorRGBA.Orange);
        flag.setMaterial(matFlag);
        return flag;
    }
    public Spatial drawNum(int row,int col){
        Spatial zero = assetManager.loadModel("main/resources/Models/Numbers/0.j3o");
        Spatial one = assetManager.loadModel("main/resources/Models/Numbers/1.j3o");
        Spatial two = assetManager.loadModel("main/resources/Models/Numbers/2.j3o");
        Spatial three = assetManager.loadModel("main/resources/Models/Numbers/3.j3o");
        Spatial four = assetManager.loadModel("main/resources/Models/Numbers/4.j3o");
        Spatial five = assetManager.loadModel("main/resources/Models/Numbers/5.j3o");
        Spatial six = assetManager.loadModel("main/resources/Models/Numbers/6.j3o");
        Spatial seven = assetManager.loadModel("main/resources/Models/Numbers/7.j3o");
        Spatial eight = assetManager.loadModel("main/resources/Models/Numbers/8.j3o");
        Spatial g = switch (mineField.getMine()[row][col]) {
            case 0 -> zero;
            case 1 -> one;
            case 2 -> two;
            case 3 -> three;
            case 4 -> four;
            case 5 -> five;
            case 6 -> six;
            case 7 -> seven;
            case 8 -> eight;
            default -> zero;
        };
        g.scale(6);
        g.rotate(-FastMath.PI / 2,0,0);

        return g;
    }

    //To load model according to the Status
    public Spatial draw(int row,int col){
        Spatial g = drawGrass();
        if (status[row][col].equals(Status.Covered_with_Mine)|status[row][col].equals(Status.Covered_without_Mine)){
            g=drawGrass();
        }
        if (status[row][col]==Status.Mine){
            g=drawMine();
        }
        if (status[row][col]==Status.Flag){g=drawFlag();g.scale(0.5f);}
        if (status[row][col]==Status.Clear){
            g=drawNum(row,col);
        }
        //设置位置
        g.scale(0.5f);
        g.center();
        float m=(float)(10.0*(row-5)+5.0);
        float n=(float)(10.0*(col-5)+5.0);
        g.setLocalTranslation(m,0.0f,n);

        return g;
    }
    //To reload each model
    public void reDraw(int row,int col) {
        this.enqueue (() -> {
            //do what you want
            rootNode.detachChild(temp[row][col]);
            temp[row][col]=draw(row,col);
            addListenerToSpatial(row,col);
            rootNode.attachChild(temp[row][col]);
        });
    }

    public void initHUD(){
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(1.2f*guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.Cyan);                             // font color
        hudText.setLocalTranslation(0, cam.getHeight()-hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }
    public void updateHUD(){
            guiNode.attachChild(hudText);
            hudText.setText(String.format(
                    """
                            Player 1:%s   Score:%d   Mistake:%d\s
                            Player 2:%s   Score:%d   Mistake:%d\s
                            Player On Turn:%s   Mines Left : %d\s"""
                    ,gameController.getP1().getUserName() , gameController.getP1().getScore() , gameController.getP1().getMistake()
                    ,gameController.getP2().getUserName() , gameController.getP2().getScore() , gameController.getP2().getMistake()
                    ,gameController.getOnTurnPlayer().getUserName(),gameController.getMine_Left()
            ));             // the text
            guiNode.attachChild(hudText);
    }


    public void makeExplosion(int row,int col){
        //set position
        float m=(float)(10.0*(row-5)+5.0);
        float n=(float)(10.0*(col-5)+5.0);
        //粒子效果
        ParticleEmitter fire =
                new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/flame.png"));
        fire.setMaterial(mat_red);
        fire.center();
        fire.setLocalTranslation(m,0.0f,n);
        fire.setImagesX(2);
        fire.setImagesY(2); // 2x2 texture animation
        fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fire.setStartSize(1.5f);
        fire.setEndSize(0.1f);
        fire.setGravity(0, 0, 0);
        fire.setLowLife(1f);
        fire.setHighLife(3f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);

        ParticleEmitter debris =
                new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
        Material debris_mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/Debris.png"));
        debris.setMaterial(debris_mat);
        debris.center();
        debris.setLocalTranslation(m,0.0f,n);
        debris.setImagesX(3);
        debris.setImagesY(3); // 3x3 texture animation
        debris.setRotateSpeed(4);
        debris.setSelectRandomImage(true);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
        debris.setStartColor(ColorRGBA.White);
        debris.setGravity(0, 6, 0);
        debris.getParticleInfluencer().setVelocityVariation(.60f);

        //Audio explosion 爆炸音效
        AudioNode explosion = new AudioNode(assetManager, "main/resources/Audio/Explosion.wav", AudioData.DataType.Buffer);
        explosion.setLooping(false);// 禁用循环播放
        explosion.setPositional(true);
        explosion.center();
        explosion.setLocalTranslation(m,0.0f,-n);
        explosion.setVolume(200);

        new Thread(() -> {
            //add the effect
            this.enqueue(() -> {
                rootNode.attachChild(fire);
                rootNode.attachChild(debris);
                rootNode.attachChild(explosion);
            });
        }).start();

        explosion.play();
        debris.emitAllParticles();
    }

    public void checkvictory(){
        if ((Math.max(gameController.getP1().getScore(),gameController.getP2().getScore()) - Math.min(gameController.getP1().getScore(),gameController.getP2().getScore())) > gameController.getMine_Left()){
            if (gameController.getP1().getScore() > gameController.p2.getScore()){
                makeVictory(gameController.p1, 1);
            }
            if (gameController.getP2().getScore() > gameController.getP1().getScore()){
                makeVictory(gameController.p2, 1);
            }
        }
        if (gameController.getMine_Left()==0){
            if (gameController.getP1().getScore() == gameController.getP2().getScore()){
                if (gameController.getP1().getMistake() < gameController.getP2().getMistake()){
                    makeVictory(gameController.p1, 1);
                }
                if (gameController.getP1().getMistake() > gameController.getP2().getMistake()){
                    makeVictory(gameController.p2, 1);
                }
                if (gameController.getP1().getMistake() == gameController.getP2().getMistake()){
                    makeVictory(null,0);
                }
            }
        }
    }
    public void makeVictory(Player winner,int situation){
        Container victoryFrame =new Container();
        victoryFrame.center();victoryFrame.scale(5);
        victoryFrame.setLocalTranslation((float)(0.5*cam.getWidth()+0.5*victoryFrame.getSize().getX()), (float)(0.5*cam.getHeight()+0.5*victoryFrame.getSize().getY()),0);
        if (situation==0){victoryFrame.addChild(new Label("Draw"));}
        if (situation==1){
            victoryFrame.addChild(new Label("Victory"));
            victoryFrame.addChild(new Label("The Winner Is "+winner.getUserName()));
        }

        AudioNode victorySound = new AudioNode(assetManager, "main/resources/Audio/Cyka Blyat.wav", AudioData.DataType.Buffer);
        victorySound.setLooping(true);
        victorySound.setPositional(false);
        victorySound.setVolume(3);

        new Thread(() -> {
            //add the effect
            this.enqueue(() -> {
                guiNode.attachChild(victoryFrame);
                rootNode.attachChild(victorySound);
            });
        }).start();

        victorySound.play();
    }

    public void addListenerToSpatial(int row,int col){
        MouseEventControl.addListenersToSpatial(temp[row][col],
                new DefaultMouseListener() {
                    @Override
                    protected void click(MouseButtonEvent event, Spatial target, Spatial capture ) {
                        if( event.getButtonIndex() == MouseInput.BUTTON_LEFT ) {onOpen(row,col);}
                        if( event.getButtonIndex() == MouseInput.BUTTON_RIGHT ) {onMark(row,col);}
                    }
                });
    }


    Container pauseWin;
    public void onPause(){
        if (gameController.isPaused){
            //继续计时，窗口消失
            guiNode.detachChild(pauseWin);

            gameController.isPaused=false;}
        else {
            //暂停计时，出现暂停窗口
            guiNode.attachChild(pauseWin);

            gameController.isPaused=true;}
    }
    public void onCheat(){
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                if (status[i][j].equals(Status.Covered_with_Mine)){status[i][j]=Status.Mine;}
                if (status[i][j].equals(Status.Covered_without_Mine)){status[i][j]=Status.Clear;}
                reDraw(i,j);
            }
        }
    }
    public void onOpen(int i,int j){
        //prevent getting mine at the first try
        if (status[i][j].equals(Status.Covered_with_Mine)|status[i][j].equals(Status.Covered_without_Mine)){
            System.out.printf("Opened the grid (%d,%d)\n",j+1,i+1);
        if (gameController.getOpenCount()==1){
            while (status[i][j].equals(Status.Covered_with_Mine)){mineField.resetMine();}
            if (status[i][j].equals(Status.Covered_without_Mine)){status[i][j]=Status.Clear;}
            gameController.addOpenCount();
        }else {
            if (status[i][j].equals(Status.Covered_with_Mine)){
                gameController.find_mine();
                gameController.getOnTurnPlayer().costScore();
                makeExplosion(i,j);
                status[i][j]=Status.Mine;
            }
            if (status[i][j].equals(Status.Covered_without_Mine)){status[i][j]=Status.Clear;}
            gameController.addOpenCount();
        }
        reDraw(i,j);
        checkvictory();
        gameController.nextTurn();
        updateHUD();
        }
    }
    public void onMark(int i,int j){
        if (status[i][j].equals(Status.Covered_with_Mine)|status[i][j].equals(Status.Covered_without_Mine)){
            System.out.printf("Marked the grid (%d,%d)\n",j+1,i+1);
        }
        if (status[i][j].equals(Status.Covered_with_Mine)){
            gameController.find_mine();
            status[i][j]=Status.Flag;
            gameController.getOnTurnPlayer().addScore();
        }
        if (status[i][j].equals(Status.Covered_without_Mine)){
            gameController.getOnTurnPlayer().addMistake();
            gameController.getOnTurnPlayer().costScore();
            status[i][j]=Status.Clear;
        }
        reDraw(i,j);
        checkvictory();
        gameController.nextTurn();
        updateHUD();
    }
    public void onRestart(){
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                if (status[i][j].equals(Status.Mine)){status[i][j]=Status.Covered_with_Mine;}
                if (status[i][j].equals(Status.Clear)){status[i][j]=Status.Covered_without_Mine;}
                if (status[i][j].equals(Status.Flag)){status[i][j]=Status.Covered_with_Mine;}
                reDraw(i,j);
            }
        }
        gameController.setMine_Left(mineField.getNumber());
        updateHUD();
    }
    public void onNewGame(){
        rootNode.detachAllChildren();
        simpleInitApp();
    }



    /*
        程序启动，提示用户选择功能
        录入（记录）功能：要求用户输入文件名，如果没有此文件，则创建新文件/如果有，则直接写入
        读取（阅读）功能：要求用户输入文件名，如果没有此文件，测提示找不到目标文件/如果有，则直接读取内容输出显示
         */
    public void readFileData(String fileName) {
        //todo: read date from file
        BufferedReader br = null;
        StringBuilder stringBuilder = new StringBuilder();
        //读取游戏性质（除雷区）
        String[] property = new String[10];
        try {
            br = new BufferedReader(new FileReader(fileName+".txt"));
            String contentLine = br.readLine();
            for (int i = 0; i < 10; i++){
                while (contentLine != null){
                    contentLine = br.readLine();
                    property[i]=contentLine;
                }
            }

            gameController.getP1().setUserName(property[0]);
            gameController.getP1().setScore(Integer.parseInt(property[1]));
            gameController.getP1().setMistake(Integer.parseInt(property[2]));
            gameController.getP2().setUserName(property[3]);
            gameController.getP2().setScore(Integer.parseInt(property[4]));
            gameController.getP2().setMistake(Integer.parseInt(property[5]));
            if (property[6].equals(gameController.getP1().getUserName())){
                gameController.onTurn = gameController.p1;
            }
            if (property[6].equals(gameController.getP2().getUserName())){
                gameController.onTurn = gameController.p2;
            }
            mineField=new MineGenerator(Integer.parseInt(property[7]),Integer.parseInt(property[8]),Integer.parseInt(property[9]));
            mineField.setMineField();
            //读取雷区
            int[]readMine=new int[mineField.getRow()*mineField.getCol()];
            for (int i = 0; i < mineField.getRow() * mineField.getCol(); i++){
                while (contentLine != null){
                    contentLine = br.readLine();
                    readMine[i]=Integer.parseInt(contentLine);
                }
            }

            for (int i = 0; i < mineField.getRow(); i++){
                for (int j = 0; j < mineField.getCol(); j++){
                    int data=readMine[mineField.getCol()*i+j];
                    if (data<0){
                        mineField.Mine[i][j] = -1;
                        if (data==-1){status[i][j]=Status.Covered_with_Mine;}
                        if (data==-2){status[i][j]=Status.Flag;}
                        if (data==-3){status[i][j]=Status.Mine;}
                    }
                    if (data>10){mineField.Mine[i][j] = data-10;status[i][j]=Status.Clear;}
                    else {mineField.Mine[i][j] = data;status[i][j]=Status.Covered_without_Mine;}
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeDataToFile(String fileName){
        //todo: write data into file
        try {
            File f = new File(fileName+".txt");
            FileWriter fw = new FileWriter(f,true);
            fw.write(gameController.getP1().getUserName()+"\n");
            fw.write(gameController.getP1().getScore()+"\n");
            fw.write(gameController.getP1().getMistake()+"\n");
            fw.write(gameController.getP2().getUserName()+"\n");
            fw.write(gameController.getP2().getScore()+"\n");
            fw.write(gameController.getP2().getMistake()+"\n");
            fw.write(gameController.getOnTurnPlayer().getUserName()+"\n");
            fw.write(mineField.getRow()+"\n");
            fw.write(mineField.getCol()+"\n");
            fw.write(mineField.getNumber()+"\n");

            int[][] data = new int[mineField.getRow()][mineField.getCol()];
            for (int i = 0; i < mineField.getRow(); i++){
                for (int j = 0; j < mineField.getCol(); j++){
                    if (status[i][j] == Status.Covered_with_Mine) {
                        data[i][j] = -1;
                    }
                    if (status[i][j] == Status.Covered_without_Mine) {
                        data[i][j] = mineField.getMine()[i][j];
                    }
                    if (status[i][j] == Status.Flag) {
                        data[i][j] = -2;
                    }
                    if (status[i][j] == Status.Clear) {
                        data[i][j] = mineField.getMine()[i][j] + 10;
                    }
                    if (status[i][j] == Status.Mine) {
                        data[i][j] = -3;
                    }
                }
            }

            for (int i = 0; i < mineField.getRow(); i++){
                for (int j = 0; j < mineField.getCol(); j++){
                    fw.write(data[i][j] + "\n");
                }
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}