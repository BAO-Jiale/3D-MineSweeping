import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.lwjgl.LwjglAbstractDisplay;
import com.jme3.util.SkyFactory;
import org.lwjgl.LWJGLException;

public class MainFrame extends SimpleApplication {
    public static GameController gameController=new GameController(new Player(),new Player());
    public static MineGenerator mineField=new MineGenerator("Junior");
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

        initKeys();
        cross=makeCross();
        cam.setLocation(new Vector3f(0,0,50.0f));
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(50.0f);


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


    }






    public void draw(Spatial g,int row,int col){
        Spatial grass = assetManager.loadModel("main/resources/Models/Grass/Grass.gltf");
        Spatial mine = assetManager.loadModel("main/resources/Models/Mine/Mine.gltf");
        if (status[row][col].equals(Status.Covered_with_Mine)|status[row][col].equals(Status.Covered_without_Mine)){
            g=grass;
        }
        if (status[row][col]==Status.Mine){
            g=mine;
        }
        if (status[row][col]==Status.Flag){}
        if (status[row][col]==Status.Clear){}
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


    private final ActionListener listener = new ActionListener() {

        //通过射线检测判断操作的是哪个格子
        public int[] getGrid(){
            // 1. 重置结果列表.
            CollisionResults results = new CollisionResults();
            // 2. 从摄像机位置向摄像机朝向画一条射线来瞄准.
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            // 3. 在结果列表里查找和射线相交的可被射击的对象。
            shootables.collideWith(ray, results);
            // 4. 打印结果
            System.out.println("----- Collisions? " + results.size() + "-----");
            for (int i = 0; i < results.size(); i++) {
                // 每个被射击到的我们可以知道距离，落点，对象名称。
                float dist = results.getCollision(i).getDistance();//距离
                Vector3f pt = results.getCollision(i).getContactPoint();//射击落点位置
                String hit = results.getCollision(i).getGeometry().getName();//射击到的物体
                System.out.println("* Collision #" + i);
                System.out.println(" You shot " + hit + " at " + pt + ", " + dist + " wu away.");
            }
            // 5. 结果的使用 (we mark the hit object)
            if (results.size() > 0){
                // 最近的碰撞点 :
                int [] cor=new int[2];
                CollisionResult closest = results.getClosestCollision();
                for (int i =0;i< mineField.getRow();i++){
                    for (int j=0;j< mineField.getCol();j++){
                        if (temp[i][j].equals(closest.getGeometry())){
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
                onOpen(temp[0],temp[1]);
            }
            if (name.equals("Mark") && !keyPressed) {
                int[] temp = getGrid();
                onMark(temp[0],temp[1]);
            }
            if (name.equals("Restart") && !keyPressed) {
                onRestart();
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
        if (status[i][j]==Status.Covered_with_Mine){status[i][j]=Status.Mine;}
        if (status[i][j]==Status.Covered_without_Mine){status[i][j]=Status.Clear;}
        rootNode.detachChild(temp[i][j]);
        draw(temp[i][j],i,j);
        gameController.nextTurn();
    }
    public void onMark(int i,int j){
        if (status[i][j]==Status.Covered_with_Mine){status[i][j]=Status.Flag;}
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

    }




}