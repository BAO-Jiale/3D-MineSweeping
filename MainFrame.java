import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.lwjgl.LwjglKeyInput;
import com.jme3.input.lwjgl.LwjglMouseInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.lwjgl.LwjglAbstractDisplay;
import com.jme3.util.SkyFactory;
import org.lwjgl.LWJGLException;


public class MainFrame extends SimpleApplication {
    public static GameController gameController=new GameController(new Player(),new Player());
    public static MineGenerator mineField=new MineGenerator("Professional");
    public Spatial[][]temp=new Spatial[mineField.getRow()][mineField.getCol()];
    public Status [][]status=mineField.getMineField();
    private Spatial cross;
    Node shootables;


    @Override
    public void simpleInitApp() {
        /* Initialize the game scene here */
        //建背景
        Spatial sky = SkyFactory.createSky(assetManager,
                "Textures/Sky/Bright/BrightSky.dds", // 贴图路径
                SkyFactory.EnvMapType.CubeMap);// 贴图类型
        rootNode.attachChild(sky);
        // 初始化滤镜处理器
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        // 添加雾化滤镜
        FogFilter fogFilter = new FogFilter(ColorRGBA.White, 0.5f, 100f);
        fpp.addFilter(fogFilter);

        initHUD();
        initKeys();
        cross=makeCross();

        cam.setLocation(new Vector3f(0,0,50.0f));
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(50.0f);
        flyCam.setZoomSpeed(15.0f);

        inputManager.addListener(listener);


        shootables =new Node("Shootables");
        rootNode.attachChild(shootables);

        //建模型
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                draw(temp[i][j],i,j);
            }
        }


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
    }

    @Override
    public void simpleRender(RenderManager rm) {
        /* (optional) Make advanced modifications to frameBuffer and scene graph. */
    }

    LwjglAbstractDisplay e=new LwjglAbstractDisplay() {
        @Override
        public Type getType() {
            return null;
        }

        @Override
        public void setTitle(String s) {

        }

        @Override
        public void restart() {

    }

        @Override
        protected void createContext(AppSettings appSettings) throws LWJGLException {

        }

        @Override
        protected void destroyContext() {

        }

        @Override
        public void create(boolean b) {

        }
    };
    InputManager inputManager=new InputManager(new LwjglMouseInput(e),new LwjglKeyInput(e),null,null);
    public void initKeys(){
        // 你可以给一个或多个事件指定同样的事件名称
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Cheat", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Open", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Mark", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("NewGame", new KeyTrigger(KeyInput.KEY_N));


        inputManager.addListener(listener);
    }






    public void draw(Spatial g,int row,int col){
        Spatial grass = assetManager.loadModel("main/resources/Models/Grass/Grass.j3o");
        Material matGrass = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matGrass.setColor("Color", new ColorRGBA(0.5f,0.6f,0.4f,0));
        grass.setMaterial(matGrass);

        Spatial mine = assetManager.loadModel("main/resources/Models/Mine/Mine.j3o");

        Spatial flag = assetManager.loadModel("main/resources/Models/Flag/Flag.j3o");
        Material matFlag = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matFlag.setColor("Color", ColorRGBA.Orange);
        flag.setMaterial(matFlag);

        Spatial zero = assetManager.loadModel("main/resources/Models/Numbers/0.j3o");
        Spatial one = assetManager.loadModel("main/resources/Models/Numbers/1.j3o");
        Spatial two = assetManager.loadModel("main/resources/Models/Numbers/2.j3o");
        Spatial three = assetManager.loadModel("main/resources/Models/Numbers/3.j3o");
        Spatial four = assetManager.loadModel("main/resources/Models/Numbers/4.j3o");
        Spatial five = assetManager.loadModel("main/resources/Models/Numbers/5.j3o");
        Spatial six = assetManager.loadModel("main/resources/Models/Numbers/6.j3o");
        Spatial seven = assetManager.loadModel("main/resources/Models/Numbers/7.j3o");
        Spatial eight = assetManager.loadModel("main/resources/Models/Numbers/8.j3o");

        if (status[row][col].equals(Status.Covered_with_Mine)|status[row][col].equals(Status.Covered_without_Mine)){
            g=grass;
        }
        if (status[row][col]==Status.Mine){
            g=mine;
        }
        if (status[row][col]==Status.Flag){g=flag;g.scale(0.25f);}
        if (status[row][col]==Status.Clear){
            switch (mineField.getMine()[row][col]){
                case 0:g=zero;
                case 1:g=one;
                case 2:g=two;
                case 3:g=three;
                case 4:g=four;
                case 5:g=five;
                case 6:g=six;
                case 7:g=seven;
                case 8:g=eight;
            }
            g.scale(6);
            g.rotate(-FastMath.PI / 2,0,0);
        }
        //设置位置
        g.scale(0.5f);
        g.center();
        float m=(float)(10.0*(row-5)+5.0);
        float n=(float)(10.0*(col-5)+5.0);
        g.setLocalTranslation(m,0.0f,-n);

        shootables.attachChild(g);
    }

    private Spatial makeCross() {
        // 采用Gui的默认字体，做个加号当准星。
        BitmapText text = guiFont.createLabel("+");
        text.setColor(ColorRGBA.Green);// 绿色

        // 居中
        float x = (cam.getWidth() - text.getLineWidth()) * 0.5f;
        float y = (cam.getHeight() + text.getLineHeight()) * 0.5f;
        text.setLocalTranslation(x, y, 0);
        text.scale(2.0f);

        guiNode.attachChild(text);

        return text;
    }
    public void makeExplosion(){
        ParticleEmitter fire =
                new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        mat_red.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/flame.png"));
        fire.setMaterial(mat_red);
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
        rootNode.attachChild(fire);

        ParticleEmitter debris =
                new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
        Material debris_mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", assetManager.loadTexture(
                "Effects/Explosion/Debris.png"));
        debris.setMaterial(debris_mat);
        debris.setImagesX(3);
        debris.setImagesY(3); // 3x3 texture animation
        debris.setRotateSpeed(4);
        debris.setSelectRandomImage(true);
        debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 4, 0));
        debris.setStartColor(ColorRGBA.White);
        debris.setGravity(0, 6, 0);
        debris.getParticleInfluencer().setVelocityVariation(.60f);
        rootNode.attachChild(debris);
        debris.emitAllParticles();
    }


    public void initHUD(){
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(1.2f*guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.Cyan);                             // font color
        hudText.setText(String.format(
                "Player 1:%s   Score:%d   Mistake:%d " +"\n"+
                "Player 2:%s   Score:%d   Mistake:%d " +"\n"+
                "Player On Turn:%s   Mines Left : %d "
                ,gameController.getP1().getUserName() , gameController.getP1().getScore() , gameController.getP1().getMistake()
                ,gameController.getP2().getUserName() , gameController.getP2().getScore() , gameController.getP2().getMistake()
                ,gameController.getOnTurnPlayer().getUserName(),gameController.getMine_Left()
        ));             // the text
        hudText.setLocalTranslation(0, cam.getHeight()-hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);
    }


    private final ActionListener listener = new ActionListener() {

        //通过射线检测判断操作的是哪个格子
        public int[] getGrid(){
            // 1. 重置结果列表.
            CollisionResults results = new CollisionResults();
            // 2. 从摄像机位置向摄像机朝向画一条射线来瞄准.
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            // 3. 在结果列表里查找和射线相交的可被射击的对象。
            shootables.collideWith(ray, results);
            // 4. 结果的使用 (we mark the hit object)
            if (results.size() > 0){
                // 最近的碰撞点 :
                int [] cor=new int[2];
                CollisionResult closest = results.getClosestCollision();
                for (int i =0;i< mineField.getRow();i++){
                    for (int j=0;j< mineField.getCol();j++){
                        if (temp[i][j].getLocalTransform().equals(closest.getGeometry().getLocalTransform())){
                            cor[0]=i;cor[1]=j;
                        }
                    }
                }
                return cor;
            }
            else {return null;}
        }

        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                onPause();
            }
            if (name.equals("Open") && !keyPressed) {
                int[] temp = getGrid();
                System.out.println(String.format("The (%d,%d) has opened",temp[0],temp[1]));
                onOpen(temp[0],temp[1]);
            }
            if (name.equals("Mark") && !keyPressed) {
                int[] temp = getGrid();
                System.out.println(String.format("The (%d,%d) has marked",temp[0],temp[1]));
                onMark(temp[0],temp[1]);
            }
            if (name.equals("Restart") && !keyPressed) {
                onRestart();
            }
            if (name.equals("NewGame") && !keyPressed) {
                onNewGame();
            }
        }
    };
    public void onPause(){
        if (gameController.isPaused){
            //继续计时，窗口消失


            gameController.isPaused=false;}
        if (!gameController.isPaused){
            //暂停计时，出现暂停窗口


            gameController.isPaused=true;}
    }
    public void onCheat(){
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                if (status[i][j]==Status.Covered_with_Mine){status[i][j]=Status.Mine;}
                if (status[i][j]==Status.Covered_without_Mine){status[i][j]=Status.Clear;}
                draw(temp[i][j],i,j);
            }
        }
    }
    public void onOpen(int i,int j){
        //防止
        if (gameController.getOpenCount()==1){
            while (status[i][j]==Status.Covered_with_Mine){mineField.resetMine();}
        }
        if (status[i][j]==Status.Covered_with_Mine){gameController.find_mine();status[i][j]=Status.Mine;makeExplosion();}
        if (status[i][j]==Status.Covered_without_Mine){status[i][j]=Status.Clear;}

        rootNode.detachChild(temp[i][j]);
        draw(temp[i][j],i,j);
        gameController.nextTurn();
    }
    public void onMark(int i,int j){
        if (status[i][j]==Status.Covered_with_Mine){gameController.find_mine();status[i][j]=Status.Flag;}
        if (status[i][j]==Status.Covered_without_Mine){status[i][j]=Status.Flag;}
        draw(temp[i][j],i,j);
        gameController.nextTurn();
    }
    public void onRestart(){
        for (int i =0;i< mineField.getRow();i++){
            for (int j=0;j< mineField.getCol();j++){
                if (status[i][j]==Status.Mine){status[i][j]=Status.Covered_with_Mine;}
                if (status[i][j]==Status.Clear){status[i][j]=Status.Covered_without_Mine;}
                if (status[i][j]==Status.Flag){
                    if (mineField.getMine()[i][j]==-1){status[i][j]=Status.Covered_with_Mine;}
                    else {status[i][j]=Status.Covered_without_Mine;}
            }
                draw(temp[i][j],i,j);
            }
        }
        gameController.setMine_Left(mineField.getNumber());


    }
    public void onNewGame(){
        mineField=new MineGenerator();
        simpleInitApp();
    }












}