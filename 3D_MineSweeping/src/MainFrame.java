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


public class MainFrame extends SimpleApplication {
    public static GameController gameController=new GameController(new Player(),new Player());
    public static MineGenerator mineField=new MineGenerator("Professional");
    public Spatial[][]temp=new Spatial[mineField.getRow()][mineField.getCol()];
    public Status [][]status=mineField.getMineField();

    private TextField textField;
    private DocumentModel document;

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
        initHUD();
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

        // 添加一个Button控件
        Button loadBtn = mainWindow.addChild(new Button("Save & Load"));
        Button patternBtn = mainWindow.addChild(new Button("Pattern"));
        Button introductionBtn = mainWindow.addChild(new Button("Intro"));

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
            textField = loadWin.addChild(new TextField("Input your file name here"));
            textField.setSingleLine(false);
            document = textField.getDocumentModel();

            // Setup some preferred sizing since this will be the primary
            // element in our GUI
            textField.center();textField.setLocalTranslation((float)0.5*cam.getWidth(), (float)0.5*cam.getHeight(), 0);
            textField.setPreferredWidth((float)0.25*cam.getWidth());
            textField.setPreferredLineCount(10);

            String fileName = textField.getText();
            System.out.println("fileName :"+fileName);

            save.addClickCommands(source1 -> {gameController.writeDataToFile(fileName);});
            load.addClickCommands(source1 -> {gameController.readFileData(fileName);initScene();});


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
                mineField=new MineGenerator("Junior");
                guiNode.detachChild(modeWin);
                initScene();initHUD();
            });
            medium.addClickCommands(source1 -> {
                mineField=new MineGenerator("Senior");
                guiNode.detachChild(modeWin);
                initScene();initHUD();
            });
            hard.addClickCommands(source1 -> {
                mineField=new MineGenerator("Professional");
                guiNode.detachChild(modeWin);
                initScene();initHUD();
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
                    mineField=new MineGenerator(temp[0],temp[1],temp[2]);
                    guiNode.detachChild(modeWin);
                    initScene();initHUD();
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
                                 a. 如果双方的分数差距大于游戏区中未揭晓的雷数，则直接判定优势方获胜
                                 b. 如果在游戏中所有雷都被揭晓时双方分数依然相同，则失误数少的一方（失误包含误触雷以及标记错误）获胜
                                 c. 如果失误数依然相同，则双方平局。"""
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

    }


    private void initScene(){
        initHUD();initAudio();
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
        audioNature.setVolume(3);// 音量
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
        Spatial g ;
        Spatial zero = assetManager.loadModel("main/resources/Models/Numbers/0.j3o");
        Spatial one = assetManager.loadModel("main/resources/Models/Numbers/1.j3o");
        Spatial two = assetManager.loadModel("main/resources/Models/Numbers/2.j3o");
        Spatial three = assetManager.loadModel("main/resources/Models/Numbers/3.j3o");
        Spatial four = assetManager.loadModel("main/resources/Models/Numbers/4.j3o");
        Spatial five = assetManager.loadModel("main/resources/Models/Numbers/5.j3o");
        Spatial six = assetManager.loadModel("main/resources/Models/Numbers/6.j3o");
        Spatial seven = assetManager.loadModel("main/resources/Models/Numbers/7.j3o");
        Spatial eight = assetManager.loadModel("main/resources/Models/Numbers/8.j3o");
        switch (mineField.getMine()[row][col]){
            case 0:g=zero;break;
            case 1:g=one;break;
            case 2:g=two;break;
            case 3:g=three;break;
            case 4:g=four;break;
            case 5:g=five;break;
            case 6:g=six;break;
            case 7:g=seven;break;
            case 8:g=eight;
            default:g=zero;
        }
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
        if (status[row][col]==Status.Flag){g=drawFlag();g.scale(0.25f);}
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
            rootNode.attachChild(temp[row][col]);
        });
    }

    public void initHUD(){
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(1.2f*guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.Cyan);                             // font color
        hudText.setText(String.format(
                """
                        Player 1:%s   Score:%d   Mistake:%d\s
                        Player 2:%s   Score:%d   Mistake:%d\s
                        Player On Turn:%s   Mines Left : %d\s"""
                ,gameController.getP1().getUserName() , gameController.getP1().getScore() , gameController.getP1().getMistake()
                ,gameController.getP2().getUserName() , gameController.getP2().getScore() , gameController.getP2().getMistake()
                ,gameController.getOnTurnPlayer().getUserName(),gameController.getMine_Left()
        ));             // the text
        hudText.setLocalTranslation(0, cam.getHeight()-hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }
    private void makeCross() {
        // 采用Gui的默认字体，做个加号当准星。
        BitmapText text = guiFont.createLabel("+");
        text.setColor(ColorRGBA.Green);// 绿色

        // 居中
        float x = (cam.getWidth() - text.getLineWidth()) * 0.5f;
        float y = (cam.getHeight() + text.getLineHeight()) * 0.5f;
        text.setLocalTranslation(x, y, 0);
        text.scale(2.0f);

        guiNode.attachChild(text);

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
        debris.setLocalTranslation(m,0.0f,-n);
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
        explosion.setPositional(true);// 设置为非定位音源，玩家无法通过耳机辨别音源的位置。常用于背景音乐。
        explosion.center();
        explosion.setLocalTranslation(m,0.0f,-n);
        explosion.setVolume(2);

        new Thread(() -> {
            //add the effect
            this.enqueue(() -> {
                rootNode.attachChild(fire);
                rootNode.attachChild(debris);
                rootNode.attachChild(explosion);
            });
        }).start();

        debris.emitAllParticles();
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


    public void onPause(){
        Container pauseWin=new Container();
        pauseWin.addChild(new Label("The Game Is Paused"));
        pauseWin.center();pauseWin.scale(2);
        pauseWin.setLocalTranslation((float)0.5*cam.getWidth(), (float)0.5*cam.getHeight(), 0);
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
        gameController.nextTurn();}
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
            status[i][j]=Status.Clear;
        }
        reDraw(i,j);
        gameController.nextTurn();
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
    }
    public void onNewGame(){
        simpleInitApp();
    }







}